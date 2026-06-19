package com.example.guardian.core.model;

import java.util.List;

/**
 * Dependency between two inferred logical modules.
 *
 * @author p15518 - Simone Meneghetti
 */
public record SpringModuleDependency(
        String fromModule,
        String toModule,
        int weight,
        List<String> examples
) {
}
