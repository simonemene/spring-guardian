package com.example.guardian.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests Architect Mode CLI exports.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void architectCommandExportsChecklistMermaidAndOpenRewrite() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/controller"));
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <properties><spring-boot.version>2.7.18</spring-boot.version></properties>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    </dependencies>
                </project>
                """);
        Files.writeString(tempDir.resolve("src/main/java/com/acme/controller/DemoController.java"), """
                package com.acme.controller;

                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class DemoController {
                    @GetMapping("/demo")
                    String demo() {
                        return "ok";
                    }
                }
                """);

        Path output = tempDir.resolve("architect.md");
        Path checklist = tempDir.resolve("checklist.json");
        Path mermaid = tempDir.resolve("module-map.mmd");
        Path rewrite = tempDir.resolve("openrewrite.yml");

        int exitCode = new CommandLine(new GuardianCommand()).execute(
                "architect",
                tempDir.toString(),
                "--format", "markdown",
                "--output", output.toString(),
                "--export-checklist", checklist.toString(),
                "--export-mermaid", mermaid.toString(),
                "--export-openrewrite", rewrite.toString()
        );

        assertEquals(0, exitCode);
        assertTrue(Files.readString(output).contains("Spring Guardian Modernization Plan"));
        assertTrue(Files.readString(checklist).contains("suggestedChange"));
        assertTrue(Files.readString(mermaid).startsWith("flowchart LR"));
        assertTrue(Files.readString(rewrite).contains("recipeList"));
    }
}
