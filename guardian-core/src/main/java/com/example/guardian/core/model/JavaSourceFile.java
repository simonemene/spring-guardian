package com.example.guardian.core.model;

import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Path;

/**
 * Parsed Java source file used by architecture rules.
 *
 * @param absolutePath absolute file path
 * @param relativePath relative path from project root
 * @param content source content
 * @param compilationUnit parsed JavaParser compilation unit
 * @param parseError parser/read error when this source could not be fully analyzed
 * @author Simone Meneghetti
 */
public record JavaSourceFile(
        Path absolutePath,
        String relativePath,
        String content,
        CompilationUnit compilationUnit,
        String parseError
) {

    public JavaSourceFile(Path absolutePath, String relativePath, String content, CompilationUnit compilationUnit) {
        this(absolutePath, relativePath, content, compilationUnit, null);
    }

    public boolean parsedSuccessfully() {
        return parseError == null || parseError.isBlank();
    }
}
