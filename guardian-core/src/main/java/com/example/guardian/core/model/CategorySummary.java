package com.example.guardian.core.model;

/**
 * Aggregated finding information for a technical category.
 *
 * @param category category label
 * @param findings total findings
 * @param criticalFindings critical findings
 * @param majorFindings major findings
 * @param minorFindings minor findings
 * @param infoFindings informational findings
 * @param explanation category explanation
 * @author p15518 - Simone Meneghetti
 */
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
