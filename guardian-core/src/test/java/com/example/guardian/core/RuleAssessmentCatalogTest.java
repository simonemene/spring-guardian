package com.example.guardian.core;

import com.example.guardian.core.model.DetectionType;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.RuleConfidence;
import com.example.guardian.core.model.Severity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleAssessmentCatalogTest {

    @Test
    void keepsSeverityAndDetectionConfidenceAsSeparateConcepts() {
        Finding deterministic = finding("SEC101_NOOP_PASSWORD_ENCODER", Severity.CRITICAL);
        RuleAssessmentCatalog.Assessment deterministicAssessment = RuleAssessmentCatalog.assessment(deterministic);

        assertEquals(RuleConfidence.VERY_HIGH, deterministicAssessment.confidence());
        assertEquals(97, deterministicAssessment.score());
        assertEquals(DetectionType.AST_CORRELATION, deterministicAssessment.detectionType());

        Finding advisor = finding("SPR_ALT040_HYSTRIX", Severity.INFO);
        RuleAssessmentCatalog.Assessment advisorAssessment = RuleAssessmentCatalog.assessment(advisor);

        assertEquals(RuleConfidence.MEDIUM, advisorAssessment.confidence());
        assertEquals(78, advisorAssessment.score());
        assertEquals(DetectionType.SOURCE_PATTERN, advisorAssessment.detectionType());
    }

    private Finding finding(String id, Severity severity) {
        return new Finding(id, severity, "title", "Example.java", 1, "evidence", "why", "fix");
    }
}
