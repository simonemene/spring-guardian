package com.example.guardian.core.model;

import java.util.List;

/**
 * User-facing finding grouped by rule.
 *
 * @param ruleId rule identifier
 * @param severity highest severity for the rule group
 * @param category localized technical category
 * @param findingType deterministic type used by the UI to separate code, tests, POM, dependencies, configuration and advisor findings
 * @param findingTypeLabel localized finding type label
 * @param title localized title
 * @param occurrences number of affected components
 * @param affectedComponents affected classes or files
 * @param whyItMatters localized risk explanation
 * @param suggestedFix localized remediation
 * @param explanation localized group explanation
 * @param guidance action-oriented guidance used by the UI
 * @author Simone Meneghetti
 */
public record FindingGroup(
        String ruleId,
        Severity severity,
        String category,
        String findingType,
        String findingTypeLabel,
        String title,
        long occurrences,
        List<AffectedComponent> affectedComponents,
        String whyItMatters,
        String suggestedFix,
        String explanation,
        RuleGuidance guidance
) {

    /**
     * Backward-compatible constructor kept for older report/model tests and consumers
     * created before {@link RuleGuidance} became part of the public report model.
     *
     * @param ruleId rule identifier
     * @param severity highest severity for the rule group
     * @param category localized technical category
     * @param findingType deterministic finding type
     * @param findingTypeLabel localized finding type label
     * @param title localized title
     * @param occurrences number of affected components
     * @param affectedComponents affected classes or files
     * @param whyItMatters localized risk explanation
     * @param suggestedFix localized remediation
     * @param explanation localized group explanation
     */
    public FindingGroup(
            String ruleId,
            Severity severity,
            String category,
            String findingType,
            String findingTypeLabel,
            String title,
            long occurrences,
            List<?> affectedComponents,
            String whyItMatters,
            String suggestedFix,
            String explanation
    ) {
        this(
                ruleId,
                severity,
                category,
                findingType,
                findingTypeLabel,
                title,
                occurrences,
                toAffectedComponents(affectedComponents),
                whyItMatters,
                suggestedFix,
                explanation,
                null
        );
    }

    private static List<AffectedComponent> toAffectedComponents(List<?> components) {
        if (components == null || components.isEmpty()) {
            return List.of();
        }

        return components.stream()
                .filter(AffectedComponent.class::isInstance)
                .map(AffectedComponent.class::cast)
                .toList();
    }
}
