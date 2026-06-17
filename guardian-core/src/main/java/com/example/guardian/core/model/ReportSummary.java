package com.example.guardian.core.model;

public record ReportSummary(
        int totalFindings,
        long criticalFindings,
        long majorFindings,
        long minorFindings,
        long infoFindings,
        String riskLevel,
        String status,
        String executiveSummary
) {
}
