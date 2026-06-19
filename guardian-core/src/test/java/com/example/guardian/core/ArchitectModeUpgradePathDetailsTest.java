package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectModeReport;
import com.example.guardian.core.model.UpgradeStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectModeUpgradePathDetailsTest {

    @TempDir
    Path tempDir;

    @Test
    void upgradePathExplainsWhyActionsEvidenceAndOptionalRecipes() throws Exception {
        Path project = tempDir.resolve("upgrade-path");
        Files.createDirectories(project.resolve("src/main/java/com/acme/api"));
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
                management.endpoint.health.show-details=always
                api.password=hardcoded
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/api/LegacyController.java"), """
                package com.acme.api;

                import javax.validation.Valid;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class LegacyController {
                    @GetMapping("/legacy")
                    String legacy() {
                        System.out.println("legacy");
                        return "ok";
                    }
                }
                """);

        ArchitectModeReport architect = new ProjectScanService(GuardianSettings.defaults()).scan(project).architectMode();

        assertFalse(architect.upgradePath().steps().isEmpty());
        assertTrue(architect.upgradePath().steps().stream().anyMatch(step -> !step.whyRecommended().isBlank()));
        assertTrue(architect.upgradePath().steps().stream().anyMatch(step -> !step.actions().isEmpty()));
        assertTrue(architect.upgradePath().steps().stream().anyMatch(step -> !step.evidence().isEmpty()));
        assertTrue(architect.openRewritePlan().suggestions().stream().anyMatch(suggestion -> suggestion.recipe().contains("UpgradeSpringBoot") || suggestion.recipe().contains("Jakarta")));

        UpgradeStep first = architect.upgradePath().steps().get(0);
        assertFalse(first.effort().isBlank());
    }
}
