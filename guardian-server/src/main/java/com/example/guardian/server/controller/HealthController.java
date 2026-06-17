package com.example.guardian.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that exposes backend health information.
 *
 * @author Simone Meneghetti
 */
@RestController
@Tag(name = "Health", description = "Backend health endpoint.")
public class HealthController {

    /**
     * Returns the current backend status.
     *
     * @return backend status
     */
    @Operation(summary = "Read backend health status")
    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
