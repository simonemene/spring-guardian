package com.example.guardian.core.model;

import java.util.List;

/**
 * One step of the Spring upgrade path.
 *
 * @author p15518 - Simone Meneghetti
 */
public record UpgradeStep(
        int order,
        String title,
        String description,
        String risk,
        String springAlternative,
        List<String> evidence
) {
}
