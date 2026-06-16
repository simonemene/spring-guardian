package com.example.guardian.core.model;

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
