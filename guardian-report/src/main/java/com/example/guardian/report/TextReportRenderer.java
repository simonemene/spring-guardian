package com.example.guardian.report;

import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.Severity;

import java.util.Comparator;

/**
 * Compact developer-oriented text renderer for local CLI and CI logs.
 *
 * @author p15518 - Simone Meneghetti
 */
public class TextReportRenderer implements ReportRenderer {

    private static final int MAX_COMPONENTS_IN_TEXT_REPORT = 20;

    @Override
    public String render(ArchitectureReviewReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("Spring Guardian Report\n");
        builder.append("======================\n\n");
        builder.append("Project: ").append(report.projectName()).append("\n");
        builder.append("Score: ").append(report.architectureScore()).append("/100\n");
        builder.append("Risk: ").append(report.riskLevel()).append("\n");
        builder.append("Status: ").append(report.summary().status()).append("\n");
        builder.append("Release readiness: ").append(report.releaseReadiness().label()).append("\n");
        builder.append("Release explanation: ").append(report.releaseReadiness().explanation()).append("\n");
        builder.append("Profile: ").append(report.profile().projectType())
                .append(" / ").append(report.profile().architectureStyle())
                .append(" / ").append(report.profile().releaseTarget())
                .append(report.profile().knownIssuesAccepted() ? " / known issues accepted" : "")
                .append("\n");
        builder.append("Java files: ").append(report.scannedJavaFiles()).append("\n");
        builder.append("POM files: ").append(report.scannedPomFiles()).append("\n");
        builder.append("Rules executed: ").append(report.rulesExecuted()).append("\n");
        builder.append("Issue groups: ").append(report.findings().size()).append("\n");
        builder.append("Occurrences: ").append(report.summary().totalFindings()).append("\n");
        builder.append("Summary: ").append(report.summary().executiveSummary()).append("\n\n");

        report.findingsBySeverity().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().ordinal()))
                .forEach(e -> builder.append(e.getKey()).append(": ").append(e.getValue()).append(" occurrence(s)\n"));

        builder.append("\nQuality gates\n");
        builder.append("-------------\n");
        report.qualityGates().forEach(gate -> builder.append("[")
                .append(gate.status()).append("] ")
                .append(gate.name()).append(" - ")
                .append(gate.explanation()).append("\n"));

        builder.append("\nRecommended actions\n");
        builder.append("-------------------\n");
        report.recommendedActions().forEach(action -> builder.append(action.priority())
                .append(". [").append(action.severity()).append("] ")
                .append(action.ruleId()).append(" - ")
                .append(action.title()).append("\n")
                .append("   Where: ").append(action.location()).append("\n")
                .append("   Fix: ").append(action.action()).append("\n"));

        if (report.architectMode() != null) {
            builder.append("\nArchitect Mode\n");
            builder.append("--------------\n");
            builder.append("Fingerprint: ").append(report.architectMode().fingerprint().summary()).append("\n");
            builder.append("Spring Maturity Score: ").append(report.architectMode().maturityScore().overallScore()).append("/100 (")
                    .append(report.architectMode().maturityScore().status()).append(")\n");
            builder.append("Production Readiness: ").append(report.architectMode().productionReadiness().score()).append("/100 (")
                    .append(report.architectMode().productionReadiness().status()).append(")\n");
            builder.append("Architecture modules: ").append(report.architectMode().architectureMap().modules().size()).append("\n");
            builder.append("Cross-module dependencies: ").append(report.architectMode().architectureMap().dependencies().size()).append("\n");
            builder.append("Cycles: ").append(report.architectMode().architectureMap().cycles().size()).append("\n");
            builder.append("Modernization checklist items: ").append(report.architectMode().modernizationPlan().checklist().size()).append("\n");
            report.architectMode().maturityScore().areas().forEach(area -> builder.append("  - ")
                    .append(area.name()).append(": ").append(area.score()).append("/100 ").append(area.status()).append("\n"));
            builder.append("\nTop modernization checklist\n");
            report.architectMode().modernizationPlan().checklist().stream().limit(10).forEach(item -> builder.append("  [ ] ")
                    .append(item.priority()).append(". ").append(item.title()).append("\n")
                    .append("      ").append(item.suggestedChange()).append("\n"));
        }

        builder.append("\nGrouped findings\n");
        builder.append("----------------\n");

        for (FindingGroup group : report.findings()) {
            builder.append("[").append(group.severity()).append("] ")
                    .append(group.ruleId()).append(" - ")
                    .append(group.title()).append("\n");
            builder.append("  Category: ").append(group.category()).append("\n");
            builder.append("  Type: ").append(group.findingTypeLabel()).append("\n");
            builder.append("  Occurrences: ").append(group.occurrences()).append("\n");
            builder.append("  Why: ").append(group.whyItMatters()).append("\n");
            builder.append("  Fix: ").append(group.suggestedFix()).append("\n");
            if (group.guidance() != null && group.guidance().springAlternative() != null && !group.guidance().springAlternative().isBlank()) {
                builder.append("  Spring alternative: ").append(group.guidance().springAlternative()).append("\n");
            }
            if (group.guidance() != null && group.guidance().documentationUrl() != null && !group.guidance().documentationUrl().isBlank()) {
                builder.append("  Docs: ").append(group.guidance().documentationUrl()).append("\n");
            }
            builder.append("  Affected components:\n");

            int displayed = 0;
            for (AffectedComponent component : group.affectedComponents()) {
                if (displayed >= MAX_COMPONENTS_IN_TEXT_REPORT) {
                    break;
                }
                builder.append("    - ").append(component.type()).append(" ").append(component.name());
                if (component.filePath() != null && !component.filePath().isBlank()) {
                    builder.append(" (").append(component.filePath());
                    if (component.line() != null) {
                        builder.append(":").append(component.line());
                    }
                    builder.append(")");
                }
                builder.append("\n");
                builder.append("      Evidence: ").append(component.evidence()).append("\n");
                displayed++;
            }

            long hidden = group.occurrences() - displayed;
            if (hidden > 0) {
                builder.append("    ... and ").append(hidden).append(" more occurrence(s). Use --format json to inspect all affected components.\n");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
