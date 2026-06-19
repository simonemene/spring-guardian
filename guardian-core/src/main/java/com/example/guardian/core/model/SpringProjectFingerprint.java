package com.example.guardian.core.model;

import java.util.List;
import java.util.Set;

/**
 * Enterprise-oriented fingerprint built on top of the existing deterministic project capabilities.
 *
 * @param buildTool detected build tool
 * @param javaVersion detected Java version when available
 * @param springBootVersion detected Spring Boot version when available
 * @param multiModule true when multiple Maven modules are detected
 * @param capabilities existing Spring Guardian capability model
 * @param springCapabilities normalized Spring ecosystem capabilities
 * @param detectedStarters detected Spring Boot starters and relevant libraries
 * @param detectedAnnotations relevant Spring annotations found in source code
 * @param detectedArchitecturalStyles inferred architecture styles
 * @param summary human-readable current project shape
 * @author p15518 - Simone Meneghetti
 */
public record SpringProjectFingerprint(
        String buildTool,
        String javaVersion,
        String springBootVersion,
        boolean multiModule,
        ProjectCapabilities capabilities,
        Set<SpringCapability> springCapabilities,
        List<String> detectedStarters,
        List<String> detectedAnnotations,
        List<String> detectedArchitecturalStyles,
        String summary
) {
}
