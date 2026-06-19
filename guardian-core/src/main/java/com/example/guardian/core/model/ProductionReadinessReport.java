package com.example.guardian.core.model;

import java.util.List;

/**
 * Production readiness advisor for Spring Boot applications.
 *
 * @author p15518 - Simone Meneghetti
 */
public record ProductionReadinessReport(
        int score,
        String status,
        List<String> strengths,
        List<String> risks,
        List<ModernizationChecklistItem> requiredActions
) {
}
