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

        assertTrue(ruleIds.contains("ADV001_MANUAL_OBJECT_MAPPER"));
        assertTrue(ruleIds.contains("ADV003_MANUAL_REST_TEMPLATE"));
        assertTrue(ruleIds.contains("ADV008_MANUAL_THREAD_CREATION"));
        assertTrue(ruleIds.contains("ADV009_MANUAL_EXECUTOR"));
        assertTrue(ruleIds.contains("ADV011_THREAD_SLEEP"));
        assertTrue(ruleIds.contains("ADV013_SCATTERED_VALUE"));
        assertTrue(ruleIds.contains("ADV021_MANUAL_VALIDATOR_FACTORY"));
        assertTrue(ruleIds.contains("ADV033_DIRECT_LOCAL_DATE_TIME_NOW"));
        assertTrue(ruleIds.contains("SPR072_SPRING_BEAN_CREATED_WITH_NEW"));
        assertTrue(ruleIds.contains("SPR083_ASYNC_WITHOUT_ENABLE_ASYNC"));
        assertTrue(ruleIds.contains("SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING"));
        assertTrue(report.findings().stream().anyMatch(finding -> "SPRING_ALTERNATIVE".equals(finding.findingType())));
        assertTrue(report.architectureAreas().stream().anyMatch(area -> area.code().equals("SPRING_ALTERNATIVE_ADVISOR")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_SPRING_ALTERNATIVE_ADVISOR")));
    }
}
