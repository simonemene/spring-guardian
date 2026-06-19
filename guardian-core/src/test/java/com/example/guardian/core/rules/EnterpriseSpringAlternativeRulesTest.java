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

class EnterpriseSpringAlternativeRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsEnterpriseSpringAlternativesFromRealProjectShape() throws Exception {
        Path project = tempDir.resolve("app");
        Files.createDirectories(project.resolve("src/main/java/com/acme"));
        Files.createDirectories(project.resolve("src/main/resources"));

        Files.writeString(project.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(project.resolve("src/main/resources/application.properties"), """
                management.endpoints.web.exposure.include=*
                management.endpoint.health.show-details=always
                spring.jpa.open-in-view=true
                api.password=superSecretValue
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/Customer.java"), """
                package com.acme;

                import jakarta.persistence.Entity;
                import jakarta.persistence.FetchType;
                import jakarta.persistence.ManyToOne;

                @Entity
                public class Customer {
                    @ManyToOne(fetch = FetchType.EAGER)
                    private Customer parent;
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/CustomerController.java"), """
                package com.acme;

                import org.springframework.transaction.annotation.Transactional;
                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.RequestBody;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                @Transactional
                public class CustomerController {
                    @PostMapping("/customers")
                    public Customer create(@RequestBody Customer customer) {
                        System.out.println(customer);
                        save();
                        return customer;
                    }

                    @Transactional
                    private void save() {
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/CustomerRepository.java"), """
                package com.acme;

                import org.springframework.stereotype.Repository;

                @Repository
                public class CustomerRepository {
                    private CustomerMapper mapper = new CustomerMapper();

                    public Object findBusiness(boolean active) {
                        if (active) {
                            return mapper.map("active");
                        }
                        return null;
                    }
                }

                class CustomerMapper {
                    Object map(String value) {
                        return value;
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/CustomerService.java"), """
                package com.acme;

                import jakarta.persistence.EntityManager;
                import org.springframework.stereotype.Service;

                @Service
                public class CustomerService {
                    private EntityManager entityManager;
                    private CustomerRepository customerRepository;

                    public Object findByName(String name) {
                        return entityManager.createQuery("select c from Customer c where c.name = '" + name + "'").getSingleResult();
                    }

                    public Object findAll() {
                        return customerRepository.findBusiness(true);
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/ApiProperties.java"), """
                package com.acme;

                import org.springframework.boot.context.properties.ConfigurationProperties;

                @ConfigurationProperties("api")
                public record ApiProperties(String url) {
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/SecurityConfig.java"), """
                package com.acme;

                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;
                import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                import org.springframework.security.web.SecurityFilterChain;

                @Configuration
                class SecurityConfig {
                    @Bean
                    SecurityFilterChain security(HttpSecurity http) throws Exception {
                        return http
                            .csrf(csrf -> csrf.disable())
                            .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll())
                            .build();
                    }
                }
                """);

        Set<String> ids = new ProjectScanService(GuardianSettings.defaults()).scan(project).findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());

        assertContains(ids, "SPR_ALT002_CSRF_DISABLED_WITHOUT_STATELESS_API");
        assertContains(ids, "SPR_ALT003_PERMIT_ALL_TOO_BROAD");
        assertContains(ids, "SPR_ALT004_ACTUATOR_EXPOSURE_WILDCARD");
        assertContains(ids, "SPR_ALT005_HEALTH_DETAILS_ALWAYS_PUBLIC");
        assertContains(ids, "SPR_ALT006_ENTITY_RETURNED_BY_CONTROLLER");
        assertContains(ids, "SPR_ALT007_ENTITY_ACCEPTED_AS_REQUEST_BODY");
        assertContains(ids, "SPR_ALT008_MISSING_REQUEST_VALIDATION");
        assertContains(ids, "SPR_ALT009_MISSING_CONTROLLER_ADVICE");
        assertContains(ids, "SPR_ALT010_OPEN_IN_VIEW_ENABLED");
        assertContains(ids, "SPR_ALT011_REPOSITORY_WITH_BUSINESS_LOGIC");
        assertContains(ids, "SPR_ALT012_CONCATENATED_QUERY_STRING");
        assertContains(ids, "SPR_ALT013_MISSING_READ_ONLY_TRANSACTION");
        assertContains(ids, "SPR_ALT014_ENTITY_RELATION_EAGER_FETCH");
        assertContains(ids, "SPR_ALT015_TRANSACTIONAL_PRIVATE_METHOD");
        assertContains(ids, "SPR_ALT016_TRANSACTIONAL_SELF_INVOCATION");
        assertContains(ids, "SPR_ALT017_TRANSACTIONAL_ON_CONTROLLER");
        assertContains(ids, "SPR_ALT018_CONFIGURATION_PROPERTIES_WITHOUT_VALIDATION");
        assertContains(ids, "SPR_ALT019_SECRET_LIKE_PROPERTY");
        assertContains(ids, "SPR_ALT020_SYSTEM_OUT_LOGGING");
    }

    @Test
    void detectsMissingSecurityFilterChainAlternativeSeparately() throws Exception {
        Path project = tempDir.resolve("security-missing");
        Files.createDirectories(project.resolve("src/main/java/com/acme"));

        Files.writeString(project.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/Application.java"), """
                package com.acme;

                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                class Application {
                }
                """);

        Set<String> ids = new ProjectScanService(GuardianSettings.defaults()).scan(project).findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());

        assertContains(ids, "SPR_ALT001_SECURITY_FILTER_CHAIN_MISSING");
    }

    private void assertContains(Set<String> ids, String expected) {
        assertTrue(ids.contains(expected), "Expected finding " + expected + " but got " + ids);
    }
}
