package com.example.guardian.core.model;

/**
 * Report section that separates findings by Spring architecture area.
 *
 * @param code stable technical area code
 * @param name localized area name
 * @param description localized area description
 * @param findings total findings for this area
 * @param criticalFindings critical findings for this area
 * @param majorFindings high-severity findings for this area
 * @param readinessStatus stable area readiness status
 * @author Simone Meneghetti
 */
public record ArchitectureAreaReport(
        String code,
        String name,
        String description,
        long findings,
        long criticalFindings,
        long majorFindings,
        String readinessStatus
) {
}
