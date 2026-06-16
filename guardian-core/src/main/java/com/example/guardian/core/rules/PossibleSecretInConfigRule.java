package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PossibleSecretInConfigRule implements SpringRule {

    private static final Pattern SECRET_LINE = Pattern.compile(
            "(password|secret|api[-_]?key|token|client[-_]?secret)\\s*[:=]\\s*['\\\"]?[^\\s'\\\"]{6,}",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String id() {
        return "SPR037_POSSIBLE_SECRET_IN_CONFIG";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        try (var stream = Files.walk(context.root())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.startsWith("application")
                                && (name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".properties"));
                    })
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i).trim();

                                if (line.startsWith("#")) {
                                    continue;
                                }

                                if (SECRET_LINE.matcher(line).find()
                                        && !line.contains("${")
                                        && !line.toLowerCase().contains("changeme")
                                        && !line.toLowerCase().contains("example")) {
                                    findings.add(new Finding(
                                            id(),
                                            Severity.CRITICAL,
                                            "Possible secret in Spring configuration",
                                            context.root().relativize(path).toString(),
                                            i + 1,
                                            "Suspicious config line: " + mask(line),
                                            "Secrets committed in configuration files can leak credentials and compromise environments.",
                                            "Move secrets to environment variables, a secret manager, Vault, AWS Secrets Manager, etc."
                                    ));
                                }
                            }
                        } catch (Exception ignored) {
                            // Keep scan resilient.
                        }
                    });
        } catch (Exception ignored) {
            // Keep scan resilient.
        }

        return findings;
    }

    private String mask(String line) {
        int colon = line.indexOf(':');
        int equals = line.indexOf('=');

        int sep;
        if (colon < 0) {
            sep = equals;
        } else if (equals < 0) {
            sep = colon;
        } else {
            sep = Math.min(colon, equals);
        }

        if (sep < 0) {
            return line;
        }

        return line.substring(0, sep + 1) + " ******";
    }
}
