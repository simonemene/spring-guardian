package com.example.guardian.core.model;

import java.util.Locale;

/**
 * Release target used by quality gates to decide whether findings block release readiness.
 *
 * @author Simone Meneghetti
 */
public enum ReleaseTarget {
    PRODUCTION,
    INTERNAL,
    LEGACY_BASELINE;

    /**
     * Resolves a release target accepted by API, CLI or UI calls.
     *
     * @param value release target code or label
     * @return resolved release target
     */
    public static ReleaseTarget from(String value) {
        if (value == null || value.isBlank()) {
            return PRODUCTION;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "PROD", "PRODUCTION", "RELEASE" -> PRODUCTION;
            case "INTERNAL", "INTERNAL_TOOL", "TEAM" -> INTERNAL;
            case "LEGACY", "BASELINE", "LEGACY_BASELINE", "KNOWN_ISSUES" -> LEGACY_BASELINE;
            default -> PRODUCTION;
        };
    }
}
