package com.example.guardian.core.model;

import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Path;

public record JavaSourceFile(
        Path absolutePath,
        String relativePath,
        String content,
        CompilationUnit compilationUnit
) {
}
