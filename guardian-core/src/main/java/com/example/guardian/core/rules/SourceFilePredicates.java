package com.example.guardian.core.rules;

import com.example.guardian.core.model.JavaSourceFile;

/**
 * Shared predicates used to classify scanned source files.
 *
 * @author Simone Meneghetti
 */
public final class SourceFilePredicates {

    private SourceFilePredicates() {
    }

    public static boolean isTestSource(JavaSourceFile file) {
        if (file == null || file.relativePath() == null) {
            return false;
        }

        String normalized = file.relativePath().replace("\\", "/");
        String fileName = normalized.substring(normalized.lastIndexOf('/') + 1);

        return normalized.contains("/src/test/")
                || normalized.startsWith("src/test/")
                || fileName.endsWith("Test.java")
                || fileName.endsWith("Tests.java")
                || fileName.endsWith("IT.java");
    }
}
