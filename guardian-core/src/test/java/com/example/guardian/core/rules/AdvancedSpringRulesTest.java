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

class AdvancedSpringRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsHighSignalArchitectureRuntimeAndModernizationCases() throws Exception {
        Path project = tempDir.resolve("risk-app");
        Files.createDirectories(project.resolve("src/main/java/com/acme"));
        Files.createDirectories(project.resolve("src/main/resources"));

        writePom(project);

        Files.writeString(project.resolve("src/main/resources/application.properties"), """
                spring.main.allow-circular-references=true
                spring.main.allow-bean-definition-overriding=true
                spring.flyway.clean-disabled=false
                spring.liquibase.drop-first=true
                server.error.include-stacktrace=always
                spring.security.debug=true
                spring.jpa.show-sql=true
                logging.level.root=TRACE
                spring.batch.jdbc.initialize-schema=always
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/OrderController.java"), """
                package com.acme;

                import java.util.Map;
                import org.springframework.web.bind.annotation.*;

                @RestController
                class OrderController {
                    @GetMapping("/orders")
                    Map<String, Object> orders(@RequestBody SearchRequest request) {
                        return internalEndpoint();
                    }

                    @GetMapping("/internal")
                    Map<String, Object> internalEndpoint() {
                        return Map.of("ok", true);
                    }
                }

                record SearchRequest(String value) {}
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/OrderService.java"), """
                package com.acme;

                import java.util.List;
                import org.slf4j.MDC;
                import org.springframework.context.ApplicationEventPublisher;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;
                import org.springframework.web.client.RestClient;

                @Service
                class OrderService {
                    private final OrderRepository repository = null;
                    private final RestClient restClient = null;
                    private final ApplicationEventPublisher publisher = null;

                    @Transactional
                    void remoteAndCatch(String token) {
                        MDC.put("requestId", "1");
                        log.error("token={}", token);
                        try {
                            repository.save(new Order());
                            restClient.get().uri("/remote").retrieve();
                            publisher.publishEvent(new Object());
                        } catch (Exception ex) {
                            log.warn(ex.getMessage());
                        }
                    }

                    @Transactional(readOnly = true)
                    void readOnlyButWrites() {
                        repository.save(new Order());
                    }

                    List<Order> filterAll() {
                        return repository.findAll().stream().filter(order -> true).toList();
                    }

                    void flushAll(List<Order> orders) {
                        for (Order order : orders) {
                            repository.saveAndFlush(order);
                        }
                    }
                }

                class DummyLogger {
                    void error(String format, Object value) {}
                    void warn(String value) {}
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/OrderRepository.java"), """
                package com.acme;

                import java.util.List;

                interface OrderRepository {
                    Order save(Order order);
                    Order saveAndFlush(Order order);
                    List<Order> findAll();
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/Order.java"), """
                package com.acme;

                import jakarta.persistence.*;
                import lombok.Data;
                import org.springframework.beans.factory.annotation.Autowired;

                @Entity
                @Data
                class Order {
                    @Autowired
                    private OrderRepository repository;

                    @ManyToMany(cascade = CascadeType.ALL)
                    private java.util.List<Tag> tags;
                }

                @Entity
                class Tag {}
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/AsyncJobs.java"), """
                package com.acme;

                import org.springframework.scheduling.annotation.Async;
                import org.springframework.scheduling.annotation.Scheduled;
                import org.springframework.transaction.annotation.Transactional;

                class AsyncJobs {
                    @Async
                    private String brokenAsync() {
                        return "x";
                    }

                    @Async
                    @Transactional
                    public void asyncTx() {
                    }

                    @Scheduled(fixedRate = 1000)
                    public void scheduled(String invalidParameter) {
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/SecurityConfig.java"), """
                package com.acme;

                import java.nio.charset.StandardCharsets;
                import io.jsonwebtoken.security.Keys;
                import org.springframework.security.core.userdetails.User;
                import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
                import org.springframework.security.crypto.password.NoOpPasswordEncoder;

                class SecurityConfig {
                    void insecure() {
                        NoOpPasswordEncoder.getInstance();
                        User.withDefaultPasswordEncoder();
                        new BCryptPasswordEncoder(4);
                        Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/BatchConfig.java"), """
                package com.acme;

                import org.springframework.beans.factory.annotation.Value;
                import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

                @EnableBatchProcessing
                class BatchConfig {
                    @Value("#{jobParameters['input']}")
                    String input;

                    void configure() {
                        new RunIdIncrementer();
                        new JobParametersBuilder().addLong("time", System.currentTimeMillis());
                        readerBuilder.saveState(false);
                        stepBuilder.retry(TransientException.class).retryLimit(3);
                    }
                }
                """);

        Set<String> ids = scanIds(project);

        assertContains(ids, "ARCH101_CONTROLLER_CALLS_CONTROLLER");
        assertContains(ids, "WEB101_GET_WITH_REQUEST_BODY");
        assertContains(ids, "SPR_ALT049_UNTYPED_CONTROLLER_RESPONSE");
        assertContains(ids, "TX101_REMOTE_CALL_INSIDE_TRANSACTION");
        assertContains(ids, "TX102_READ_ONLY_TRANSACTION_WRITES");
        assertContains(ids, "TX103_TRANSACTIONAL_EXCEPTION_SWALLOWED");
        assertContains(ids, "SPR_ALT050_EVENT_PUBLISHED_INSIDE_TRANSACTION");
        assertContains(ids, "JPA101_FIND_ALL_FILTER_IN_MEMORY");
        assertContains(ids, "JPA102_FLUSH_INSIDE_LOOP");
        assertContains(ids, "ARCH102_ENTITY_INJECTS_SPRING_BEAN");
        assertContains(ids, "JPA103_ENTITY_LOMBOK_DATA");
        assertContains(ids, "JPA104_MANY_TO_MANY_CASCADE_ALL");
        assertContains(ids, "ASYNC101_INVALID_ASYNC_METHOD");
        assertContains(ids, "ASYNC102_SCHEDULED_METHOD_HAS_PARAMETERS");
        assertContains(ids, "SPR_ALT041_ASYNC_TRANSACTIONAL_SAME_METHOD");
        assertContains(ids, "SEC101_NOOP_PASSWORD_ENCODER");
        assertContains(ids, "SEC102_DEFAULT_PASSWORD_ENCODER");
        assertContains(ids, "SEC103_HARDCODED_SIGNING_SECRET");
        assertContains(ids, "SEC104_BCRYPT_LOW_STRENGTH");
        assertContains(ids, "BAT041_SAVE_STATE_FALSE");
        assertContains(ids, "BAT042_RUN_ID_INCREMENTER_REVIEW");
        assertContains(ids, "BAT043_RANDOMIZED_JOB_PARAMETER");
        assertContains(ids, "BAT051_RETRY_WITHOUT_BACKOFF");
        assertContains(ids, "BAT052_JOB_PARAMETER_WITHOUT_BATCH_SCOPE");
        assertContains(ids, "CFG101_ALLOW_CIRCULAR_REFERENCES");
        assertContains(ids, "CFG103_FLYWAY_CLEAN_ENABLED");
    }

    @Test
    void doesNotRaiseCorrelatedRulesWhenTheRiskyContextIsAbsent() throws Exception {
        Path project = tempDir.resolve("healthy-context");
        Files.createDirectories(project.resolve("src/main/java/com/acme"));
        writePom(project);

        Files.writeString(project.resolve("src/main/java/com/acme/Service.java"), """
                package com.acme;

                import java.util.concurrent.CompletableFuture;
                import org.springframework.scheduling.annotation.Async;
                import org.springframework.transaction.annotation.Transactional;
                import org.springframework.web.client.RestClient;

                class Service {
                    private RestClient restClient;

                    @Transactional(readOnly = true)
                    void readOnly() {
                        repository.findAll();
                    }

                    void remoteOutsideTransaction() {
                        restClient.get().uri("/remote").retrieve();
                    }

                    @Async
                    public CompletableFuture<String> async() {
                        return CompletableFuture.completedFuture("ok");
                    }
                }
                """);

        Set<String> ids = scanIds(project);
        assertFalse(ids.contains("TX101_REMOTE_CALL_INSIDE_TRANSACTION"));
        assertFalse(ids.contains("TX102_READ_ONLY_TRANSACTION_WRITES"));
        assertFalse(ids.contains("ASYNC101_INVALID_ASYNC_METHOD"));
    }

    private void writePom(Path project) throws Exception {
        Files.writeString(project.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-batch</artifactId></dependency>
                    </dependencies>
                </project>
                """);
    }

    private Set<String> scanIds(Path project) {
        return new ProjectScanService(GuardianSettings.defaults()).scan(project).findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());
    }

    private void assertContains(Set<String> ids, String expected) {
        assertTrue(ids.contains(expected), "Expected finding " + expected + " but got " + ids);
    }
}
