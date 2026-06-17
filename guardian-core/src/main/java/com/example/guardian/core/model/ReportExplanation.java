package com.example.guardian.core.model;

import java.util.List;

/**
 * User-facing explanation that describes how to read the report.
 *
 * @param scoreMeaning score explanation
 * @param severityMeaning severity explanation
 * @param howToUseThisReport report usage guidance
 * @param nextSteps suggested next steps
 * @author p15518 - Simone Meneghetti
 */
public record ReportExplanation(
        String scoreMeaning,
        String severityMeaning,
        String howToUseThisReport,
        List<String> nextSteps
) {
}
