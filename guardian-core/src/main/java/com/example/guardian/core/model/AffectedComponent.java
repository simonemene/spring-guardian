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

    /**
     * Backward-compatible constructor kept for older report/model tests and consumers
     * that were created before code snippets became part of the report model.
     *
     * @param type component type
     * @param name component display name
     * @param filePath relative file path
     * @param line source line when available
     * @param evidence technical evidence collected by the rule
     */
    public AffectedComponent(
            String type,
            String name,
            String filePath,
            Integer line,
            String evidence
    ) {
        this(type, name, filePath, line, evidence, "");
    }

    /**
     * Backward-compatible primitive line constructor.
     *
     * @param type component type
     * @param name component display name
     * @param filePath relative file path
     * @param line source line
     * @param evidence technical evidence collected by the rule
     */
    public AffectedComponent(
            String type,
            String name,
            String filePath,
            int line,
            String evidence
    ) {
        this(type, name, filePath, Integer.valueOf(line), evidence, "");
    }
}
