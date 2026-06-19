package com.example.guardian.core.model;

import java.util.List;

/**
 * Spring-native maturity score: not generic quality, but how well the project uses Spring capabilities.
 *
 * @param overallScore global score from 0 to 100
 * @param status GOOD, WARNING or CRITICAL
 * @param areas area-level scores
 * @param strengths positive Spring signals
 * @param weakAreas areas that should be improved first
 * @author p15518 - Simone Meneghetti
 */
public record SpringMaturityScore(
        int overallScore,
        String status,
        List<SpringMaturityAreaScore> areas,
        List<String> strengths,
        List<String> weakAreas
) {
}
