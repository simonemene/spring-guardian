package com.example.guardian.cli;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.ModernizationChecklistItem;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.report.HtmlReportRenderer;
import com.example.guardian.report.TextReportRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Architect Mode command: turns a Spring scan into a modernization roadmap.
 *
 * @author p15518 - Simone Meneghetti
 */
@Command(name = "architect", description = "Generate Spring Architecture Map, Maturity Score and Modernization Plan")
public class ArchitectCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Project path to analyze")
    private Path projectPath;

    @Option(names = "--format", description = "Output format: text, json, html or markdown", defaultValue = "html")
    private String format;

    @Option(names = "--output", description = "Write the main Architect Mode output to file")
    private Path output;

    @Option(names = "--export-checklist", description = "Export modernization checklist JSON")
    private Path exportChecklist;

    @Option(names = "--export-mermaid", description = "Export architecture map as Mermaid .mmd")
    private Path exportMermaid;

    @Option(names = "--export-openrewrite", description = "Export OpenRewrite recipe suggestions YAML")
    private Path exportOpenRewrite;

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

    @Override
    public Integer call() throws Exception {
        Path root = validateProjectPath(projectPath);
        ProjectScanService scanService = new ProjectScanService(GuardianSettings.defaults());
        ProjectProfile profile = ProjectProfile.from(projectType, architectureStyle, releaseTarget, knownIssuesAccepted);
        ArchitectureReviewReport report = scanService.scan(root, ReportLanguage.from(language), profile);

        writeExports(report);

        String rendered = switch (format.toLowerCase()) {
            case "json" -> toJson(report.architectMode());
            case "html" -> new HtmlReportRenderer().render(report);
            case "markdown", "md" -> report.architectMode().modernizationPlan().markdown();
            case "text" -> new TextReportRenderer().render(report);
            default -> throw new IllegalArgumentException("Unsupported architect output format: " + format);
        };

        if (output != null) {
            writeString(output, rendered);
        } else {
            System.out.println(rendered);
        }

        return 0;
    }

    private void writeExports(ArchitectureReviewReport report) throws Exception {
        if (exportChecklist != null) {
            List<ModernizationChecklistItem> checklist = report.architectMode().modernizationPlan().checklist();
            writeString(exportChecklist, toJson(checklist));
        }
        if (exportMermaid != null) {
            writeString(exportMermaid, report.architectMode().architectureMap().mermaidDiagram());
        }
        if (exportOpenRewrite != null) {
            writeString(exportOpenRewrite, report.architectMode().openRewritePlan().yaml());
        }
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

    private void writeString(Path path, String value) throws Exception {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, value == null ? "" : value);
    }

    private String toJson(Object value) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }
}
