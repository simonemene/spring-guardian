package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects missing Spring capabilities that should normally exist for the detected Spring modules.
 *
 * @author Simone Meneghetti
 */
public class MissingSpringCapabilityRule implements SpringRule {

    @Override
    public String id() {
        return "CAP000_MISSING_SPRING_CAPABILITIES";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        if (context.capabilities().usesSpringWeb() && !hasValidationDependency(context) && hasRequestBodyBoundary(context)) {
            findings.add(new Finding(
                    "CAP001_WEB_VALIDATION_NOT_DETECTED",
                    Severity.MINOR,
                    "Spring Web project without Bean Validation capability",
                    null,
                    null,
                    "Spring Web and @RequestBody boundaries were detected, but Bean Validation was not detected.",
                    "Request validation may stay manual or inconsistent if validation support is not part of the project architecture.",
                    "Add spring-boot-starter-validation and validate request DTOs with @Valid, @Validated and typed constraints."
            ));
        }
        if (context.capabilities().usesSpringWeb() && !context.capabilities().usesOpenApi() && hasPublicEndpoint(context)) {
            findings.add(new Finding(
                    "CAP002_WEB_OPENAPI_NOT_DETECTED",
                    Severity.INFO,
                    "Spring Web project without OpenAPI capability",
                    null,
                    null,
                    "Spring Web endpoints were detected, but OpenAPI or Swagger annotations were not detected.",
                    "API contracts are harder to review, test and share when endpoint metadata is not generated from the codebase.",
                    "Add springdoc-openapi and document public endpoints with @Operation and @ApiResponse where appropriate."
            ));
        }
        if ((context.capabilities().usesSpringWeb() || context.capabilities().usesSpringBatch()) && !context.capabilities().usesActuator()) {
            findings.add(new Finding(
                    "CAP003_ACTUATOR_NOT_DETECTED",
                    Severity.INFO,
                    "Actuator capability not detected",
                    null,
                    null,
                    "Spring Web or Spring Batch was detected, but Spring Boot Actuator was not detected.",
                    "Operational visibility is weaker without standardized health, info and metrics endpoints.",
                    "Add spring-boot-starter-actuator and expose only the production-safe endpoints required by your platform."
            ));
        }
        if (context.capabilities().usesSpringBatch() && !context.capabilities().usesActuator()) {
            findings.add(new Finding(
                    "CAP004_BATCH_OBSERVABILITY_CAPABILITY_NOT_DETECTED",
                    Severity.INFO,
                    "Spring Batch observability capability not detected",
                    null,
                    null,
                    "Spring Batch was detected, but Actuator/Micrometer support was not detected.",
                    "Batch operations are harder to govern without health, metrics and execution counters exposed to the runtime platform.",
                    "Add Actuator and Micrometer integration, then expose job, step, read, write, skip and failure signals."
            ));
        }
        return findings;
    }

    private boolean hasValidationDependency(ProjectScanContext context) {
        for (Path pomFile : context.pomFiles()) {
            try {
                String pom = Files.readString(pomFile, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                if (pom.contains("spring-boot-starter-validation")
                        || pom.contains("hibernate-validator")
                        || pom.contains("jakarta.validation-api")
                        || pom.contains("javax.validation")) {
                    return true;
                }
            } catch (IOException ignored) {
                continue;
            }
        }
        return false;
    }

    private boolean hasRequestBodyBoundary(ProjectScanContext context) {
        return context.javaFiles().stream()
                .filter(file -> !SourceFilePredicates.isTestSource(file))
                .anyMatch(file -> containsNonImportLine(file, "@requestbody"));
    }

    private boolean hasPublicEndpoint(ProjectScanContext context) {
        return context.javaFiles().stream()
                .filter(file -> !SourceFilePredicates.isTestSource(file))
                .anyMatch(file -> containsNonImportLine(file, "@getmapping")
                        || containsNonImportLine(file, "@postmapping")
                        || containsNonImportLine(file, "@putmapping")
                        || containsNonImportLine(file, "@patchmapping")
                        || containsNonImportLine(file, "@deletemapping")
                        || containsNonImportLine(file, "@requestmapping"));
    }

    private boolean containsNonImportLine(JavaSourceFile file, String token) {
        String[] lines = file.content().split("\\R", -1);
        for (String line : lines) {
            String trimmed = line.strip().toLowerCase(Locale.ROOT);
            if (trimmed.isBlank() || trimmed.startsWith("import ") || trimmed.startsWith("//") || trimmed.startsWith("*") || trimmed.startsWith("/*")) {
                continue;
            }
            if (trimmed.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
