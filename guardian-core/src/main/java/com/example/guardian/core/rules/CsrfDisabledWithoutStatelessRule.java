package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects CSRF disablement without an explicit stateless session policy.
 *
 * @author p15518 - Simone Meneghetti
 */
public class CsrfDisabledWithoutStatelessRule implements SpringRule {

    @Override
    public String id() {
        return "SPR059_CSRF_DISABLED_WITHOUT_STATELESS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            String content = file.content();
            boolean csrfDisabled = content.contains("csrf") && (content.contains("disable()") || content.contains("AbstractHttpConfigurer::disable"));
            boolean stateless = content.contains("SessionCreationPolicy.STATELESS") || content.contains("STATELESS");
            if (csrfDisabled && !stateless) {
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "CSRF disabled without explicit stateless policy",
                        file.relativePath(),
                        1,
                        "Security configuration disables CSRF but does not declare SessionCreationPolicy.STATELESS.",
                        "Disabling CSRF is usually acceptable for truly stateless APIs, but risky for browser applications using cookies or sessions.",
                        "Declare the session policy explicitly and disable CSRF only for stateless APIs or narrowly scoped endpoints."
                ));
            }
        }

        return findings;
    }
}
