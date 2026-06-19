package com.example.guardian.core.model;

import java.util.List;

/**
 * Roadmap that transforms a scan into a Spring modernization path.
 *
 * @author p15518 - Simone Meneghetti
 */
public record SpringModernizationPlan(
        List<ModernizationChecklistItem> quickWins,
        List<ModernizationChecklistItem> architecturalRefactorings,
        List<ModernizationChecklistItem> productionReadinessFixes,
        List<ModernizationChecklistItem> upgradePath,
        List<ModernizationChecklistItem> checklist,
        String markdown
) {
}
