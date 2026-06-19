package com.example.guardian.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies Architect Mode JSON output for CI integrations.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectCommandJsonOutputTest {

    @TempDir
    Path tempDir;

    @Test
    void architectCommandWritesJsonArchitectModeOnly() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/order/controller"));
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    </dependencies>
                </project>
                """);
        Files.writeString(tempDir.resolve("src/main/java/com/acme/order/controller/OrderController.java"), """
                package com.acme.order.controller;

                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class OrderController {
                }
                """);

        Path output = tempDir.resolve("architect.json");
        int exitCode = new CommandLine(new GuardianCommand()).execute(
                "architect",
                tempDir.toString(),
                "--format", "json",
                "--output", output.toString()
        );

        String json = Files.readString(output);
        assertEquals(0, exitCode);
        assertTrue(json.contains("\"maturityScore\""));
        assertTrue(json.contains("\"architectureMap\""));
        assertTrue(json.contains("\"modernizationPlan\""));
        assertTrue(json.contains("\"openRewritePlan\""));
    }
}
