package com.example.guardian.core.model;

/**
 * Action-oriented guidance attached to a grouped finding.
 * It keeps the report concise by separating what was detected from the risk,
 * the expected Spring-oriented solution and optional documentation/examples.
 *
 * @param detectedProblem concise description of the detected problem
 * @param riskImpact practical risk introduced by the finding
 * @param recommendedApproach expected implementation approach
 * @param springAlternative Spring object, abstraction or pattern to use when applicable
 * @param documentationUrl official documentation URL or empty value when not applicable
 * @param beforeExample optional generic example, intentionally empty in UI reports unless explicitly safe
 * @param afterExample optional generic example, intentionally empty in UI reports unless explicitly safe
 * @author Simone Meneghetti
 */
public record RuleGuidance(
        String detectedProblem,
        String riskImpact,
        String recommendedApproach,
        String springAlternative,
        String documentationUrl,
        String beforeExample,
        String afterExample
) {
}
