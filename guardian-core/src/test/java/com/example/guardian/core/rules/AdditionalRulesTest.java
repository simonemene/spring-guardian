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

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionalRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsAdditionalSpringGuardianCases() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/config"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/entity"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));

        Files.writeString(tempDir.resolve("src/main/java/com/acme/config/WebConfig.java"), """
                package com.acme.config;

                import org.springframework.context.annotation.Bean;
                import org.springframework.web.cors.CorsConfiguration;

                public class WebConfig {
                    @Bean
                    CorsConfiguration corsConfiguration() {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowCredentials(true);
                        configuration.addAllowedOriginPattern("*");
                        return configuration;
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/entity/OrderEntity.java"), """
                package com.acme.entity;

                import jakarta.persistence.Entity;
                import jakarta.persistence.FetchType;
                import jakarta.persistence.ManyToOne;

                @Entity
                public class OrderEntity {
                    @ManyToOne(fetch = FetchType.EAGER)
                    private CustomerEntity customer;
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/entity/CustomerEntity.java"), """
                package com.acme.entity;

                import jakarta.persistence.Entity;

                @Entity
                public class CustomerEntity {
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/OrderService.java"), """
                package com.acme.service;

                import java.util.List;
                import org.springframework.stereotype.Service;

                @Service
                public class OrderService {
                    private OrderRepository orderRepository;

                    public void process(List<Long> ids) {
                        try {
                            for (Long id : ids) {
                                orderRepository.findById(id);
                            }
                        } catch (RuntimeException ignored) {
                        }
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/OrderRepository.java"), """
                package com.acme.service;

                public interface OrderRepository {
                    Object findById(Long id);
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/controller/OrderController.java"), """
                package com.acme.controller;

                import java.util.Map;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                public class OrderController {
                    @GetMapping("/orders/rebuild")
                    public Map<String, Object> rebuild() {
                        updateReadModel();
                        return Map.of("status", "ok");
                    }

                    private void updateReadModel() {
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/test/java/com/acme/AsyncTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;

                class AsyncTest {
                    @Test
                    void waits() throws Exception {
                        Thread.sleep(1000);
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR046_CORS_ALLOW_ALL_WITH_CREDENTIALS"));
        assertTrue(ruleIds.contains("SPR047_EMPTY_CATCH_BLOCK"));
        assertTrue(ruleIds.contains("SPR048_JPA_EAGER_FETCHING"));
        assertTrue(ruleIds.contains("SPR049_REPOSITORY_CALL_INSIDE_LOOP"));
        assertTrue(ruleIds.contains("SPR050_GET_ENDPOINT_MUTATES_STATE"));
        assertTrue(ruleIds.contains("SPR051_CONTROLLER_RAW_RESPONSE"));
        assertTrue(ruleIds.contains("SPR052_THREAD_SLEEP_IN_TEST"));
        assertTrue(report.summary().totalFindings() > 0);
        assertTrue(report.rulesExecuted() >= 50);
        assertTrue(report.findingsByCategory().stream().anyMatch(category -> category.category().equals("Spring Security")));
        assertTrue(report.recommendedActions().stream().anyMatch(action -> action.ruleId().equals("SPR046_CORS_ALLOW_ALL_WITH_CREDENTIALS")));
    }
}
