package com.example.guardian.core.scanner;

import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ProjectScanContext;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Scans project sources and builds the immutable rule evaluation context.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ProjectSourceScanner {

    /**
     * Scans a project with the default strict profile.
     *
     * @param root project root
     * @return project scan context
     */
    public ProjectScanContext scan(Path root) {
        return scan(root, ProjectProfile.defaults());
    }

    /**
     * Scans a project with a caller-provided stateless profile.
     *
     * @param root project root
     * @param profile scan profile
     * @return project scan context
     */
    public ProjectScanContext scan(Path root, ProjectProfile profile) {
        if (!Files.exists(root)) {
            throw new IllegalArgumentException("Il percorso non esiste: " + root);
        }

        try {
            List<Path> allFiles;
            try (Stream<Path> stream = Files.walk(root)) {
                allFiles = stream
                        .filter(Files::isRegularFile)
                        .filter(path -> !isIgnored(path))
                        .toList();
            }

            List<JavaSourceFile> javaFiles = allFiles.stream()
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(path -> parseJavaFile(root, path))
                    .toList();

            List<Path> pomFiles = allFiles.stream()
                    .filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .toList();

            Set<String> entities = findAnnotatedClassNames(javaFiles, "Entity");
            boolean hasTests = allFiles.stream().anyMatch(path -> normalize(path).contains("/src/test/"));
            boolean hasAdvice = javaFiles.stream().anyMatch(file ->
                    file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream()
                            .anyMatch(c -> hasAnnotation(c, "RestControllerAdvice") || hasAnnotation(c, "ControllerAdvice"))
            );
            ProjectCapabilities capabilities = detectCapabilities(root, javaFiles, pomFiles, allFiles);

            return new ProjectScanContext(root, javaFiles, pomFiles, entities, hasTests, hasAdvice, capabilities, profile == null ? ProjectProfile.defaults() : profile);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile scansionare il percorso del progetto: " + root, e);
        }
    }

    private JavaSourceFile parseJavaFile(Path root, Path path) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            CompilationUnit cu = StaticJavaParser.parse(content);
            String relativePath = root.relativize(path).toString();
            return new JavaSourceFile(path, relativePath, content, cu);
        } catch (Exception e) {
            CompilationUnit empty = new CompilationUnit();
            String relativePath = root.relativize(path).toString();
            return new JavaSourceFile(path, relativePath, "", empty);
        }
    }

    private Set<String> findAnnotatedClassNames(List<JavaSourceFile> files, String annotation) {
        Set<String> names = new HashSet<>();
        for (JavaSourceFile file : files) {
            file.compilationUnit()
                    .findAll(ClassOrInterfaceDeclaration.class)
                    .stream()
                    .filter(c -> hasAnnotation(c, annotation))
                    .forEach(c -> names.add(c.getNameAsString()));
        }
        return names;
    }

    private ProjectCapabilities detectCapabilities(Path root, List<JavaSourceFile> javaFiles, List<Path> pomFiles, List<Path> allFiles) {
        String pomText = String.join("\n", pomFiles.stream().map(this::readSafely).toList()).toLowerCase();
        String javaText = javaFiles.stream().map(JavaSourceFile::content).reduce("", (left, right) -> left + "\n" + right).toLowerCase();
        List<String> paths = allFiles.stream().map(path -> root.relativize(path).toString().replace("\\", "/").toLowerCase()).toList();

        boolean usesSpringWeb = containsAny(pomText, "spring-boot-starter-web", "spring-webmvc") || containsAny(javaText, "org.springframework.web", "@restcontroller", "@controller");
        boolean usesSpringSecurity = containsAny(pomText, "spring-boot-starter-security", "spring-security") || containsAny(javaText, "org.springframework.security", "securityfilterchain");
        boolean usesJpa = containsAny(pomText, "spring-boot-starter-data-jpa", "hibernate-core", "jakarta.persistence", "javax.persistence") || containsAny(javaText, "@entity", "jakarta.persistence", "javax.persistence");
        boolean usesActuator = containsAny(pomText, "spring-boot-starter-actuator") || containsAny(javaText, "org.springframework.boot.actuate");
        boolean usesValidation = containsAny(pomText, "spring-boot-starter-validation", "jakarta.validation", "hibernate-validator") || containsAny(javaText, "jakarta.validation", "javax.validation", "@valid", "@validated");
        boolean usesOpenApi = containsAny(pomText, "springdoc-openapi", "swagger") || containsAny(javaText, "io.swagger.v3", "@operation", "@schema");
        boolean usesLombok = containsAny(pomText, "lombok") || containsAny(javaText, "lombok.", "@requiredargsconstructor", "@allargsconstructor");
        boolean usesSpringBatch = containsAny(pomText, "spring-boot-starter-batch", "spring-batch-core") || containsAny(javaText, "org.springframework.batch", "@enablebatchprocessing");
        boolean hasControllerLayer = paths.stream().anyMatch(path -> path.contains("/controller/")) || containsAny(javaText, "@restcontroller", "@controller");
        boolean hasServiceLayer = paths.stream().anyMatch(path -> path.contains("/service/")) || containsAny(javaText, "@service");
        boolean hasRepositoryLayer = paths.stream().anyMatch(path -> path.contains("/repository/")) || containsAny(javaText, "@repository", " extends jparepository", " extends crudrepository");
        boolean hasDomainLayer = paths.stream().anyMatch(path -> path.contains("/domain/"));
        boolean hasApplicationLayer = paths.stream().anyMatch(path -> path.contains("/application/"));
        boolean hasInfrastructureLayer = paths.stream().anyMatch(path -> path.contains("/infrastructure/") || path.contains("/adapter/"));
        List<String> styles = detectedStyles(hasDomainLayer, hasApplicationLayer, hasInfrastructureLayer, hasControllerLayer, hasServiceLayer, hasRepositoryLayer);

        return new ProjectCapabilities(
                usesSpringWeb,
                usesSpringSecurity,
                usesJpa,
                usesActuator,
                usesValidation,
                usesOpenApi,
                usesLombok,
                usesSpringBatch,
                hasControllerLayer,
                hasServiceLayer,
                hasRepositoryLayer,
                hasDomainLayer,
                hasApplicationLayer,
                hasInfrastructureLayer,
                styles
        );
    }

    private List<String> detectedStyles(boolean hasDomainLayer, boolean hasApplicationLayer, boolean hasInfrastructureLayer, boolean hasControllerLayer, boolean hasServiceLayer, boolean hasRepositoryLayer) {
        List<String> styles = new ArrayList<>();
        if (hasDomainLayer && hasApplicationLayer && hasInfrastructureLayer) {
            styles.add("HEXAGONAL_OR_DDD");
        }
        if (hasControllerLayer && hasServiceLayer && hasRepositoryLayer) {
            styles.add("LAYERED");
        }
        if (styles.isEmpty()) {
            styles.add("UNCLASSIFIED");
        }
        return List.copyOf(styles);
    }

    private String readSafely(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(value.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnnotation(NodeWithAnnotations<?> node, String simpleName) {
        return node.getAnnotations().stream()
                .anyMatch(a -> a.getName().getIdentifier().equals(simpleName)
                        || a.getNameAsString().endsWith("." + simpleName));
    }

    private boolean isIgnored(Path path) {
        String normalized = normalize(path);
        return normalized.contains("/target/")
                || normalized.contains("/build/")
                || normalized.contains("/.git/")
                || normalized.contains("/.idea/")
                || normalized.contains("/node_modules/");
    }

    private String normalize(Path path) {
        return path.toString().replace("\\", "/");
    }
}
