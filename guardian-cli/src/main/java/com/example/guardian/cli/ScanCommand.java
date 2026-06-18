package com.example.guardian.cli;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.Severity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.Callable;

/**
 * Picocli command that scans a Spring project and renders the architecture report.
 *
 * @author Simone Meneghetti
 */
@Command(name = "scan", description = "Scan a Spring project")
public class ScanCommand implements Callable<Integer> {

    private static final int MAX_COMPONENTS_IN_TEXT_REPORT = 20;

    @Parameters(index = "0", description = "Project path to scan")
    private Path projectPath;

    @Option(names = "--format", description = "Output format: text or json", defaultValue = "text")
    private String format;

    @Option(names = "--output", description = "Write output to file")
    private Path output;

    @Option(names = "--fail-on", description = "Fail with exit code 2 on severity: critical, major, minor", defaultValue = "")
    private String failOn;

    @Option(names = "--language", description = "Report language: it or en", defaultValue = "it")
    private String language;

    @Option(names = "--project-type", description = "Project type: WEB_API, BATCH, LIBRARY", defaultValue = "WEB_API")
    private String projectType;

    @Option(names = "--architecture-style", description = "Architecture style: AUTO_DETECTED, LAYERED, DOMAIN_DRIVEN_DESIGN, HEXAGONAL, LEGACY_LAYERED", defaultValue = "AUTO_DETECTED")
    private String architectureStyle;

    @Option(names = "--release-target", description = "Release target: PRODUCTION, INTERNAL, LEGACY_BASELINE", defaultValue = "PRODUCTION")
    private String releaseTarget;

    @Option(names = "--known-issues", description = "Calibrate scan as legacy baseline with known issues")
    private boolean knownIssuesAccepted;

    @Option(names = "--api-prefix", description = "Required REST API prefix", defaultValue = "/api/v1")
    private String apiPrefix;

    @Option(names = "--max-controller-lines", description = "Max lines for controller request method", defaultValue = "35")
    private int maxControllerLines;

    @Option(names = "--max-controller-branches", description = "Max branching statements for controller request method", defaultValue = "8")
    private int maxControllerBranches;

    @Override
    public Integer call() throws Exception {
        GuardianSettings settings = GuardianSettings.of(apiPrefix, maxControllerLines, maxControllerBranches);
        ProjectScanService scanService = new ProjectScanService(settings);
        ProjectProfile profile = ProjectProfile.from(projectType, architectureStyle, releaseTarget, knownIssuesAccepted);
        ArchitectureReviewReport report = scanService.scan(projectPath, ReportLanguage.from(language), profile);

        String rendered = switch (format.toLowerCase()) {
            case "json" -> toJson(report);
            case "text" -> toText(report);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };

        if (output != null) {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            Files.writeString(output, rendered);
        } else {
            System.out.println(rendered);
        }

        if (shouldFail(report)) {
            return 2;
        }

        return 0;
    }

    private String toJson(ArchitectureReviewReport report) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
    }

    private String toText(ArchitectureReviewReport report) {
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

        builder.append("\nGrouped findings\n");
        builder.append("----------------\n");

        for (FindingGroup group : report.findings()) {
            builder.append("[").append(group.severity()).append("] ")
                    .append(group.ruleId()).append(" - ")
                    .append(group.title()).append("\n");
            builder.append("  Category: ").append(group.category()).append("\n");
            builder.append("  Occurrences: ").append(group.occurrences()).append("\n");
            builder.append("  Why: ").append(group.whyItMatters()).append("\n");
            builder.append("  Fix: ").append(group.suggestedFix()).append("\n");
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

    private boolean shouldFail(ArchitectureReviewReport report) {
        if (failOn == null || failOn.isBlank()) {
            return false;
        }

        Severity threshold = switch (failOn.toLowerCase()) {
            case "critical" -> Severity.CRITICAL;
            case "major" -> Severity.MAJOR;
            case "minor" -> Severity.MINOR;
            default -> throw new IllegalArgumentException("Unsupported fail-on severity: " + failOn);
        };

        return report.findings().stream()
                .anyMatch(finding -> finding.severity().ordinal() <= threshold.ordinal());
    }
}
