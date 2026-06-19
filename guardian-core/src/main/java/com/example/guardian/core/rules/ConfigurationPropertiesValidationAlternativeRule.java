package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects @ConfigurationProperties classes and records without Bean Validation activation.
 *
 * @author p15518 - Simone Meneghetti
 */
final class ConfigurationPropertiesValidationAlternativeRule implements SpringRule {

    private static final int VALIDATED_LOOKBACK_LINES = 3;
    private static final int TYPE_DECLARATION_LOOKAHEAD_LINES = 8;

    @Override
    public String id() {
        return "SPR_ALT018_CONFIGURATION_PROPERTIES_WITHOUT_VALIDATION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            Set<String> reportedTypeNames = new HashSet<>();

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (AstUtils.hasAnnotation(clazz, "ConfigurationProperties") && !AstUtils.hasAnnotation(clazz, "Validated")) {
                    reportedTypeNames.add(clazz.getNameAsString());
                    findings.add(new Finding(
                            id(),
                            Severity.INFO,
                            "@ConfigurationProperties without validation",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Type " + clazz.getNameAsString() + " is bound with @ConfigurationProperties but has no @Validated.",
                            "Configuration binding without validation can fail late, after the application has started or when the value is first used.",
                            "Add @Validated and Bean Validation constraints to required fields or record components."
                    ));
                }
            }

            for (RecordDeclaration record : file.compilationUnit().findAll(RecordDeclaration.class)) {
                if (AstUtils.hasAnnotation(record, "ConfigurationProperties") && !AstUtils.hasAnnotation(record, "Validated")) {
                    reportedTypeNames.add(record.getNameAsString());
                    findings.add(new Finding(
                            id(),
                            Severity.INFO,
                            "@ConfigurationProperties record without validation",
                            file.relativePath(),
                            AstUtils.line(record).orElse(null),
                            "Record " + record.getNameAsString() + " is bound with @ConfigurationProperties but has no @Validated.",
                            "Configuration records without validation can accept missing or malformed values until runtime behavior exposes the issue.",
                            "Add @Validated and Bean Validation constraints such as @NotBlank, @Positive or @DurationMin."
                    ));
                }
            }

            addTextFallbackFindings(file, findings, reportedTypeNames);
        }

        return findings;
    }

    private void addTextFallbackFindings(JavaSourceFile file, List<Finding> findings, Set<String> reportedTypeNames) {
        if (!file.content().contains("@ConfigurationProperties")) {
            return;
        }

        List<String> lines = file.content().lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).contains("@ConfigurationProperties")) {
                continue;
            }

            int declarationIndex = findTypeDeclarationIndex(lines, i);
            if (declarationIndex < 0 || hasValidatedNearConfigurationProperties(lines, i, declarationIndex)) {
                continue;
            }

            String typeName = extractTypeName(lines.get(declarationIndex));
            if (!reportedTypeNames.add(typeName)) {
                continue;
            }

            findings.add(new Finding(
                    id(),
                    Severity.INFO,
                    "@ConfigurationProperties without validation",
                    file.relativePath(),
                    i + 1,
                    "Type " + typeName + " is bound with @ConfigurationProperties but has no @Validated.",
                    "Configuration binding without validation can fail late, after the application has started or when the value is first used.",
                    "Add @Validated and Bean Validation constraints to required fields or record components."
            ));
        }
    }

    private int findTypeDeclarationIndex(List<String> lines, int configurationPropertiesIndex) {
        int upperBound = Math.min(lines.size() - 1, configurationPropertiesIndex + TYPE_DECLARATION_LOOKAHEAD_LINES);
        for (int i = configurationPropertiesIndex; i <= upperBound; i++) {
            String normalized = lines.get(i).strip();
            if (isTypeDeclarationLine(normalized)) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasValidatedNearConfigurationProperties(List<String> lines, int configurationPropertiesIndex, int declarationIndex) {
        int lowerBound = Math.max(0, configurationPropertiesIndex - VALIDATED_LOOKBACK_LINES);
        for (int i = lowerBound; i <= declarationIndex; i++) {
            if (lines.get(i).contains("@Validated")) {
                return true;
            }
        }
        return false;
    }

    private boolean isTypeDeclarationLine(String line) {
        return line.contains(" class ")
                || line.startsWith("class ")
                || line.contains(" record ")
                || line.startsWith("record ")
                || line.contains(" interface ")
                || line.startsWith("interface ");
    }

    private String extractTypeName(String declarationLine) {
        String[] tokens = declarationLine
                .replace("{", " ")
                .replace("(", " ")
                .split("[^A-Za-z0-9_$]+");

        for (int i = 0; i < tokens.length - 1; i++) {
            if ("class".equals(tokens[i]) || "record".equals(tokens[i]) || "interface".equals(tokens[i])) {
                return tokens[i + 1];
            }
        }

        return "configuration properties type";
    }
}
