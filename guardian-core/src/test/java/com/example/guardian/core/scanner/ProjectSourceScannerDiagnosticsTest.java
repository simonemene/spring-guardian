package com.example.guardian.core.scanner;

import com.example.guardian.core.model.ProjectScanContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectSourceScannerDiagnosticsTest {

    @TempDir
    Path tempDir;

    @Test
    void keepsExplicitDiagnosticWhenJavaParserCannotParseSource() throws Exception {
        Path source = tempDir.resolve("src/main/java/demo/Broken.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, "package demo; class Broken { void x( { }");

        ProjectScanContext context = new ProjectSourceScanner().scan(tempDir);

        assertEquals(1, context.javaFiles().size());
        assertFalse(context.javaFiles().get(0).parsedSuccessfully());
        assertNotNull(context.javaFiles().get(0).parseError());
    }
}
