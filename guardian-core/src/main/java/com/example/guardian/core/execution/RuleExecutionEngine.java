package com.example.guardian.core.execution;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.ScanDiagnostic;
import com.example.guardian.core.rules.SpringRule;
import com.example.guardian.core.security.SensitiveDataRedactor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Executes deterministic rules independently so one failing rule cannot invalidate the whole scan. */
public final class RuleExecutionEngine {

    private final List<SpringRule> rules;

    public RuleExecutionEngine(List<SpringRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public RuleExecutionResult execute(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        List<ScanDiagnostic> diagnostics = new ArrayList<>();
        int succeeded = 0;

        for (SpringRule rule : rules) {
            try {
                List<Finding> evaluated = rule.evaluate(context);
                if (evaluated != null) {
                    findings.addAll(evaluated);
                }
                succeeded++;
            } catch (RuntimeException exception) {
                diagnostics.add(new ScanDiagnostic(
                        "RULE_ERROR",
                        rule.id(),
                        null,
                        safeMessage(exception)
                ));
            }
        }

        findings.sort(Comparator
                .comparing(Finding::severity)
                .thenComparing(Finding::ruleId)
                .thenComparing(finding -> finding.filePath() == null ? "" : finding.filePath())
                .thenComparing(finding -> finding.line() == null ? 0 : finding.line()));

        return new RuleExecutionResult(
                List.copyOf(findings),
                rules.size(),
                succeeded,
                rules.size() - succeeded,
                List.copyOf(diagnostics)
        );
    }

    public int ruleCount() {
        return rules.size();
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        message = SensitiveDataRedactor.redact(message);
        return message.length() <= 500 ? message : message.substring(0, 497) + "...";
    }

    public record RuleExecutionResult(
            List<Finding> findings,
            int attempted,
            int succeeded,
            int failed,
            List<ScanDiagnostic> diagnostics
    ) {
    }
}
