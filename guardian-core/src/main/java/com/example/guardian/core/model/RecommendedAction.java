package com.example.guardian.core.model;

/**
 * Prioritized action derived from critical and high-severity findings.
 *
 * @param priority action priority
 * @param severity related severity
 * @param ruleId related rule identifier
 * @param title action title
 * @param location first affected location
 * @param reason reason for the action
 * @param action suggested action
 * @author p15518 - Simone Meneghetti
 */
public record RecommendedAction(
        int priority,
        Severity severity,
        String ruleId,
        String title,
        String location,
        String reason,
        String action
) {
}
