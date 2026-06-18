package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;

import java.util.List;

/**
 * Deterministic rule evaluated against a scanned Spring project.
 *
 * @author Simone Meneghetti
 */
public interface SpringRule {

    /**
     * Returns the stable rule identifier.
     *
     * @return stable rule identifier
     */
    String id();

    /**
     * Evaluates the rule against the project context.
     *
     * @param context scanned project context
     * @return findings detected by this rule
     */
    List<Finding> evaluate(ProjectScanContext context);
}
