package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Spring Batch retry configuration without an explicit backoff policy.
 *
 * @author Simone Meneghetti
 */
public class BatchRetryWithoutBackoffRule implements SpringRule {

    @Override
    public String id() {
        return "BAT051_RETRY_WITHOUT_BACKOFF";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringBatch()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            List<MethodCallExpr> retries = file.compilationUnit().findAll(MethodCallExpr.class).stream()
                    .filter(call -> call.getNameAsString().equals("retry") || call.getNameAsString().equals("retryLimit"))
                    .toList();
            if (retries.isEmpty()) {
                continue;
            }
            boolean hasBackoff = file.compilationUnit().findAll(MethodCallExpr.class).stream()
                    .anyMatch(call -> call.getNameAsString().equals("backOffPolicy") || call.getNameAsString().equals("backOff"));
            if (hasBackoff) {
                continue;
            }
            MethodCallExpr retry = retries.get(0);
            findings.add(new Finding(
                    id(),
                    Severity.MINOR,
                    "Batch retry without explicit backoff",
                    file.relativePath(),
                    AstUtils.line(retry).orElse(null),
                    "Retry configuration is present without an explicit backoff policy in the same Batch configuration source.",
                    "Immediate retries can hammer a temporarily unavailable database/service and consume the retry budget before the dependency has time to recover.",
                    "Configure an appropriate bounded backoff for transient failures and keep retry limited to idempotent/recoverable operations."
            ));
        }
        return findings;
    }
}
