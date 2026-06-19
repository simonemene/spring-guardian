package com.example.guardian.core.model;

/**
 * Spring Guardian Architect Mode output attached to the scan report.
 *
 * @author p15518 - Simone Meneghetti
 */
public record ArchitectModeReport(
        SpringProjectFingerprint fingerprint,
        SpringMaturityScore maturityScore,
        SpringArchitectureMap architectureMap,
        SpringModernizationPlan modernizationPlan,
        ProductionReadinessReport productionReadiness,
        SpringUpgradePath upgradePath,
        OpenRewritePlan openRewritePlan
) {
}
