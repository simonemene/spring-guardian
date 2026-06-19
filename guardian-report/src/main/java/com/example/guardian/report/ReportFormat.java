package com.example.guardian.report;

import java.util.Locale;

/**
 * Supported rendering formats for Spring Guardian reports.
 *
 * @author p15518 - Simone Meneghetti
 */
public enum ReportFormat {
    TEXT,
    JSON,
    HTML;

    /**
     * Parses a user-facing report format.
     *
     * @param value format value from CLI or API
     * @return parsed format
     */
    public static ReportFormat from(String value) {
        if (value == null || value.isBlank()) {
            return TEXT;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "text", "txt" -> TEXT;
            case "json" -> JSON;
            case "html", "htm" -> HTML;
            default -> throw new IllegalArgumentException("Unsupported format: " + value + ". Supported formats: text, json, html.");
        };
    }
}
