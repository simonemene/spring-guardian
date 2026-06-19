package com.example.guardian.core.model;

import java.util.List;

/**
 * Detected cycle between inferred logical modules.
 *
 * @author p15518 - Simone Meneghetti
 */
public record SpringArchitectureCycle(
        List<String> modules,
        String remediation
) {
}
