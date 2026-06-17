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

class SpringAlternativeAdvisorRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsManualJavaObjectsAndSpringAlternatives() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/config"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <properties>
                        <spring-boot.version>3.5.0</spring-boot.version>
                    </properties>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/ManualSpringService.java"), """
                package com.acme.service;

                import com.fasterxml.jackson.databind.ObjectMapper;
                import java.io.FileInputStream;
                import java.text.SimpleDateFormat;
                import java.time.LocalDateTime;
                import java.util.Timer;
                import java.util.concurrent.ConcurrentHashMap;
                import java.util.concurrent.Executors;
                import org.springframework.beans.factory.annotation.Value;
                import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
                import org.springframework.stereotype.Service;
                import org.springframework.web.client.RestTemplate;
                import org.springframework.web.reactive.function.client.WebClient;

                @Service
                public class ManualSpringService {
                    @Value("${demo.value}")
                    private String demoValue;

                    public void execute() throws Exception {
                        ObjectMapper mapper = new ObjectMapper();
                        Thread thread = new Thread(() -> {});
                        Executors.newFixedThreadPool(4);
                        Timer timer = new Timer();
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        org.springframework.jdbc.core.JdbcTemplate template = new org.springframework.jdbc.core.JdbcTemplate();
                        System.getenv("APP_SECRET");
                        FileInputStream stream = new FileInputStream("/tmp/data.txt");
                        PaymentClient client = new PaymentClient();
                        RestTemplate restTemplate = new RestTemplate();
                        WebClient.builder();
                        java.net.URL url = new java.net.URL("https://example.com");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        LocalDateTime.now();
                        Thread.sleep(10);
                        jakarta.validation.Validation.buildDefaultValidatorFactory();
                        ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/PaymentClient.java"), """
                package com.acme.service;

                public class PaymentClient {
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/AsyncScheduledService.java"), """
                package com.acme.service;

                import org.springframework.scheduling.annotation.Async;
                import org.springframework.scheduling.annotation.Scheduled;
                import org.springframework.stereotype.Service;

                @Service
                public class AsyncScheduledService {
                    @Async
                    public void asyncWork() {
                    }

                    @Scheduled(fixedDelay = 1000)
                    public void scheduledWork() {
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/test/java/com/acme/OrderControllerTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;
                import org.springframework.boot.test.mock.mockito.MockBean;
                import org.springframework.test.web.servlet.MockMvc;

                class OrderControllerTest {
                    @MockBean
                    Object service;

                    MockMvc mockMvc;

                    @Test
                    void test() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR064_MANUAL_OBJECT_MAPPER"));
        assertTrue(ruleIds.contains("SPR065_MANUAL_THREAD_CREATION"));
        assertTrue(ruleIds.contains("SPR066_MANUAL_EXECUTOR_CREATION"));
        assertTrue(ruleIds.contains("SPR067_TIMER_SCHEDULING"));
        assertTrue(ruleIds.contains("SPR068_MANUAL_JDBC_TEMPLATE"));
        assertTrue(ruleIds.contains("SPR069_MANUAL_PASSWORD_ENCODER"));
        assertTrue(ruleIds.contains("SPR070_DIRECT_ENVIRONMENT_ACCESS"));
        assertTrue(ruleIds.contains("SPR071_MANUAL_FILE_RESOURCE_ACCESS"));
        assertTrue(ruleIds.contains("SPR072_SPRING_BEAN_CREATED_WITH_NEW"));
        assertTrue(ruleIds.contains("SPR073_MOCKBEAN_MODERNIZATION_ADVISOR"));
        assertTrue(ruleIds.contains("SPR074_STRUCTURED_LOGGING_ADVISOR"));
        assertTrue(ruleIds.contains("SPR075_MOCKMVC_TESTER_ADVISOR"));
        assertTrue(ruleIds.contains("SPR076_MANUAL_REST_TEMPLATE"));
        assertTrue(ruleIds.contains("SPR077_WEBCLIENT_BUILDER_CREATED_MANUALLY"));
        assertTrue(ruleIds.contains("SPR079_LOW_LEVEL_HTTP_CLIENT"));
        assertTrue(ruleIds.contains("SPR080_SIMPLE_DATE_FORMAT"));
        assertTrue(ruleIds.contains("SPR082_VALUE_INJECTION_FOR_GROUPED_CONFIG"));
        assertTrue(ruleIds.contains("SPR083_ASYNC_WITHOUT_ENABLE_ASYNC"));
        assertTrue(ruleIds.contains("SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING"));
        assertTrue(ruleIds.contains("SPR085_DIRECT_LOCAL_DATE_TIME_NOW"));
        assertTrue(ruleIds.contains("SPR088_THREAD_SLEEP_IN_PRODUCTION_CODE"));
        assertTrue(ruleIds.contains("SPR089_MANUAL_VALIDATOR_FACTORY"));
        assertTrue(ruleIds.contains("SPR090_MANUAL_CACHE_STRUCTURE"));
        assertTrue(report.findings().stream().anyMatch(finding -> "SPRING_ALTERNATIVE".equals(finding.findingType())));
        assertTrue(report.architectureAreas().stream().anyMatch(area -> area.code().equals("SPRING_ALTERNATIVE_ADVISOR")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_SPRING_ALTERNATIVE_ADVISOR")));
    }
}
