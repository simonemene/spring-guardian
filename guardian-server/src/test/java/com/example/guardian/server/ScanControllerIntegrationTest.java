package com.example.guardian.server;

import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.FindingGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScanControllerIntegrationTest {

    private ConfigurableApplicationContext context;
    private int port;

    @TempDir
    Path tempDir;

    @BeforeEach
    void startApplication() {
        SpringApplication application = new SpringApplication(GuardianServerApplication.class);
        application.setDefaultProperties(Map.of(
                "server.port", "0",
                "spring.main.banner-mode", "off"
        ));
        context = application.run();
        port = ((WebServerApplicationContext) context).getWebServer().getPort();
    }

    @AfterEach
    void stopApplication() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void scanLocalPathReturnsArchitectureReport() throws Exception {
        createProject(tempDir);

        ResponseEntity<ArchitectureReviewReport> response = new TestRestTemplate().postForEntity(
                "http://localhost:" + port + "/api/v1/scans/local",
                new HttpEntity<>(Map.of("path", tempDir.toString()), jsonHeaders()),
                ArchitectureReviewReport.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(tempDir.toAbsolutePath().normalize().toString(), response.getBody().projectRootPath());

        Set<String> ruleIds = response.getBody().findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR002_FIELD_INJECTION"));
        assertTrue(ruleIds.contains("SPR003_CONTROLLER_INJECTS_REPOSITORY"));
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void createProject(Path root) throws Exception {
        Files.createDirectories(root.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/repository"));
        Files.createDirectories(root.resolve("src/test/java/com/acme"));

        Files.writeString(root.resolve("src/main/java/com/acme/repository/UserRepository.java"), """
                package com.acme.repository;
                public interface UserRepository {}
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/controller/UserController.java"), """
                package com.acme.controller;

                import com.acme.repository.UserRepository;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                public class UserController {
                    @Autowired
                    private UserRepository userRepository;
                }
                """);

        Files.writeString(root.resolve("src/test/java/com/acme/UserControllerTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;

                class UserControllerTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);
    }
}
