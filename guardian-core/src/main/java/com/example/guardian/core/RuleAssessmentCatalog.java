package com.example.guardian.core;

import com.example.guardian.core.model.DetectionType;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.RuleConfidence;

import java.util.Set;

/**
 * Assigns confidence and detection mechanics to grouped findings.
 * The mapping is intentionally conservative: advisory applicability is kept below
 * deterministic AST/configuration evidence even when both are useful.
 *
 * @author Simone Meneghetti
 */
final class RuleAssessmentCatalog {

    private static final Set<String> VERY_HIGH_AST = Set.of(
            "ASYNC101_INVALID_ASYNC_METHOD",
            "ASYNC102_SCHEDULED_METHOD_HAS_PARAMETERS",
            "WEB101_GET_WITH_REQUEST_BODY",
            "ARCH102_ENTITY_INJECTS_SPRING_BEAN",
            "SEC101_NOOP_PASSWORD_ENCODER",
            "SEC102_DEFAULT_PASSWORD_ENCODER",
            "SEC103_HARDCODED_SIGNING_SECRET",
            "SEC104_BCRYPT_LOW_STRENGTH",
            "JPA103_ENTITY_LOMBOK_DATA",
            "JPA104_MANY_TO_MANY_CASCADE_ALL",
            "BAT052_JOB_PARAMETER_WITHOUT_BATCH_SCOPE",
            "BAT043_RANDOMIZED_JOB_PARAMETER"
    );

    private static final Set<String> HIGH_AST = Set.of(
            "ARCH101_CONTROLLER_CALLS_CONTROLLER",
            "TX101_REMOTE_CALL_INSIDE_TRANSACTION",
            "TX102_READ_ONLY_TRANSACTION_WRITES",
            "TX103_TRANSACTIONAL_EXCEPTION_SWALLOWED",
            "JPA101_FIND_ALL_FILTER_IN_MEMORY",
            "JPA102_FLUSH_INSIDE_LOOP",
            "OBS101_SENSITIVE_DATA_LOGGED",
            "OBS102_MDC_WITHOUT_CLEANUP",
            "BAT051_RETRY_WITHOUT_BACKOFF",
            "SPR_ALT041_ASYNC_TRANSACTIONAL_SAME_METHOD",
            "SPR_ALT049_UNTYPED_CONTROLLER_RESPONSE",
            "SPR_ALT050_EVENT_PUBLISHED_INSIDE_TRANSACTION"
    );

    private RuleAssessmentCatalog() {
    }

    static Assessment assessment(Finding finding) {
        String id = finding == null || finding.ruleId() == null ? "" : finding.ruleId();
        if (VERY_HIGH_AST.contains(id)) {
            return new Assessment(RuleConfidence.VERY_HIGH, 97, DetectionType.AST_CORRELATION);
        }
        if (HIGH_AST.contains(id)) {
            return new Assessment(RuleConfidence.HIGH, 90, DetectionType.AST_CORRELATION);
        }
        if (id.startsWith("SPR_ALT")) {
            return new Assessment(RuleConfidence.MEDIUM, 78, DetectionType.SOURCE_PATTERN);
        }
        if (id.startsWith("CAP")) {
            return new Assessment(RuleConfidence.MEDIUM, 75, DetectionType.CAPABILITY_INFERENCE);
        }
        if (id.startsWith("ARCH")) {
            return new Assessment(RuleConfidence.HIGH, 86, DetectionType.ARCHITECTURE_ANALYSIS);
        }
        if (id.startsWith("CFG") || id.startsWith("SEC") || id.startsWith("BAT")
                || id.startsWith("POM") || id.startsWith("CLD") || id.startsWith("WEB")
                || id.startsWith("OBS") || id.startsWith("JPA") || id.startsWith("TX")
                || id.startsWith("ASYNC") || id.startsWith("ADV")) {
            return new Assessment(RuleConfidence.HIGH, 88, DetectionType.SOURCE_PATTERN);
        }
        return new Assessment(RuleConfidence.HIGH, 85, DetectionType.PROJECT_CONTEXT);
    }

    record Assessment(RuleConfidence confidence, int score, DetectionType detectionType) {
    }
}
