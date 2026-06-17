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
 * @author p15518 - Simone Meneghetti
 */
public record JavaSourceFile(
        Path absolutePath,
        String relativePath,
        String content,
        CompilationUnit compilationUnit
) {
}
