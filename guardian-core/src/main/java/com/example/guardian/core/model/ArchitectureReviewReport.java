package com.example.guardian.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ArchitectureReviewReport(
        String projectName,
        Instant scannedAt,
        int architectureScore,
        long scannedJavaFiles,
        long scannedPomFiles,
        List<Finding> findings,
        Map<Severity, Long> findingsBySeverity
) {
}
