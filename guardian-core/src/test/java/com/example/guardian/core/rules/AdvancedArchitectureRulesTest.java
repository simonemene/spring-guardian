package com.example.guardian.core.rules;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureStyle;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ProjectType;
import com.example.guardian.core.model.ReleaseTarget;
import com.example.guardian.core.model.ReportLanguage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdvancedArchitectureRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsAdvancedSpringJpaSecurityAndLayeringCases() throws Exception {
        createAdvancedProject(tempDir);

        var profile = new ProjectProfile(ProjectType.WEB_API, ArchitectureStyle.DOMAIN_DRIVEN_DESIGN, ReleaseTarget.PRODUCTION, false);
        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir, ReportLanguage.ITALIAN, profile);
        Set<String> ruleIds = report.findings().stream().map(FindingGroup::ruleId).collect(Collectors.toSet());

        assertTrue(ruleIds.contains("SPR053_JPA_ENTITY_ACCESSIBLE_NO_ARGS_CONSTRUCTOR"));
        assertTrue(ruleIds.contains("SPR054_JPA_TO_ONE_RELATIONSHIP_SHOULD_BE_LAZY"));
        assertTrue(ruleIds.contains("SPR055_DOMAIN_LAYER_DEPENDS_ON_SPRING"));
        assertTrue(ruleIds.contains("SPR056_SERVICE_DEPENDS_ON_WEB_LAYER"));
        assertTrue(ruleIds.contains("SPR057_REPOSITORY_DEPENDS_ON_UPPER_LAYER"));
        assertTrue(ruleIds.contains("SPR058_SECURITY_FILTER_CHAIN_MISSING"));
        assertTrue(ruleIds.contains("SPR060_ENDPOINT_WITHOUT_OPENAPI_OPERATION"));
        assertTrue(ruleIds.contains("SPR061_ALL_ARGS_CONSTRUCTOR_ON_SPRING_COMPONENT"));
        assertTrue(ruleIds.contains("SPR062_CONSTRUCTOR_DEPENDENCY_FIELD_NOT_FINAL"));
        assertTrue(ruleIds.contains("SPR063_REST_CONTROLLER_WITHOUT_BASE_MAPPING"));
        assertEquals("NOT_READY", report.releaseReadiness().status());
        assertTrue(report.qualityGates().stream().anyMatch(gate -> gate.code().equals("GATE_PROFILE_ALIGNMENT")));
    }

    private void createAdvancedProject(Path root) throws Exception {
        Files.createDirectories(root.resolve("src/main/java/com/acme/controller"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/domain"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/service"));
        Files.createDirectories(root.resolve("src/main/java/com/acme/repository"));
        Files.createDirectories(root.resolve("src/test/java/com/acme"));

        Files.writeString(root.resolve("pom.xml"), """
                <project>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-security</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-data-jpa</artifactId>
                        </dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/controller/UserController.java"), """
                package com.acme.controller;

                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                public class UserController {
                    @GetMapping("/users")
                    public String users() {
                        return "ok";
                    }
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/domain/User.java"), """
                package com.acme.domain;

                import jakarta.persistence.Entity;
                import jakarta.persistence.ManyToOne;
                import org.springframework.stereotype.Component;

                @Entity
                @Component
                public class User {
                    @ManyToOne
                    private Group group;

                    public User(String name) {
                    }
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/domain/Group.java"), """
                package com.acme.domain;

                import jakarta.persistence.Entity;

                @Entity
                public class Group {
                    protected Group() {
                    }
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/service/UserService.java"), """
                package com.acme.service;

                import lombok.AllArgsConstructor;
                import org.springframework.http.ResponseEntity;
                import org.springframework.stereotype.Service;

                @Service
                @AllArgsConstructor
                public class UserService {
                    private UserRepository repository;

                    public UserService(UserRepository repository) {
                        this.repository = repository;
                    }

                    public ResponseEntity<String> load() {
                        return ResponseEntity.ok("ok");
                    }
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/service/UserRepository.java"), """
                package com.acme.service;

                public interface UserRepository {
                }
                """);

        Files.writeString(root.resolve("src/main/java/com/acme/repository/UserJpaRepository.java"), """
                package com.acme.repository;

                import org.springframework.http.ResponseEntity;

                public interface UserJpaRepository {
                    ResponseEntity<String> invalid();
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
