package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.ReleaseTarget;
import com.example.guardian.core.model.Severity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Detects runtime configuration values committed inside packaged Spring configuration files.
 *
 * @author Simone Meneghetti
 */
public class ApplicationPropertiesExternalizationRule implements SpringRule {

    private static final Pattern RUNTIME_KEY = Pattern.compile(
            "(^|[.\\-_:])(url|uri|host|port|username|password|secret|token|api[-_]?key|client[-_]?secret|keystore|truststore|datasource|jdbc|kafka|rabbitmq|redis|mail|smtp|cors|allowed[-_]?origins|cron|schedule|fixed[-_]?delay|fixed[-_]?rate|directory|folder|path|endpoint)([.\\-_:]|$)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String id() {
        return "SPR091_APPLICATION_PROPERTIES_SHOULD_BE_EXTERNALIZED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (Path file : applicationConfigFiles(context.root())) {
            inspectFile(context, file, findings);
        }
        return findings;
    }

    private void inspectFile(ProjectScanContext context, Path file, List<Finding> findings) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            String fileName = file.getFileName().toString().toLowerCase(Locale.ROOT);
            boolean mainApplicationFile = fileName.equals("application.properties") || fileName.equals("application.yml") || fileName.equals("application.yaml");
            boolean productionFile = fileName.contains("prod") || fileName.contains("production");
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank() || line.startsWith("#") || line.startsWith("---")) {
                    continue;
                }
                ConfigLine configLine = parse(line);
                if (configLine == null || !RUNTIME_KEY.matcher(configLine.key()).find() || isExternalized(configLine.value())) {
                    continue;
                }
                Severity severity = productionFile || context.profile().releaseTarget() == ReleaseTarget.PRODUCTION ? Severity.MAJOR : Severity.MINOR;
                if (mainApplicationFile || productionFile || severity == Severity.MAJOR) {
                    findings.add(new Finding(
                            id(),
                            severity,
                            "Packaged Spring configuration should be externalized",
                            context.root().relativize(file).toString(),
                            i + 1,
                            "Property " + configLine.key() + " has packaged value " + mask(configLine.value()) + ".",
                            "Runtime values in application.properties or application.yml make the artifact environment-specific and increase release risk.",
                            "Keep packaged configuration as placeholders/defaults and pass environment-specific values with environment variables, mounted config, Kubernetes ConfigMap/Secret, Vault or the deployment platform."
                    ));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private List<Path> applicationConfigFiles(Path root) {
        try (var stream = Files.walk(root)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        String normalized = path.toString().replace("\\", "/");
                        return !normalized.contains("/target/")
                                && !normalized.contains("/build/")
                                && normalized.contains("/src/main/resources/")
                                && name.startsWith("application")
                                && (name.endsWith(".properties") || name.endsWith(".yml") || name.endsWith(".yaml"));
                    })
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private ConfigLine parse(String line) {
        int separator = line.indexOf('=');
        if (separator < 0) {
            separator = line.indexOf(':');
        }
        if (separator < 1 || separator >= line.length() - 1) {
            return null;
        }
        String key = line.substring(0, separator).trim();
        String value = line.substring(separator + 1).trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }
        if (value.startsWith("'") && value.endsWith("'") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }
        return key.isBlank() || value.isBlank() ? null : new ConfigLine(key, value);
    }

    private boolean isExternalized(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return value.contains("${")
                || lower.equals("true")
                || lower.equals("false")
                || lower.equals("null")
                || lower.equals("changeme")
                || lower.equals("example")
                || lower.startsWith("classpath:")
                || lower.startsWith("classpath*:");
    }

    private String mask(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, Math.min(3, value.length())) + "****";
    }

    private record ConfigLine(String key, String value) {
    }
}
