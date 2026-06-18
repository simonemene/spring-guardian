package com.example.guardian.core.model;

/**
 * Component affected by a grouped architecture finding.
 *
 * @param type component type
 * @param name component display name
 * @param filePath relative file path
 * @param line source line when available
 * @param evidence technical evidence collected by the rule
 * @param codeSnippet source excerpt that triggered the finding, when available
 * @author Simone Meneghetti
 */
public record AffectedComponent(
        String type,
        String name,
        String filePath,
        Integer line,
        String evidence,
        String codeSnippet
) {
}
