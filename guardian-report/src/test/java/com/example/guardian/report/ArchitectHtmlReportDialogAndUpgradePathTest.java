package com.example.guardian.report;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectHtmlReportDialogAndUpgradePathTest {

    @TempDir
    Path tempDir;

    @Test
    void htmlReportContainsArchitectureDialogAndDetailedUpgradePath() throws Exception {
        Path project = tempDir.resolve("html-dialog");
        Files.createDirectories(project.resolve("src/main/java/com/acme/web"));
        Files.createDirectories(project.resolve("src/main/resources"));

        Files.writeString(project.resolve("pom.xml"), """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <properties><java.version>11</java.version><spring-boot.version>2.7.18</spring-boot.version></properties>
                  <dependencies>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
                  </dependencies>
                </project>
                """);

        Files.writeString(project.resolve("src/main/resources/application.properties"), """
                management.endpoints.web.exposure.include=*
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/web/DemoController.java"), """
                package com.acme.web;

                import java.security.Principal;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class DemoController {
                    @GetMapping("/demo")
                    String demo(Principal principal) {
                        if (principal != null) {
                            return "ok";
                        }
                        return "anonymous";
                    }
                }
                """);

        String html = new HtmlReportRenderer().render(new ProjectScanService(GuardianSettings.defaults()).scan(project));

        assertTrue(html.contains("architectureMapDialog"));
        assertTrue(html.contains("Open architecture graph"));
        assertTrue(html.contains("Spring layer flow and module risks"));
        assertTrue(html.contains("Why</b>"));
        assertTrue(html.contains("Actions</b>"));
        assertTrue(html.contains("effort"));
    }
}
