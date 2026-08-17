package com.example.guardian.core.model;

/**
 * Confidence assigned to a finding detection independently from its severity.
 * Severity answers "how bad if true"; confidence answers "how certain is the detection".
 *
 * @author Simone Meneghetti
 */
public enum RuleConfidence {
    VERY_HIGH,
    HIGH,
    MEDIUM
}
