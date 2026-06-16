package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UnsafeDdlAutoRule implements SpringRule {

    private static final Pattern DDL_AUTO_UNSAFE = Pattern.compile(
            "spring\\.jpa\\.hibernate\\.ddl-auto\\s*[:=]\\s*(update|create|create-drop)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String id() {
        return "SPR038_DDL_AUTO_UNSAFE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        try (var stream = Files.walk(context.root())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("application"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i).trim();
                                if (DDL_AUTO_UNSAFE.matcher(line).find()) {
                                    Severity severity = path.getFileName().toString().contains("prod")
                                            ? Severity.CRITICAL
                                            : Severity.MAJOR;

                                    findings.add(new Finding(
                                            id(),
                                            severity,
                                            "Potentially unsafe Hibernate ddl-auto setting",
                                            context.root().relativize(path).toString(),
                                            i + 1,
                                            "Detected line: " + line,
                                            "ddl-auto=update/create/create-drop can modify schemas unexpectedly and is dangerous in controlled environments.",
                                            "Use migrations with Flyway/Liquibase and avoid schema mutation in production."
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
}
