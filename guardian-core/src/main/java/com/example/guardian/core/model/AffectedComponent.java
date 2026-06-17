package com.example.guardian.core.model;

public record AffectedComponent(
        String type,
        String name,
        String filePath,
        Integer line,
        String evidence
) {
}
