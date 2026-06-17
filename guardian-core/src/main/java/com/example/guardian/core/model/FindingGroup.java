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
        String explanation
) {
}
