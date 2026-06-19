package com.example.guardian.cli;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.Severity;
import com.example.guardian.report.HtmlReportRenderer;
import com.example.guardian.report.ReportFormat;
import com.example.guardian.report.TextReportRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Picocli command that scans a Spring project and renders the architecture report.
 *
 * @author p15518 - Simone Meneghetti
 */
@Command(name = "scan", description = "Scan a Spring project")
public class ScanCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Project path to scan")
    private Path projectPath;

    @Option(names = "--format", description = "Output format: text, json or html", defaultValue = "text")
    private String format;

    @Option(names = "--output", description = "Write output to file")
    private Path output;

    @Option(names = "--fail-on", description = "Fail with exit code 2 on severity: critical, major, minor", defaultValue = "")
    private String failOn;

    @Option(names = "--language", description = "Report language: it or en", defaultValue = "it")
    private String language;

    @Option(names = "--project-type", description = "Project type: UNKNOWN, WEB_API, BATCH, LIBRARY", defaultValue = "UNKNOWN")
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
        Path root = validateProjectPath(projectPath);
        GuardianSettings settings = GuardianSettings.of(apiPrefix, maxControllerLines, maxControllerBranches);
        ProjectScanService scanService = new ProjectScanService(settings);
        ProjectProfile profile = ProjectProfile.from(projectType, architectureStyle, releaseTarget, knownIssuesAccepted);
        ArchitectureReviewReport report = scanService.scan(root, ReportLanguage.from(language), profile);

        ReportFormat resolvedFormat = ReportFormat.from(format);
        String rendered = switch (resolvedFormat) {
            case JSON -> toJson(report);
            case HTML -> new HtmlReportRenderer().render(report);
            case TEXT -> new TextReportRenderer().render(report);
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

    private Path validateProjectPath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Project path is required.");
        }
        Path normalized = path.toAbsolutePath().normalize();
        if (!Files.exists(normalized)) {
            throw new IllegalArgumentException("Project path does not exist: " + normalized);
        }
        if (!Files.isDirectory(normalized)) {
            throw new IllegalArgumentException("Project path is not a directory: " + normalized);
        }
        return normalized;
    }

    private String toJson(ArchitectureReviewReport report) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
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
