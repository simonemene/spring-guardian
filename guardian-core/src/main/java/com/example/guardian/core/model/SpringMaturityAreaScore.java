package com.example.guardian.core.model;

import java.util.List;

/**
 * Score for a Spring maturity area.
 *
 * @param code stable area code
 * @param name user-facing area name
 * @param score score from 0 to 100
 * @param status GOOD, WARNING or CRITICAL
 * @param drivers deterministic reasons that influenced the score
 * @param recommendations targeted improvement hints
 * @author p15518 - Simone Meneghetti
 */
public record SpringMaturityAreaScore(
        String code,
        String name,
        int score,
        String status,
        List<String> drivers,
        List<String> recommendations
) {
}
