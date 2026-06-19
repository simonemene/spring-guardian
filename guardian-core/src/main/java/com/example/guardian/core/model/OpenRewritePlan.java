package com.example.guardian.core.model;

import java.util.List;

/**
 * Exportable OpenRewrite plan generated as suggestions, without modifying code.
 *
 * @author p15518 - Simone Meneghetti
 */
public record OpenRewritePlan(
        String recipeName,
        String displayName,
        List<OpenRewriteSuggestion> suggestions,
        String yaml
) {
}
