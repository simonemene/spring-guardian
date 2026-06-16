package com.example.guardian.cli;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.Severity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.Callable;

@Command(name = "scan", description = "Scan a Spring project")
public class ScanCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Project path to scan")
    private Path projectPath;

    @Option(names = "--format", description = "Output format: text or json", defaultValue = "text")
    private String format;

    @Option(names = "--output", description = "Write output to file")
    private Path output;

    @Option(names = "--fail-on", description = "Fail with exit code 2 on severity: critical, major, minor", defaultValue = "")
    private String failOn;

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
        ArchitectureReviewReport report = scanService.scan(projectPath);

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
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
    }

    private String toText(ArchitectureReviewReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("Spring Guardian Report\n");
        builder.append("======================\n\n");
        builder.append("Project: ").append(report.projectName()).append("\n");
        builder.append("Score: ").append(report.architectureScore()).append("/100\n");
        builder.append("Java files: ").append(report.scannedJavaFiles()).append("\n");
        builder.append("POM files: ").append(report.scannedPomFiles()).append("\n");
        builder.append("Findings: ").append(report.findings().size()).append("\n\n");

        report.findingsBySeverity().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().ordinal()))
                .forEach(e -> builder.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

        builder.append("\n");

        for (Finding finding : report.findings()) {
            builder.append("[").append(finding.severity()).append("] ")
                    .append(finding.ruleId()).append(" - ")
                    .append(finding.title()).append("\n");

            if (finding.filePath() != null) {
                builder.append("  File: ").append(finding.filePath());
                if (finding.line() != null) {
                    builder.append(":").append(finding.line());
                }
                builder.append("\n");
            }

            builder.append("  Evidence: ").append(finding.evidence()).append("\n");
            builder.append("  Why: ").append(finding.whyItMatters()).append("\n");
            builder.append("  Fix: ").append(finding.suggestedFix()).append("\n\n");
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
