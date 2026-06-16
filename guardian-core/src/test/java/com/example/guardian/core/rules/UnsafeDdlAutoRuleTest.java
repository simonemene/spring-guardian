package com.example.guardian.core.rules;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnsafeDdlAutoRuleTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsUnsafeDdlAutoInProdAsCritical() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));
        Files.createDirectories(tempDir.resolve("src/main/resources"));

        Files.writeString(tempDir.resolve("src/main/java/com/acme/App.java"), """
                package com.acme;
                public class App {}
                """);

        Files.writeString(tempDir.resolve("src/test/java/com/acme/AppTest.java"), """
                package com.acme;
                import org.junit.jupiter.api.Test;
                class AppTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/resources/application-prod.properties"), """
                spring.jpa.hibernate.ddl-auto=create-drop
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);

        assertTrue(report.findings().stream()
                .anyMatch(finding -> finding.ruleId().equals("SPR038_DDL_AUTO_UNSAFE")
                        && finding.severity() == Severity.CRITICAL));
    }
}
