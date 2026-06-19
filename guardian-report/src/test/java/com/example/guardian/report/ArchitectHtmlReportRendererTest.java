package com.example.guardian.report;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies Architect Mode is visible in the standalone HTML report.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectHtmlReportRendererTest {

    @TempDir
    Path tempDir;

    @Test
    void htmlReportContainsArchitectModeSections() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/controller"));
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    </dependencies>
                </project>
                """);
        Files.writeString(tempDir.resolve("src/main/java/com/acme/controller/DemoController.java"), """
                package com.acme.controller;

                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class DemoController {
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        String html = new HtmlReportRenderer().render(report);

        assertTrue(html.contains("Spring Guardian Architect Mode"));
        assertTrue(html.contains("Spring Maturity Score"));
        assertTrue(html.contains("Spring Architecture Map"));
        assertTrue(html.contains("Modernization Plan"));
        assertTrue(html.contains("OpenRewrite suggestions YAML"));
    }
}
