package com.example.guardian.core.model;

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
