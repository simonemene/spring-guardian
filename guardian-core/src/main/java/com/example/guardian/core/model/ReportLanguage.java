package com.example.guardian.core.model;

import java.util.Locale;

/**
 * Supported report languages for user-facing report texts.
 *
 * @author Simone Meneghetti
 */
public enum ReportLanguage {
    ITALIAN,
    ENGLISH;

    /**
     * Resolves a language value accepted by API, CLI or UI calls.
     *
     * @param value language code or label
     * @return resolved report language
     */
    public static ReportLanguage from(String value) {
        if (value == null || value.isBlank()) {
            return ITALIAN;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "en", "eng", "english" -> ENGLISH;
            case "it", "ita", "italian", "italiano" -> ITALIAN;
            default -> ITALIAN;
        };
    }
}
