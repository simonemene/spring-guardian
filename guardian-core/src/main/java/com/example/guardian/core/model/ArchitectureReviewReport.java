package com.example.guardian.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Final structured report returned by Spring Guardian.
 *
 * @param projectName analyzed project name
 * @param scannedAt scan timestamp
 * @param projectRootPath absolute project root path used by the scan
 * @param profile scan profile selected for this analysis
 * @param capabilities detected Spring and architecture capabilities
 * @param summary executive report summary
 * @param releaseReadiness deterministic release readiness decision
 * @param architectureScore score from 0 to 100
 * @param riskLevel overall risk level
 * @param scannedJavaFiles number of Java files scanned
 * @param scannedPomFiles number of Maven POM files scanned
 * @param rulesExecuted number of rules executed
 * @param findingsBySeverity finding count by severity
 * @param findingsByCategory finding count by category
 * @param findingsByType finding count by deterministic technical type
 * @param architectureAreas findings grouped by Spring architecture area
 * @param qualityGates deterministic quality gates
 * @param recommendedActions prioritized recommended actions
 * @param explanation report reading explanation
 * @param findings findings grouped by rule
 * @author Simone Meneghetti
 */
public record ArchitectureReviewReport(
        String projectName,
        Instant scannedAt,
        String projectRootPath,
        ProjectProfile profile,
        ProjectCapabilities capabilities,
        ReportSummary summary,
        ReleaseReadiness releaseReadiness,
        int architectureScore,
        String riskLevel,
        long scannedJavaFiles,
        long scannedPomFiles,
        int rulesExecuted,
        Map<Severity, Long> findingsBySeverity,
        List<CategorySummary> findingsByCategory,
        List<CategorySummary> findingsByType,
        List<ArchitectureAreaReport> architectureAreas,
        List<QualityGate> qualityGates,
        List<RecommendedAction> recommendedActions,
        ReportExplanation explanation,
        List<FindingGroup> findings
) {
}
