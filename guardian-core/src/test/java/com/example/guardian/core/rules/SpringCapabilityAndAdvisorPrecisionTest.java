package com.example.guardian.core.rules;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.FindingGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringCapabilityAndAdvisorPrecisionTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsMissingSpringCapabilitiesFromRealWebBoundaries() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """);
        Files.writeString(tempDir.resolve("src/main/java/com/acme/controller/OrderController.java"), """
                package com.acme.controller;

                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.RequestBody;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class OrderController {
                    @PostMapping("/orders")
                    void create(@RequestBody OrderRequest request) {
                    }
                }

                record OrderRequest(String name) {}
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

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());
        Set<String> types = report.findings().stream().map(FindingGroup::findingType).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("CAP001_WEB_VALIDATION_NOT_DETECTED"));
        assertTrue(ruleIds.contains("CAP002_WEB_OPENAPI_NOT_DETECTED"));
        assertTrue(ruleIds.contains("CAP003_ACTUATOR_NOT_DETECTED"));
        assertTrue(types.contains("SPRING_CAPABILITY_GAP"));
    }

    @Test
    void advisorDoesNotDetectCacheFromPlainMapButDetectsStaticMutableCache() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));
        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/CacheService.java"), """
                package com.acme.service;

                import java.util.Map;
                import java.util.concurrent.ConcurrentHashMap;

                class CacheService {
                    private Map<String, String> requestValues;
                    private static final Map<String, String> processedCache = new ConcurrentHashMap<>();
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

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        var cacheFindings = report.findings().stream()
                .filter(group -> group.ruleId().equals("ADV016_STATIC_MUTABLE_CACHE_MAP"))
                .toList();

        assertTrue(cacheFindings.stream().anyMatch(group -> group.affectedComponents().stream()
                .anyMatch(component -> component.evidence().contains("processedCache"))));
        assertFalse(cacheFindings.stream().anyMatch(group -> group.affectedComponents().stream()
                .anyMatch(component -> component.evidence().contains("requestValues"))));
    }
}
