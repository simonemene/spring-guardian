package com.example.guardian.core.rules;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ReportLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationAndDependencyGovernanceRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsExternalizedConfigurationAndDependencyGovernanceProblems() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/resources"));
        Files.createDirectories(tempDir.resolve("module-a"));
        Files.createDirectories(tempDir.resolve("module-b"));

        Files.writeString(tempDir.resolve("src/main/resources/application.properties"), """
                spring.datasource.url=jdbc:postgresql://localhost:5432/app
                spring.profiles.active=prod
                external.payment.endpoint=https://payments.example.local
                """);

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-webflux</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>legacy-tool</artifactId>
                            <version>1.0.0-SNAPSHOT</version>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("module-a/pom.xml"), """
                <project>
                    <dependencies>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>shared-client</artifactId>
                            <version>1.0.0</version>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("module-b/pom.xml"), """
                <project>
                    <dependencies>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>shared-client</artifactId>
                            <version>2.0.0</version>
                        </dependency>
                    </dependencies>
                </project>
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir, ReportLanguage.ITALIAN);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR091_APPLICATION_PROPERTIES_SHOULD_BE_EXTERNALIZED"));
        assertTrue(ruleIds.contains("SPR092_HARDCODED_ACTIVE_SPRING_PROFILE"));
        assertTrue(ruleIds.contains("SPR093_MAVEN_DEPENDENCY_VERSION_CONFLICT"));
        assertTrue(ruleIds.contains("SPR094_MAVEN_MIXED_STACK_DEPENDENCIES"));
        assertTrue(ruleIds.contains("SPR095_MAVEN_DEPENDENCY_HYGIENE"));
        assertTrue(report.findings().stream().anyMatch(finding -> "CONFIGURATION".equals(finding.findingType())));
        assertTrue(report.findings().stream().anyMatch(finding -> "DEPENDENCIES".equals(finding.findingType())));
    }
}
