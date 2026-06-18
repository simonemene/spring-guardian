package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Suggests Spring Boot structured logging when a modern Boot project still uses only plain log output.
 *
 * @author Simone Meneghetti
 */
public class StructuredLoggingAdvisorRule implements SpringRule {

    @Override
    public String id() {
        return "SPR074_STRUCTURED_LOGGING_ADVISOR";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!isModernBootProject(context) || hasStructuredLoggingConfiguration(context)) {
            return List.of();
        }

        return List.of(new Finding(
                id(),
                Severity.INFO,
                "Structured logging not configured",
                "project",
                null,
                "Spring Boot 3.4+ project without logging.structured configuration detected.",
                "Structured JSON logs make production diagnostics, correlation and log aggregation easier without custom logging code.",
                "Evaluate Spring Boot structured logging formats such as ECS, GELF or Logstash for production profiles."
        ));
    }

    private boolean isModernBootProject(ProjectScanContext context) {
        String pom = context.pomFiles().stream().map(this::read).reduce("", (left, right) -> left + "\n" + right).toLowerCase(Locale.ROOT);
        return pom.contains("spring-boot") && (pom.contains("3.4") || pom.contains("3.5") || pom.contains("4.0"));
    }

    private boolean hasStructuredLoggingConfiguration(ProjectScanContext context) {
        try (Stream<Path> paths = Files.walk(context.root())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> !isIgnored(path))
                    .filter(path -> path.getFileName().toString().startsWith("application"))
                    .map(this::read)
                    .map(text -> text.toLowerCase(Locale.ROOT))
                    .anyMatch(text -> text.contains("logging.structured"));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isIgnored(Path path) {
        String normalized = path.toString().replace("\\", "/");
        return normalized.contains("/target/")
                || normalized.contains("/build/")
                || normalized.contains("/.git/")
                || normalized.contains("/.idea/")
                || normalized.contains("/node_modules/");
    }

    private String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
