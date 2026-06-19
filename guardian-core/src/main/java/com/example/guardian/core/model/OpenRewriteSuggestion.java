package com.example.guardian.core.model;

import java.util.List;

/**
 * OpenRewrite recipe suggestion related to detected modernization needs.
 *
 * @author p15518 - Simone Meneghetti
 */
public record OpenRewriteSuggestion(
        String recipe,
        String reason,
        List<String> relatedFindings
) {
}
