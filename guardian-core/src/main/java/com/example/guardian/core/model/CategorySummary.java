package com.example.guardian.core.model;

public record CategorySummary(
        String category,
        long findings,
        long criticalFindings,
        long majorFindings,
        long minorFindings,
        long infoFindings,
        String explanation
) {
}
