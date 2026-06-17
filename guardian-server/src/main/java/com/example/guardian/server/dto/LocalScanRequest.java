package com.example.guardian.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request used to scan a project directory already visible to the backend process.
 *
 * @param path absolute or mounted project directory path
 * @param projectType project type used to calibrate this stateless scan
 * @param architectureStyle expected architecture style used to calibrate this stateless scan
 * @param releaseTarget release target used by quality gates
 * @param knownIssuesAccepted true when the scan should be calibrated as a legacy baseline with known issues
 * @author p15518 - Simone Meneghetti
 */
@Schema(description = "Request used to scan a project directory already visible to the backend process.")
public record LocalScanRequest(
        @NotBlank
        @Schema(description = "Absolute or mounted project directory path.", example = "C:\\progetti\\spring-guardian")
        String path,
        @Schema(description = "Project type. Accepted values: WEB_API, BATCH, LIBRARY.", example = "WEB_API")
        String projectType,
        @Schema(description = "Architecture style. Accepted values: AUTO_DETECTED, LAYERED, DOMAIN_DRIVEN_DESIGN, HEXAGONAL, LEGACY_LAYERED.", example = "AUTO_DETECTED")
        String architectureStyle,
        @Schema(description = "Release target. Accepted values: PRODUCTION, INTERNAL, LEGACY_BASELINE.", example = "PRODUCTION")
        String releaseTarget,
        @Schema(description = "Use true for a legacy baseline scan with known issues.", example = "false")
        Boolean knownIssuesAccepted
) {
}
