package com.example.guardian.core.model;

import java.util.List;

public record FindingGroup(
        String ruleId,
        Severity severity,
        String category,
        String title,
        long occurrences,
        List<AffectedComponent> affectedComponents,
        String whyItMatters,
        String suggestedFix,
        String explanation
) {
}
