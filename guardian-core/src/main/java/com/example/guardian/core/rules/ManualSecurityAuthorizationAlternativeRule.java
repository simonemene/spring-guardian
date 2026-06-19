package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Detects manual authorization checks that should move to Spring Security authorization mechanisms.
 *
 * @author p15518 - Simone Meneghetti
 */
final class ManualSecurityAuthorizationAlternativeRule implements SpringRule {

    enum Mode {
        PRINCIPAL_NULL,
        SECURITY_CONTEXT_IN_BUSINESS_CODE,
        ROLE_STRING_CHECK
    }

    private static final int MAX_FINDINGS = 40;

    private final Mode mode;

    ManualSecurityAuthorizationAlternativeRule(Mode mode) {
        this.mode = mode;
    }

    @Override
    public String id() {
        return switch (mode) {
            case PRINCIPAL_NULL -> "SPR_ALT021_MANUAL_PRINCIPAL_SECURITY_CHECK";
            case SECURITY_CONTEXT_IN_BUSINESS_CODE -> "SPR_ALT022_SECURITY_CONTEXT_HOLDER_IN_BUSINESS_CODE";
            case ROLE_STRING_CHECK -> "SPR_ALT023_MANUAL_ROLE_STRING_CHECK";
        };
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            inspect(file, findings);
            if (findings.size() >= MAX_FINDINGS) {
                break;
            }
        }
        return findings;
    }

    private void inspect(JavaSourceFile file, List<Finding> findings) {
        String content = file.content() == null ? "" : file.content();
        String lowerContent = content.toLowerCase(Locale.ROOT);
        if (!hasSecuritySignal(lowerContent)) {
            return;
        }
        if ((mode == Mode.SECURITY_CONTEXT_IN_BUSINESS_CODE || mode == Mode.ROLE_STRING_CHECK) && isSecurityAdapter(file)) {
            return;
        }

        String[] lines = content.split("\\R", -1);
        for (int i = 0; i < lines.length && findings.size() < MAX_FINDINGS; i++) {
            String line = lines[i];
            if (ignoredLine(line)) {
                continue;
            }
            if (!matches(line, lowerContent)) {
                continue;
            }
            findings.add(new Finding(
                    id(),
                    Severity.INFO,
                    title(),
                    file.relativePath(),
                    i + 1,
                    compact(line),
                    whyItMatters(),
                    suggestedFix()
            ));
        }
    }

    private boolean hasSecuritySignal(String lowerContent) {
        return lowerContent.contains("java.security.principal")
                || lowerContent.contains("getuserprincipal(")
                || lowerContent.contains("org.springframework.security")
                || lowerContent.contains("authentication")
                || lowerContent.contains("principal")
                || lowerContent.contains("role_");
    }

    private boolean matches(String line, String lowerContent) {
        String lowerLine = line.toLowerCase(Locale.ROOT);
        return switch (mode) {
            case PRINCIPAL_NULL -> principalNullCheck(lowerLine, lowerContent);
            case SECURITY_CONTEXT_IN_BUSINESS_CODE -> lowerLine.contains("securitycontextholder.getcontext()");
            case ROLE_STRING_CHECK -> manualRoleStringCheck(line, lowerLine);
        };
    }

    private boolean principalNullCheck(String lowerLine, String lowerContent) {
        return lowerLine.contains("getuserprincipal() != null")
                || lowerLine.contains("getuserprincipal()!=null")
                || lowerLine.contains("null != request.getuserprincipal()")
                || lowerLine.contains("null!=request.getuserprincipal()")
                || Pattern.compile("\\bprincipal\\s*!=\\s*null\\b").matcher(lowerLine).find()
                || Pattern.compile("\\bnull\\s*!=\\s*principal\\b").matcher(lowerLine).find()
                || (lowerContent.contains("org.springframework.security.core.authentication")
                && Pattern.compile("\\bauthentication\\s*!=\\s*null\\b").matcher(lowerLine).find());
    }

    private boolean manualRoleStringCheck(String line, String lowerLine) {
        String trimmed = line.strip();
        if (trimmed.startsWith("@PreAuthorize") || trimmed.startsWith("@PostAuthorize")) {
            return false;
        }
        return (lowerLine.contains("getauthorities()") && lowerLine.contains("role_"))
                || lowerLine.contains(".contains(\"role_")
                || lowerLine.contains(".equals(\"role_")
                || lowerLine.contains(".equalsignorecase(\"role_")
                || lowerLine.contains("hasrole(\"")
                || lowerLine.contains("hasauthority(\"");
    }

    private boolean isSecurityAdapter(JavaSourceFile file) {
        String normalizedPath = file.relativePath() == null ? "" : file.relativePath().replace("\\", "/").toLowerCase(Locale.ROOT);
        String lowerContent = file.content() == null ? "" : file.content().toLowerCase(Locale.ROOT);
        return normalizedPath.contains("/security/")
                || normalizedPath.contains("/config/")
                || normalizedPath.endsWith("securityconfig.java")
                || lowerContent.contains("@enablewebsecurity")
                || lowerContent.contains("securityfilterchain");
    }

    private boolean ignoredLine(String line) {
        if (line == null) {
            return true;
        }
        String trimmed = line.strip();
        return trimmed.isEmpty()
                || trimmed.startsWith("//")
                || trimmed.startsWith("*")
                || trimmed.startsWith("/*")
                || trimmed.startsWith("import ");
    }

    private String title() {
        return switch (mode) {
            case PRINCIPAL_NULL -> "Manual Principal null authorization check";
            case SECURITY_CONTEXT_IN_BUSINESS_CODE -> "SecurityContextHolder used in business code";
            case ROLE_STRING_CHECK -> "Manual role string authorization check";
        };
    }

    private String whyItMatters() {
        return switch (mode) {
            case PRINCIPAL_NULL -> "Checking Principal or Authentication for null inside application code is a weak authorization boundary and can diverge from Spring Security rules.";
            case SECURITY_CONTEXT_IN_BUSINESS_CODE -> "Reading SecurityContextHolder from service or domain code couples business logic to web security infrastructure and makes tests and authorization ownership unclear.";
            case ROLE_STRING_CHECK -> "Manual ROLE_* string checks duplicate authorization rules and can drift from SecurityFilterChain or method-security policies.";
        };
    }

    private String suggestedFix() {
        return switch (mode) {
            case PRINCIPAL_NULL -> "Move authorization to SecurityFilterChain, @PreAuthorize or a dedicated authorization service; inject @AuthenticationPrincipal only when the identity is business input.";
            case SECURITY_CONTEXT_IN_BUSINESS_CODE -> "Keep SecurityContextHolder in security/web adapters and pass an explicit authenticated user or authorization decision into the application service.";
            case ROLE_STRING_CHECK -> "Use @PreAuthorize, AuthorizationManager, SecurityFilterChain matchers or a typed domain permission service instead of scattered ROLE_* string comparisons.";
        };
    }

    private String compact(String line) {
        String compact = line == null ? "" : line.strip();
        if (compact.length() <= 220) {
            return compact;
        }
        return compact.substring(0, 217) + "...";
    }
}
