package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectModeReport;
import com.example.guardian.core.model.SpringMaturityAreaScore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectModeMaturityScoreCalibrationTest {

    @TempDir
    Path tempDir;

    @Test
    void calibratesSecurityAndWebScoresFromRealSpringAntiPatterns() throws Exception {
        Path project = tempDir.resolve("maturity");
        Files.createDirectories(project.resolve("src/main/java/com/acme/app/web"));
        Files.createDirectories(project.resolve("src/main/java/com/acme/app/service"));

        Files.writeString(project.resolve("pom.xml"), """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <dependencies>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
                  </dependencies>
                </project>
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/app/web/AccountController.java"), """
                package com.acme.app.web;

                import java.security.Principal;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class AccountController {
                    private final com.acme.app.service.AccountRepository repository = null;

                    @GetMapping("/accounts")
                    Object accounts(Principal principal) {
                        if (principal != null) {
                            return repository.findAll();
                        }
                        return java.util.List.of();
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/app/service/AccountRepository.java"), """
                package com.acme.app.service;

                import org.springframework.stereotype.Repository;

                @Repository
                class AccountRepository {
                    Object findAll() { return null; }
                }
                """);

        ArchitectModeReport architect = new ProjectScanService(GuardianSettings.defaults()).scan(project).architectMode();

        assertNotNull(architect);
        SpringMaturityAreaScore security = area(architect, "SECURITY");
        SpringMaturityAreaScore webApi = area(architect, "WEB_API");
        SpringMaturityAreaScore architecture = area(architect, "ARCHITECTURE");

        assertTrue(security.score() < 100, "Security must not stay 100 when manual Principal checks are present: " + security);
        assertTrue(security.drivers().stream().anyMatch(driver -> driver.contains("SPR_ALT021") || driver.contains("Manual Principal")),
                "Security drivers should explain manual authorization checks: " + security.drivers());
        assertTrue(webApi.score() < 100, "Web/API score should decrease when validation/OpenAPI or controller boundary issues are detected.");
        assertTrue(architecture.score() < 100, "Architecture score should decrease when a controller talks to a repository.");
    }

    private SpringMaturityAreaScore area(ArchitectModeReport report, String code) {
        return report.maturityScore().areas().stream()
                .filter(area -> area.code().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
