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

class SpringSecurityAlternativeRulesTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsManualPrincipalSecurityContextAndRoleChecks() throws Exception {
        Path project = tempDir.resolve("manual-security");
        Files.createDirectories(project.resolve("src/main/java/com/acme/order/service"));
        Files.createDirectories(project.resolve("src/main/java/com/acme/order/web"));

        Files.writeString(project.resolve("pom.xml"), """
                <project>
                  <modelVersion>4.0.0</modelVersion>
                  <dependencies>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-security</artifactId></dependency>
                  </dependencies>
                </project>
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/order/web/OrderController.java"), """
                package com.acme.order.web;

                import java.security.Principal;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class OrderController {
                    @GetMapping("/orders")
                    String orders(Principal principal) {
                        if (principal != null) {
                            return "ok";
                        }
                        return "anonymous";
                    }
                }
                """);

        Files.writeString(project.resolve("src/main/java/com/acme/order/service/OrderService.java"), """
                package com.acme.order.service;

                import org.springframework.security.core.Authentication;
                import org.springframework.security.core.context.SecurityContextHolder;
                import org.springframework.stereotype.Service;

                @Service
                class OrderService {
                    boolean canExport(Authentication authentication) {
                        var current = SecurityContextHolder.getContext().getAuthentication();
                        return authentication != null
                            && current.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
                    }
                }
                """);

        Set<String> ids = new ProjectScanService(GuardianSettings.defaults()).scan(project).findings().stream()
                .map(FindingGroup::ruleId)
                .collect(Collectors.toSet());

        assertTrue(ids.contains("SPR_ALT021_MANUAL_PRINCIPAL_SECURITY_CHECK"), "Expected manual principal check but got " + ids);
        assertTrue(ids.contains("SPR_ALT022_SECURITY_CONTEXT_HOLDER_IN_BUSINESS_CODE"), "Expected SecurityContextHolder business usage but got " + ids);
        assertTrue(ids.contains("SPR_ALT023_MANUAL_ROLE_STRING_CHECK"), "Expected manual role string check but got " + ids);
    }
}
