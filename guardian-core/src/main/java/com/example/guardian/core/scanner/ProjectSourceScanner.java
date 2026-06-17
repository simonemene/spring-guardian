package com.example.guardian.core.scanner;

import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectSourceScanner {

    public ProjectScanContext scan(Path root) {
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

            boolean hasTests = allFiles.stream().anyMatch(path -> {
                String normalized = path.toString().replace("\\", "/");
                return normalized.contains("/src/test/");
            });

            boolean hasAdvice = javaFiles.stream().anyMatch(file ->
                    file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream()
                            .anyMatch(c -> hasAnnotation(c, "RestControllerAdvice") || hasAnnotation(c, "ControllerAdvice"))
            );

            return new ProjectScanContext(root, javaFiles, pomFiles, entities, hasTests, hasAdvice);
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

    private boolean hasAnnotation(NodeWithAnnotations<?> node, String simpleName) {
        return node.getAnnotations().stream()
                .anyMatch(a -> a.getName().getIdentifier().equals(simpleName)
                        || a.getNameAsString().endsWith("." + simpleName));
    }

    private boolean isIgnored(Path path) {
        String normalized = path.toString().replace("\\", "/");
        return normalized.contains("/target/")
                || normalized.contains("/build/")
                || normalized.contains("/.git/")
                || normalized.contains("/.idea/")
                || normalized.contains("/node_modules/");
    }
}
