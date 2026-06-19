package com.example.guardian.core.model;

import java.util.List;

/**
 * Logical Spring module inferred from package names and Spring stereotypes.
 *
 * @author p15518 - Simone Meneghetti
 */
public record SpringModuleSummary(
        String name,
        String basePackage,
        long controllers,
        long services,
        long repositories,
        long entities,
        long configurations,
        long clients,
        long events,
        long batchComponents,
        List<String> risks
) {
}
