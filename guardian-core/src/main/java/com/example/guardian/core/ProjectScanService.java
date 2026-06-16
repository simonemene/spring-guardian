package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.example.guardian.core.rules.GuardianRules;
import com.example.guardian.core.rules.SpringRule;
import com.example.guardian.core.scanner.ProjectSourceScanner;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectScanService {

    private final ProjectSourceScanner scanner;
    private final List<SpringRule> rules;

    public ProjectScanService(GuardianSettings settings) {
        this.scanner = new ProjectSourceScanner();
        this.rules = GuardianRules.defaultRules(settings);
    }

    public ArchitectureReviewReport scan(Path root) {
        ProjectScanContext context = scanner.scan(root);

        List<Finding> findings = rules.stream()
                .flatMap(rule -> rule.evaluate(context).stream())
                .sorted(Comparator
                        .comparing(Finding::severity)
                        .thenComparing(Finding::ruleId)
                        .thenComparing(f -> f.filePath() == null ? "" : f.filePath())
                        .thenComparing(f -> f.line() == null ? 0 : f.line()))
                .toList();

        Map<Severity, Long> bySeverity = findings.stream()
                .collect(Collectors.groupingBy(Finding::severity, Collectors.counting()));

        return new ArchitectureReviewReport(
                root.getFileName() == null ? root.toString() : root.getFileName().toString(),
                Instant.now(),
                calculateScore(findings),
                context.javaFiles().size(),
                context.pomFiles().size(),
                findings,
                bySeverity
        );
    }

    private int calculateScore(List<Finding> findings) {
        int penalty = 0;
        for (Finding finding : findings) {
            penalty += switch (finding.severity()) {
                case CRITICAL -> 12;
                case MAJOR -> 6;
                case MINOR -> 2;
                case INFO -> 1;
            };
        }
        return Math.max(0, 100 - penalty);
    }
}
