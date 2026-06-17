package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.ReportLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectScanServiceLanguageTest {

    @TempDir
    Path tempDir;

    @Test
    void scanReturnsItalianTexts() throws Exception {
        createFieldInjectionProject(tempDir);

        ArchitectureReviewReport report = new ProjectScanService(GuardianSettings.defaults())
                .scan(tempDir, ReportLanguage.ITALIAN);

        assertTrue(report.findings().stream().anyMatch(finding -> finding.title().equals("Dipendenza iniettata su campo")));
        assertTrue(report.findingsByCategory().stream().anyMatch(category -> category.category().equals("Architettura")));
    }

    @Test
    void scanReturnsEnglishTexts() throws Exception {
        createFieldInjectionProject(tempDir);

        ArchitectureReviewReport report = new ProjectScanService(GuardianSettings.defaults())
                .scan(tempDir, ReportLanguage.ENGLISH);

        assertTrue(report.findings().stream().anyMatch(finding -> finding.title().equals("Field injection")));
        assertTrue(report.findingsByCategory().stream().anyMatch(category -> category.category().equals("Architecture")));
    }

    @Test
    void languageParserAcceptsCommonCodes() {
        assertEquals(ReportLanguage.ITALIAN, ReportLanguage.from("it"));
        assertEquals(ReportLanguage.ENGLISH, ReportLanguage.from("en"));
        assertEquals(ReportLanguage.ITALIAN, ReportLanguage.from("unknown"));
    }

    private void createFieldInjectionProject(Path root) throws Exception {
        Files.createDirectories(root.resolve("src/main/java/com/acme"));
        Files.createDirectories(root.resolve("src/test/java/com/acme"));
        Files.writeString(root.resolve("src/main/java/com/acme/UserService.java"), """
                package com.acme;

                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Service;

                @Service
                public class UserService {
                    @Autowired
                    private Object dependency;
                }
                """);
        Files.writeString(root.resolve("src/test/java/com/acme/UserServiceTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;

                class UserServiceTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);
    }
}
