package com.example.guardian.core.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public record ProjectScanContext(
        Path root,
        List<JavaSourceFile> javaFiles,
        List<Path> pomFiles,
        Set<String> entityClassNames,
        boolean hasTests,
        boolean hasRestControllerAdvice
) {
}
