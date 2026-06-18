package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for MissingTestsRule.
 *
 * @author Simone Meneghetti
 */
public class MissingTestsRule implements SpringRule {

    @Override
    public String id() {
        return "SPR012_MISSING_TESTS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.hasTests()) {
            return List.of(new Finding(
                    id(),
                    Severity.CRITICAL,
                    "No test sources detected",
                    null,
                    null,
                    "No files under src/test were found.",
                    "A Spring project without automated tests is risky, especially for service logic, persistence and controller contracts.",
                    "Add unit tests for services and integration tests for controllers/repositories."
            ));
        }
        return List.of();
    }
}
