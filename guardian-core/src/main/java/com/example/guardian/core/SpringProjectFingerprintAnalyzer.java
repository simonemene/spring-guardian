package com.example.guardian.core;

import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.SpringCapability;
import com.example.guardian.core.model.SpringProjectFingerprint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Builds the stable Spring project fingerprint used by Architect Mode and UI project identity. */
final class SpringProjectFingerprintAnalyzer {

    SpringProjectFingerprint analyze(ProjectScanContext context) {
        ProjectCapabilities capabilities = context.capabilities();
        Set<SpringCapability> springCapabilities = new LinkedHashSet<>();
        if (capabilities.usesSpringWeb()) springCapabilities.add(SpringCapability.WEB_MVC);
        if (hasText(context, "spring-boot-starter-webflux", "org.springframework.web.reactive", "webclient")) springCapabilities.add(SpringCapability.WEBFLUX);
        if (capabilities.usesSpringSecurity()) springCapabilities.add(SpringCapability.SECURITY);
        if (hasText(context, "spring-boot-starter-oauth2-resource-server", "oauth2resourceserver")) springCapabilities.add(SpringCapability.OAUTH2_RESOURCE_SERVER);
        if (capabilities.usesJpa()) springCapabilities.add(SpringCapability.DATA_JPA);
        if (hasText(context, "spring-boot-starter-jdbc", "jdbctemplate")) springCapabilities.add(SpringCapability.JDBC);
        if (capabilities.usesSpringBatch()) springCapabilities.add(SpringCapability.BATCH);
        if (capabilities.usesActuator()) springCapabilities.add(SpringCapability.ACTUATOR);
        if (capabilities.usesValidation()) springCapabilities.add(SpringCapability.VALIDATION);
        if (capabilities.usesOpenApi()) springCapabilities.add(SpringCapability.OPENAPI);
        if (hasText(context, "spring-modulith")) springCapabilities.add(SpringCapability.MODULITH);
        if (hasText(context, "spring-ai")) springCapabilities.add(SpringCapability.SPRING_AI);
        if (hasText(context, "testcontainers")) springCapabilities.add(SpringCapability.TESTCONTAINERS);
        if (capabilities.usesLombok()) springCapabilities.add(SpringCapability.LOMBOK);

        String pomText = pomText(context);
        String buildTool = context.pomFiles().isEmpty() ? detectGradle(context.root()) : "MAVEN";
        String javaVersion = firstMatch(pomText,
                "<java.version>\\s*([^<]+)\\s*</java.version>",
                "<maven.compiler.release>\\s*([^<]+)\\s*</maven.compiler.release>",
                "<maven.compiler.source>\\s*([^<]+)\\s*</maven.compiler.source>");
        String springBootVersion = firstMatch(pomText,
                "<spring-boot.version>\\s*([^<]+)\\s*</spring-boot.version>",
                "<artifactId>spring-boot-starter-parent</artifactId>\\s*<version>\\s*([^<]+)\\s*</version>",
                "<artifactId>spring-boot-dependencies</artifactId>\\s*<version>\\s*([^<]+)\\s*</version>");

        List<String> starters = detectedStarters(pomText);
        List<String> annotations = detectedAnnotations(context);
        String summary = buildFingerprintSummary(springCapabilities, capabilities, buildTool, javaVersion, springBootVersion);

        return new SpringProjectFingerprint(
                buildTool,
                blankToUnknown(javaVersion),
                blankToUnknown(springBootVersion),
                context.pomFiles().size() > 1 || pomText.contains("<modules>"),
                capabilities,
                Set.copyOf(springCapabilities),
                starters,
                annotations,
                capabilities.detectedArchitecturalStyles(),
                summary
        );
    }

    private String buildFingerprintSummary(Set<SpringCapability> capabilities, ProjectCapabilities existing, String buildTool, String javaVersion, String bootVersion) {
        List<String> pieces = new ArrayList<>();
        if (capabilities.contains(SpringCapability.WEB_MVC)) pieces.add("Spring Boot MVC/API");
        if (capabilities.contains(SpringCapability.SECURITY)) pieces.add("Security");
        if (capabilities.contains(SpringCapability.DATA_JPA)) pieces.add("JPA");
        if (capabilities.contains(SpringCapability.BATCH)) pieces.add("Batch");
        if (pieces.isEmpty()) pieces.add("Spring/Java project");
        pieces.add(buildTool);
        if (javaVersion != null && !javaVersion.isBlank()) pieces.add("Java " + javaVersion);
        if (bootVersion != null && !bootVersion.isBlank()) pieces.add("Spring Boot " + bootVersion);
        if (!existing.usesActuator()) pieces.add("Actuator missing");
        if (!existing.usesValidation() && existing.usesSpringWeb()) pieces.add("Validation missing");
        if (!existing.usesOpenApi() && existing.usesSpringWeb()) pieces.add("OpenAPI missing");
        return String.join(" · ", pieces);
    }

    private List<String> detectedStarters(String pomText) {
        Matcher matcher = Pattern.compile("<artifactId>\\s*([^<]*spring[^<]*)\\s*</artifactId>", Pattern.CASE_INSENSITIVE).matcher(pomText);
        List<String> starters = new ArrayList<>();
        while (matcher.find()) {
            String starter = matcher.group(1).trim();
            if (!starter.isBlank() && !starters.contains(starter)) starters.add(starter);
        }
        return List.copyOf(starters);
    }

    private List<String> detectedAnnotations(ProjectScanContext context) {
        Set<String> annotations = new LinkedHashSet<>();
        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(com.github.javaparser.ast.expr.AnnotationExpr.class).stream()
                    .map(annotation -> annotation.getName().getIdentifier())
                    .filter(name -> name.startsWith("Rest") || name.startsWith("Controller") || name.startsWith("Service")
                            || name.startsWith("Repository") || name.startsWith("Entity") || name.startsWith("Transactional")
                            || name.startsWith("Scheduled") || name.startsWith("Async") || name.startsWith("Configuration")
                            || name.startsWith("Bean") || name.startsWith("Validated") || name.startsWith("Valid"))
                    .forEach(annotations::add);
        }
        return List.copyOf(annotations);
    }

    private String pomText(ProjectScanContext context) {
        return context.pomFiles().stream().map(this::readSafely).collect(Collectors.joining("\n"));
    }

    private String firstMatch(String text, String... patterns) {
        if (text == null) return "";
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            if (matcher.find()) return matcher.group(1).trim();
        }
        return "";
    }

    boolean hasText(ProjectScanContext context, String... values) {
        String joined = (pomText(context) + "\n" + context.javaFiles().stream().map(JavaSourceFile::content).collect(Collectors.joining("\n"))).toLowerCase(Locale.ROOT);
        for (String value : values) {
            if (joined.contains(value.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private String detectGradle(Path root) {
        return Files.exists(root.resolve("build.gradle")) || Files.exists(root.resolve("build.gradle.kts")) ? "GRADLE" : "UNKNOWN";
    }

    private String readSafely(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException exception) {
            return "";
        }
    }

    private String blankToUnknown(String value) {
        return value == null || value.isBlank() ? "UNKNOWN" : value;
    }
}
