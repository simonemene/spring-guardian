package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.FindingGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProjectScanServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void scanDetectsMainSpringArchitectureProblems() throws Exception {
        createSpringProject(tempDir);

        ProjectScanService service = new ProjectScanService(GuardianSettings.defaults());

        ArchitectureReviewReport report = service.scan(tempDir);

        Set<String> ruleIds = report.findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR002_FIELD_INJECTION"));
        assertTrue(ruleIds.contains("SPR003_CONTROLLER_INJECTS_REPOSITORY"));
        assertTrue(ruleIds.contains("SPR006_ENTITY_EXPOSED_IN_CONTROLLER"));
        assertTrue(ruleIds.contains("SPR007_SELF_INVOCATION_PROXY"));
        assertTrue(ruleIds.contains("SPR008_INVALID_TRANSACTIONAL_USAGE"));
        assertTrue(ruleIds.contains("SPR010_MISSING_REST_CONTROLLER_ADVICE"));
        assertTrue(ruleIds.contains("SPR011_GENERIC_TRY_CATCH"));
        assertTrue(ruleIds.contains("SPR013_PATHVARIABLE_WITHOUT_NAME"));
        assertTrue(ruleIds.contains("SPR014_API_VERSIONING_MISSING"));
        assertTrue(ruleIds.contains("SPR020_OPTIONAL_GET_WITHOUT_GUARD"));
        assertTrue(ruleIds.contains("SPR021_CONSOLE_LOGGING"));
        assertTrue(ruleIds.contains("SPR023_REQUEST_BODY_WITHOUT_VALIDATION"));
        assertTrue(ruleIds.contains("SPR025_NULL_RETURN_IN_REPOSITORY_OR_SERVICE"));
        assertTrue(ruleIds.contains("SPR026_HTTP_CLIENT_CREATED_MANUALLY"));
        assertTrue(ruleIds.contains("SPR037_POSSIBLE_SECRET_IN_CONFIG"));
        assertTrue(ruleIds.contains("SPR038_DDL_AUTO_UNSAFE"));
        assertTrue(ruleIds.contains("SPR039_ACTUATOR_EXPOSE_ALL"));

        assertTrue(report.architectureScore() < 100);
        assertTrue(report.scannedJavaFiles() > 0);
    }

    @Test
    void fieldAutowiredIsIgnoredInTestSources() throws Exception {
        createMinimalProjectWithAutowiredOnlyInTests(tempDir);

        ProjectScanService service = new ProjectScanService(GuardianSettings.defaults());

        ArchitectureReviewReport report = service.scan(tempDir);

        boolean hasFieldInjectionFindingInTest = report.findings().stream()
                .anyMatch(finding -> finding.ruleId().equals("SPR002_FIELD_INJECTION"));

        assertFalse(hasFieldInjectionFindingInTest);
    }

    @Test
    void missingTestsIsReportedWhenSrcTestIsAbsent() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.writeString(
                tempDir.resolve("src/main/java/com/acme/service/UserService.java"),
                """
                package com.acme.service;

                import org.springframework.stereotype.Service;

                @Service
                public class UserService {
                    public String hello() {
                        return "hello";
                    }
                }
                """
        );

        ProjectScanService service = new ProjectScanService(GuardianSettings.defaults());

        ArchitectureReviewReport report = service.scan(tempDir);

        assertTrue(report.findings().stream()
                .anyMatch(finding -> finding.ruleId().equals("SPR012_MISSING_TESTS")));
    }

    @Test
    void godClassRuleIgnoresLargeTestClasses() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme/service"));

        Files.writeString(tempDir.resolve("src/main/java/com/acme/service/UserService.java"), """
                package com.acme.service;

                import org.springframework.stereotype.Service;

                @Service
                public class UserService {
                    public String ok() {
                        return "ok";
                    }
                }
                """);

        StringBuilder methods = new StringBuilder();
        for (int i = 0; i < 25; i++) {
            methods.append("    void helper").append(i).append("() {}\n");
        }

        Files.writeString(tempDir.resolve("src/test/java/com/acme/service/BatchDependencyServiceUnitTest.java"), """
                package com.acme.service;

                class BatchDependencyServiceUnitTest {
                """ + methods + """
                }
                """);

        ArchitectureReviewReport report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);

        assertFalse(report.findings().stream()
                .anyMatch(finding -> finding.ruleId().equals("SPR030_GOD_CLASS")));
    }

    private void createSpringProject(Path root) throws Exception {
        Files.createDirectories(root.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/repository"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/entity"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/dto"));
        Files.createDirectories(root.resolve("src/main/resources"));
        Files.createDirectories(root.resolve("src/test/java/com/acme"));

        Files.writeString(root.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-web</artifactId>
                            <version>6.1.1</version>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(root.resolve("src/main/resources/application-prod.properties"), """
                app.password=superSecret123
                spring.jpa.hibernate.ddl-auto=update
                management.endpoints.web.exposure.include=*
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/entity/UserEntity.java"), """
                package com.acme.entity;

                import jakarta.persistence.Entity;

                @Entity
                public class UserEntity {
                    private Long id;
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/repository/UserRepository.java"), """
                package com.acme.repository;

                import java.util.Optional;
                import com.acme.entity.UserEntity;

                public interface UserRepository {
                    Optional<UserEntity> findById(Long id);
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/dto/CreateUserRequest.java"), """
                package com.acme.dto;

                public record CreateUserRequest(String name, String email) {
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/controller/UserController.java"), """
                package com.acme.controller;

                import com.acme.repository.UserRepository;
                import com.acme.entity.UserEntity;
                import com.acme.dto.CreateUserRequest;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.web.bind.annotation.*;
                import org.springframework.web.client.RestTemplate;

                @RestController
                @RequestMapping("/users")
                public class UserController {

                    @Autowired
                    private UserRepository userRepository;

                    @GetMapping("/{id}")
                    public UserEntity get(@PathVariable Long id) {
                        try {
                            System.out.println("loading");
                            return userRepository.findById(id).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @PostMapping
                    public UserEntity create(@RequestBody CreateUserRequest request) {
                        RestTemplate restTemplate = new RestTemplate();
                        return null;
                    }
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/service/OrderService.java"), """
                package com.acme.service;

                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                @Service
                public class OrderService {

                    public void process() {
                        save();
                    }

                    @Transactional
                    private void save() {
                    }

                    public Object load() {
                        return null;
                    }
                }
                """);

        Files.writeString(root.resolve("src/test/java/com/acme/UserControllerTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;
                import org.springframework.beans.factory.annotation.Autowired;

                class UserControllerTest {
                    @Autowired
                    Object object;

                    @Test
                    void contextLoads() {
                    }
                }
                """);
    }

    private void createMinimalProjectWithAutowiredOnlyInTests(Path root) throws Exception {
        Files.createDirectories(root.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(root.resolve("src/test/java/com/acme"));

        Files.writeString(root.resolve("src/main/java/com/acme/service/UserService.java"), """
                package com.acme.service;

                import org.springframework.stereotype.Service;

                @Service
                public class UserService {
                }
                """);

        Files.writeString(root.resolve("src/test/java/com/acme/UserServiceTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;
                import org.springframework.beans.factory.annotation.Autowired;

                class UserServiceTest {
                    @Autowired
                    Object object;

                    @Test
                    void test() {
                    }
                }
                """);
    }
}
