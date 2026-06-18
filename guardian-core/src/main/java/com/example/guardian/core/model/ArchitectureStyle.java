package com.example.guardian.core.model;

import java.util.Locale;

/**
 * Architecture style used to calibrate deterministic architecture rules.
 *
 * @author Simone Meneghetti
 */
public enum ArchitectureStyle {
    AUTO_DETECTED,
    LAYERED,
    DOMAIN_DRIVEN_DESIGN,
    HEXAGONAL,
    LEGACY_LAYERED;

    /**
     * Resolves an architecture style accepted by API, CLI or UI calls.
     *
     * @param value architecture style code or label
     * @return resolved architecture style
     */
    public static ArchitectureStyle from(String value) {
        if (value == null || value.isBlank()) {
            return AUTO_DETECTED;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "AUTO", "AUTO_DETECTED", "DETECTED" -> AUTO_DETECTED;
            case "DDD", "DOMAIN_DRIVEN", "DOMAIN_DRIVEN_DESIGN" -> DOMAIN_DRIVEN_DESIGN;
            case "HEX", "HEXAGONAL", "PORTS_AND_ADAPTERS", "CLEAN_ARCHITECTURE" -> HEXAGONAL;
            case "LEGACY", "LEGACY_LAYERED" -> LEGACY_LAYERED;
            default -> AUTO_DETECTED;
        };
    }
}
