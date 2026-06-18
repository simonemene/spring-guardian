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

class ExtendedSpringArchitectureRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsExtendedSecurityWebBatchCloudPomObservabilityAndAdvisorRules() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/domain"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/batch"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/security"));
        Files.createDirectories(tempDir.resolve("src/main/resources"));

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <packaging>war</packaging>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                            <version>3.3.0</version>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-webflux</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-security</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-batch</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>com.h2database</groupId>
                            <artifactId>h2</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-devtools</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("src/main/resources/application.yml"), """
                spring:
                  profiles:
                    active: prod
                  datasource:
                    url: jdbc:postgresql://prod-db:5432/app
                    password: secret
                management:
                  endpoints:
                    web:
                      exposure:
                        include: "*"
                  endpoint:
                    health:
                      show-details: always
                logging:
                  file:
                    name: /var/log/app.log
                external:
                  file:
                    path: /tmp/input.csv
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/security/SecurityConfig.java"), """
                package com.acme.security;

                import org.springframework.context.annotation.Bean;
                import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                import org.springframework.security.web.SecurityFilterChain;

                class SecurityConfig {
                    @Bean
                    SecurityFilterChain chain(HttpSecurity http) throws Exception {
                        http.csrf(csrf -> csrf.disable());
                        http.formLogin(form -> {});
                        http.httpBasic(basic -> {});
                        http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll().anyRequest().permitAll());
                        return http.build();
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/domain/Order.java"), """
                package com.acme.domain;

                import org.springframework.http.ResponseEntity;
                import org.springframework.context.ApplicationEventPublisher;

                public class Order {
                    private final ApplicationEventPublisher publisher = null;
                    public ResponseEntity<String> expose() {
                        return ResponseEntity.ok("bad");
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/controller/OrderController.java"), """
                package com.acme.controller;

                import com.acme.domain.Order;
                import org.springframework.data.domain.Page;
                import org.springframework.data.domain.Pageable;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.CrossOrigin;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.RequestBody;
                import org.springframework.web.bind.annotation.RestController;

                @CrossOrigin
                @RestController
                class OrderController {
                    @PostMapping("/orders")
                    ResponseEntity<Object> create(@RequestBody Order order) {
                        try {
                            throw new IllegalStateException("boom");
                        } catch (Exception ex) {
                            return ResponseEntity.badRequest().body(ex.getMessage());
                        }
                    }

                    @GetMapping("/orders/save")
                    Page<Order> save(Pageable pageable) {
                        return null;
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/OrderService.java"), """
                package com.acme.service;

                import java.util.concurrent.CompletableFuture;
                import org.springframework.http.ResponseEntity;
                import org.springframework.stereotype.Service;

                @Service
                class OrderService {
                    ResponseEntity<String> find() {
                        CompletableFuture.supplyAsync(() -> "x");
                        long start = System.currentTimeMillis();
                        return ResponseEntity.ok("ok" + start);
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/batch/ImportJobConfig.java"), """
                package com.acme.batch;

                import org.springframework.batch.core.ExitStatus;
                import org.springframework.batch.core.job.builder.JobBuilder;
                import org.springframework.batch.item.database.JdbcCursorItemReader;
                import org.springframework.batch.item.database.JdbcPagingItemReader;

                class ImportJobConfig {
                    void configure() throws Exception {
                        new JobBuilder("job1");
                        chunk(1000);
                        JdbcPagingItemReader<String> pagingReader = new JdbcPagingItemReader<>();
                        JdbcCursorItemReader<String> cursorReader = new JdbcCursorItemReader<>();
                        String sql = "select * from CUSTOMER";
                        retry(Exception.class);
                        skip(Exception.class);
                        faultTolerant();
                        Thread.sleep(10);
                        System.exit(1);
                        ExitStatus status = ExitStatus.COMPLETED;
                    }
                    void chunk(int value) {}
                    void retry(Class<?> type) {}
                    void skip(Class<?> type) {}
                    void faultTolerant() {}
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir, ReportLanguage.ENGLISH);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());
        Set<String> types = report.findings().stream().map(FindingGroup::findingType).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("ARCH001_DOMAIN_MUST_NOT_DEPEND_ON_WEB_LAYER"));
        assertTrue(ruleIds.contains("SEC002_ANY_REQUEST_PERMIT_ALL"));
        assertTrue(ruleIds.contains("WEB036_PAGE_ENTITY_EXPOSED"));
        assertTrue(ruleIds.contains("BAT028_READER_SELECT_STAR"));
        assertTrue(ruleIds.contains("CLD003_SECRET_IN_PACKAGED_CONFIG"));
        assertTrue(ruleIds.contains("OBS025_HEALTH_DETAILS_ALWAYS_EXPOSED"));
        assertTrue(ruleIds.contains("POM002_BOOT_STARTER_VERSION_OVERRIDE"));
        assertTrue(ruleIds.contains("ADV066_COMPLETABLE_FUTURE_DEFAULT_EXECUTOR"));

        assertTrue(types.contains("ARCHITECTURE"));
        assertTrue(types.contains("SECURITY"));
        assertTrue(types.contains("WEB_LAYER"));
        assertTrue(types.contains("SPRING_BATCH"));
        assertTrue(types.contains("CLOUD_READINESS"));
        assertTrue(types.contains("OBSERVABILITY"));
        assertTrue(types.contains("DEPENDENCIES"));
        assertTrue(types.contains("SPRING_ALTERNATIVE"));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_BATCH")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_DEPENDENCY_GOVERNANCE")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_CLOUD_READINESS")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_OBSERVABILITY")));
        assertTrue(report.rulesExecuted() >= 300);
        assertTrue(report.findings().stream()
                .flatMap(group -> group.affectedComponents().stream())
                .anyMatch(component -> component.codeSnippet() != null && component.codeSnippet().contains("permitAll")));
    }
}
