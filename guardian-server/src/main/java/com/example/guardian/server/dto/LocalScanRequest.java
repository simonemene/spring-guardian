package com.example.guardian.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request used to scan a project directory already visible to the backend process.
 *
 * @param path absolute or mounted project directory path
 * @author p15518 - Simone Meneghetti
 */
@Schema(description = "Request used to scan a project directory already visible to the backend process.")
public record LocalScanRequest(
        @Schema(description = "Absolute or mounted project directory path.", example = "C:\\progetti\\spring-guardian")
        String path
) {
}
