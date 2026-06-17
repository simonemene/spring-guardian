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
                import java.util.Timer;
                import java.util.concurrent.Executors;
                import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
                import org.springframework.stereotype.Service;

                @Service
                public class ManualSpringService {
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
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/PaymentClient.java"), """
                package com.acme.service;

                public class PaymentClient {
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
        assertTrue(report.architectureAreas().stream().anyMatch(area -> area.code().equals("SPRING_ALTERNATIVE_ADVISOR")));
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_SPRING_ALTERNATIVE_ADVISOR")));
    }
}
