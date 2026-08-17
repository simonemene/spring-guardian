package com.example.guardian.core.model;

import java.util.List;

/**
 * Scan integrity metadata. A report can contain useful findings even when a small part of the
 * project could not be parsed, but consumers must be able to see that limitation explicitly.
 *
 * @param totalJavaFiles Java files discovered
 * @param parsedJavaFiles Java files parsed successfully
 * @param parseFailures Java files that could not be parsed/read
 * @param rulesAttempted deterministic rules attempted
 * @param rulesSucceeded rules completed without throwing
 * @param ruleFailures rules that failed during execution
 * @param issues detailed integrity diagnostics
 */
public record ScanDiagnostics(
        int totalJavaFiles,
        int parsedJavaFiles,
        int parseFailures,
        int rulesAttempted,
        int rulesSucceeded,
        int ruleFailures,
        List<ScanDiagnostic> issues
) {
    public ScanDiagnostics {
        issues = issues == null ? List.of() : List.copyOf(issues);
    }

    /**
     * @return true only when all Java files were parsed and all rules completed
     */
    public boolean complete() {
        return parseFailures == 0 && ruleFailures == 0;
    }
}
