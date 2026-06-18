package com.example.guardian.core.model;

/**
 * Deterministic quality gate used to explain release readiness.
 *
 * @param code stable quality gate code
 * @param name localized quality gate name
 * @param status stable quality gate status
 * @param explanation localized explanation
 * @param required true when the gate can block release readiness
 * @param failingFindings number of findings that failed the gate
 * @author Simone Meneghetti
 */
public record QualityGate(
        String code,
        String name,
        String status,
        String explanation,
        boolean required,
        long failingFindings
) {
}
