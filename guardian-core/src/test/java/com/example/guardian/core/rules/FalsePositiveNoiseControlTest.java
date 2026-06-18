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

import static org.junit.jupiter.api.Assertions.assertFalse;

class FalsePositiveNoiseControlTest {

    @TempDir
    Path tempDir;

    @Test
    void batchDtoAndImportsDoNotCreateUnrelatedSecurityCacheOrProcessorFindings() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/batch"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-batch</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """);
        Files.writeString(tempDir.resolve("src/main/java/com/acme/batch/CacheMapAllevamentiDto.java"), """
                package com.acme.batch;

                import org.springframework.batch.item.ItemProcessor;
                import org.springframework.batch.core.repository.JobRepository;
                import java.util.Map;

                public class CacheMapAllevamentiDto {
                    private final JobRepository jobRepository;
                    private final Map<String, String> values;

                    CacheMapAllevamentiDto(JobRepository jobRepository, Map<String, String> values) {
                        this.jobRepository = jobRepository;
                        this.values = values;
                    }
                }
                """);
        Files.writeString(tempDir.resolve("src/test/java/com/acme/SmokeTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;

                class SmokeTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir, ReportLanguage.ITALIAN);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());

        assertFalse(ruleIds.contains("SEC021_MULTIPLE_SECURITY_FILTER_CHAINS_WITHOUT_ORDER"));
        assertFalse(ruleIds.contains("BAT009_STATEFUL_ITEM_PROCESSOR"));
        assertFalse(ruleIds.contains("BAT034_REPOSITORY_CALL_INSIDE_PROCESSOR_LOOP"));
        assertFalse(ruleIds.contains("BAT035_REMOTE_CALL_PER_ITEM_IN_PROCESSOR"));
        assertFalse(ruleIds.contains("ADV087_MANUAL_IDEMPOTENCY_MAP"));
    }
}
