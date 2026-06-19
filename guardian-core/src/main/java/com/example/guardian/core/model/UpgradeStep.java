package com.example.guardian.core.model;

import java.util.List;

/**
 * One step of the Spring upgrade path.
 *
 * @author p15518 - Simone Meneghetti
 */
public record UpgradeStep(
        int order,
        String title,
        String description,
        String risk,
        String springAlternative,
        List<String> evidence,
        String whyRecommended,
        List<String> actions,
        String effort,
        List<String> openRewriteRecipes
) {
    /**
     * Backward-compatible constructor used by older tests and report consumers.
     *
     * @param order step order
     * @param title step title
     * @param description description
     * @param risk risk level
     * @param springAlternative Spring alternative
     * @param evidence evidence
     */
    public UpgradeStep(
            int order,
            String title,
            String description,
            String risk,
            String springAlternative,
            List<String> evidence
    ) {
        this(
                order,
                title,
                description,
                risk,
                springAlternative,
                evidence == null ? List.of() : List.copyOf(evidence),
                description,
                List.of(description),
                "MEDIUM",
                List.of()
        );
    }

    /**
     * Normalizes nullable collections.
     */
    public UpgradeStep {
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        actions = actions == null ? List.of() : List.copyOf(actions);
        openRewriteRecipes = openRewriteRecipes == null ? List.of() : List.copyOf(openRewriteRecipes);
        whyRecommended = whyRecommended == null || whyRecommended.isBlank() ? description : whyRecommended;
        effort = effort == null || effort.isBlank() ? "MEDIUM" : effort;
    }
}
