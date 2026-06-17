package com.example.guardian.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Final structured report returned by Spring Guardian.
 *
 * @param projectName analyzed project name
 * @param scannedAt scan timestamp
 * @param summary executive report summary
 * @param architectureScore score from 0 to 100
 * @param riskLevel overall risk level
 * @param scannedJavaFiles number of Java files scanned
 * @param scannedPomFiles number of Maven POM files scanned
 * @param rulesExecuted number of rules executed
 * @param findingsBySeverity finding count by severity
 * @param findingsByCategory finding count by category
 * @param recommendedActions prioritized recommended actions
 * @param explanation report reading explanation
 * @param findings findings grouped by rule
 * @author Simone Meneghetti
 */
public record ArchitectureReviewReport(
        String projectName,
        Instant scannedAt,
        ReportSummary summary,
        int architectureScore,
        String riskLevel,
        long scannedJavaFiles,
        long scannedPomFiles,
        int rulesExecuted,
        Map<Severity, Long> findingsBySeverity,
        List<CategorySummary> findingsByCategory,
        List<RecommendedAction> recommendedActions,
        ReportExplanation explanation,
        List<FindingGroup> findings
) {
}
