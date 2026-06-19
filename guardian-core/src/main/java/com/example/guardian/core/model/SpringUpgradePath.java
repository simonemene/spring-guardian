package com.example.guardian.core.model;

import java.util.List;

/**
 * Deterministic upgrade path inferred from Java, Spring Boot and Jakarta signals.
 *
 * @author p15518 - Simone Meneghetti
 */
public record SpringUpgradePath(
        String currentJavaVersion,
        String currentSpringBootVersion,
        List<UpgradeStep> steps
) {
}
