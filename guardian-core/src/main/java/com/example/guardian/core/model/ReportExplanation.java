package com.example.guardian.core.model;

import java.util.List;

public record ReportExplanation(
        String scoreMeaning,
        String severityMeaning,
        String howToUseThisReport,
        List<String> nextSteps
) {
}
