package com.example.guardian.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ScanCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void scanCommandWritesJsonReport() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/UserService.java"), """
                package com.acme.service;

                import org.springframework.stereotype.Service;

                @Service
                public class UserService {
                    public String hello() { return "hello"; }
                }
                """);

        Files.writeString(tempDir.resolve("src/test/java/com/acme/UserServiceTest.java"), """
                package com.acme;
                import org.junit.jupiter.api.Test;
                class UserServiceTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        Path output = tempDir.resolve("report.json");

        int exitCode = new CommandLine(new GuardianCommand())
                .execute("scan", tempDir.toString(), "--format", "json", "--output", output.toString());

        assertEquals(0, exitCode);
        assertTrue(Files.exists(output));
        assertTrue(Files.readString(output).contains("architectureScore"));
    }

    @Test
    void scanCommandReturnsTwoWhenFailOnCriticalMatches() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme"));
        Files.writeString(tempDir.resolve("src/main/java/com/acme/App.java"), """
                package com.acme;
                public class App {}
                """);

        int exitCode = new CommandLine(new GuardianCommand())
                .execute("scan", tempDir.toString(), "--fail-on", "critical");

        assertEquals(2, exitCode);
    }
}
