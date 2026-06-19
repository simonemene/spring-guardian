package com.example.guardian.core.model;

import java.util.List;

/**
 * Exportable checklist item used to turn findings into an execution plan.
 *
 * @author p15518 - Simone Meneghetti
 */
public record ModernizationChecklistItem(
        String id,
        String title,
        boolean completed,
        Severity severity,
        List<String> files,
        String whyItMatters,
        String springAlternative,
        String suggestedChange,
        Effort effort,
        BusinessImpact businessImpact,
        int priority,
        List<String> relatedFindings
) {
}
