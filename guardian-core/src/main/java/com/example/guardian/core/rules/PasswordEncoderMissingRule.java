package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for PasswordEncoderMissingRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class PasswordEncoderMissingRule implements SpringRule {

    @Override
    public String id() {
        return "SPR042_PASSWORD_ENCODER_MISSING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        boolean securityDetected = context.javaFiles().stream()
                .anyMatch(file -> file.content().contains("SecurityFilterChain")
                        || file.content().contains("HttpSecurity")
                        || file.content().contains("UserDetailsService"));

        if (!securityDetected) {
            return List.of();
        }

        boolean encoderDetected = context.javaFiles().stream()
                .anyMatch(file -> file.content().contains("PasswordEncoder")
                        || file.content().contains("BCryptPasswordEncoder")
                        || file.content().contains("DelegatingPasswordEncoder"));

        if (!encoderDetected) {
            return List.of(new Finding(
                    id(),
                    Severity.MAJOR,
                    "Spring Security detected but no PasswordEncoder found",
                    null,
                    null,
                    "Security configuration exists, but no PasswordEncoder usage/bean was detected.",
                    "Password handling without an explicit encoder is unsafe and often misconfigured.",
                    "Define a PasswordEncoder bean, usually BCryptPasswordEncoder or DelegatingPasswordEncoder."
            ));
        }

        return List.of();
    }
}
