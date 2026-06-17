package com.example.guardian.core.model;

import java.util.List;

/**
 * Release readiness decision produced by deterministic quality gates.
 *
 * @param status stable release readiness status
 * @param label localized release readiness label
 * @param explanation localized release readiness explanation
 * @param releasable true when the project can be considered releasable by the selected profile
 * @param blockers localized blocking reasons
 * @param warnings localized non-blocking warnings
 * @author p15518 - Simone Meneghetti
 */
public record ReleaseReadiness(
        String status,
        String label,
        String explanation,
        boolean releasable,
        List<String> blockers,
        List<String> warnings
) {
}
