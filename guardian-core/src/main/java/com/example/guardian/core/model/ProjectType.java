package com.example.guardian.core.model;

import java.util.Locale;

/**
 * Functional project type used to adjust release readiness and user guidance.
 *
 * @author p15518 - Simone Meneghetti
 */
public enum ProjectType {
    WEB_API,
    BATCH,
    LIBRARY,
    UNKNOWN;

    /**
     * Resolves a project type accepted by API, CLI or UI calls.
     *
     * @param value project type code or label
     * @return resolved project type
     */
    public static ProjectType from(String value) {
        if (value == null || value.isBlank()) {
            return WEB_API;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "API", "WEB", "WEB_API", "REST", "REST_API" -> WEB_API;
            case "BATCH", "SPRING_BATCH", "JOB" -> BATCH;
            case "LIB", "LIBRARY", "STARTER", "SDK" -> LIBRARY;
            default -> UNKNOWN;
        };
    }
}
