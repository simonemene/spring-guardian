package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectScanServiceSecretRedactionTest {

    @TempDir
    Path tempDir;

    @Test
    void reportNeverContainsDetectedSecretInEvidenceOrSnippet() throws Exception {
        Path javaSource = tempDir.resolve("src/main/java/demo/App.java");
        Path config = tempDir.resolve("src/main/resources/application.properties");
        Files.createDirectories(javaSource.getParent());
        Files.createDirectories(config.getParent());
        Files.writeString(javaSource, "package demo; class App {}");
        Files.writeString(config, "spring.datasource.url=jdbc:h2:mem:test\nspring.datasource.password=SuperPassword123!\n");

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        String renderedEvidence = report.findings().stream()
                .flatMap(group -> group.affectedComponents().stream())
                .map(component -> component.evidence() + "\n" + component.codeSnippet())
                .reduce("", (left, right) -> left + "\n" + right);

        assertTrue(report.findings().stream().anyMatch(group -> group.ruleId().contains("SECRET")));
        assertFalse(renderedEvidence.contains("SuperPassword123!"));
    }
}
