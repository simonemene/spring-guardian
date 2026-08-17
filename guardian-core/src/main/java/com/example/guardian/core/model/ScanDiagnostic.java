package com.example.guardian.core.model;

/**
 * Diagnostic produced when Spring Guardian cannot fully inspect part of a project.
 *
 * @param type diagnostic type such as PARSE_ERROR or RULE_ERROR
 * @param source scanner/rule identifier that produced the diagnostic
 * @param filePath affected project file when available
 * @param message safe diagnostic message with secrets redacted
 */
public record ScanDiagnostic(
        String type,
        String source,
        String filePath,
        String message
) {
}
