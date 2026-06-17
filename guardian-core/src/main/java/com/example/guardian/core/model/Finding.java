package com.example.guardian.core.model;

/**
 * Single raw finding produced by one deterministic rule.
 *
 * @param ruleId rule identifier
 * @param severity finding severity
 * @param title technical title
 * @param filePath relative file path
 * @param line source line when available
 * @param evidence technical evidence
 * @param whyItMatters explanation of the risk
 * @param suggestedFix recommended remediation
 * @author p15518 - Simone Meneghetti
 */
public record Finding(
        String ruleId,
        Severity severity,
        String title,
        String filePath,
        Integer line,
        String evidence,
        String whyItMatters,
        String suggestedFix
) {
}
