package com.example.guardian.core.model;

/**
 * Executive summary of the architecture scan.
 *
 * @param totalFindings total finding occurrences
 * @param criticalFindings critical finding occurrences
 * @param majorFindings high-severity finding occurrences
 * @param minorFindings medium-severity finding occurrences
 * @param infoFindings informational finding occurrences
 * @param riskLevel overall risk level
 * @param status stable status code
 * @param executiveSummary localized summary
 * @author Simone Meneghetti
 */
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
