package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Spring Security usage without an explicit SecurityFilterChain.
 *
 * @author Simone Meneghetti
 */
public class SecurityFilterChainMissingRule implements SpringRule {

    @Override
    public String id() {
        return "SPR058_SECURITY_FILTER_CHAIN_MISSING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        if (!context.capabilities().usesSpringSecurity()) {
            return findings;
        }

        boolean hasFilterChain = context.javaFiles().stream()
                .anyMatch(file -> file.content().contains("SecurityFilterChain"));

        if (!hasFilterChain) {
            findings.add(new Finding(
                    id(),
                    Severity.CRITICAL,
                    "Spring Security dependency without explicit SecurityFilterChain",
                    null,
                    null,
                    "Spring Security is detected, but no SecurityFilterChain bean was found.",
                    "Without an explicit filter chain the security policy is unclear and may rely on defaults that do not match the application contract.",
                    "Define one or more SecurityFilterChain beans with endpoint authorization, CSRF policy, CORS policy, session policy and authentication mechanism."
            ));
        }

        return findings;
    }
}
