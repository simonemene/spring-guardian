package com.example.guardian.core.execution;

import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.rules.SpringRule;
import com.example.guardian.core.scanner.ProjectSourceScanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleExecutionEngineTest {

    @TempDir
    Path tempDir;

    @Test
    void isolatesFailingRuleAndReportsDiagnostic() throws Exception {
        Path source = tempDir.resolve("src/main/java/demo/App.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, "package demo; class App {}");
        ProjectScanContext context = new ProjectSourceScanner().scan(tempDir);

        SpringRule failing = new SpringRule() {
            @Override public String id() { return "TEST_FAIL"; }
            @Override public List<com.example.guardian.core.model.Finding> evaluate(ProjectScanContext ignored) {
                throw new IllegalStateException("password=NeverExposeThis");
            }
        };
        SpringRule succeeding = new SpringRule() {
            @Override public String id() { return "TEST_OK"; }
            @Override public List<com.example.guardian.core.model.Finding> evaluate(ProjectScanContext ignored) { return List.of(); }
        };

        var result = new RuleExecutionEngine(List.of(failing, succeeding)).execute(context);

        assertEquals(2, result.attempted());
        assertEquals(1, result.succeeded());
        assertEquals(1, result.failed());
        assertEquals(1, result.diagnostics().size());
        org.junit.jupiter.api.Assertions.assertFalse(result.diagnostics().get(0).message().contains("NeverExposeThis"));
    }
}
