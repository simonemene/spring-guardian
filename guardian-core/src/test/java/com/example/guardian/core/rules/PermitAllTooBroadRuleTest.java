package com.example.guardian.core.rules;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PermitAllTooBroadRuleTest {

    @TempDir
    Path tempDir;

    @Test
    void detectsBroadPermitAllMatcher() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/config"));
        Files.createDirectories(tempDir.resolve("src/test/java/com/acme"));

        Files.writeString(tempDir.resolve("src/main/java/com/acme/config/SecurityConfig.java"), """
                package com.acme.config;

                import org.springframework.context.annotation.Bean;
                import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                import org.springframework.security.web.SecurityFilterChain;

                public class SecurityConfig {
                    @Bean
                    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                        http.authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll());
                        return http.build();
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/test/java/com/acme/DummyTest.java"), """
                package com.acme;

                import org.junit.jupiter.api.Test;

                class DummyTest {
                    @Test
                    void ok() {
                        org.junit.jupiter.api.Assertions.assertTrue(true);
                    }
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);

        assertTrue(report.findings().stream()
                .anyMatch(finding -> finding.ruleId().equals("SPR041_PERMIT_ALL_TOO_BROAD")));
    }
}
