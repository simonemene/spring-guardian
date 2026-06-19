package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.List;

/**
 * Reuses a high-confidence detector but publishes the finding as a Spring Alternative advisor.
 *
 * @author Simone Meneghetti
 */
final class DelegatingSpringAlternativeRule implements SpringRule {

    private final SpringRule delegate;
    private final String id;
    private final Severity severity;
    private final String title;
    private final String whyItMatters;
    private final String suggestedFix;

    DelegatingSpringAlternativeRule(SpringRule delegate, String id, Severity severity, String title, String whyItMatters, String suggestedFix) {
        this.delegate = delegate;
        this.id = id;
        this.severity = severity;
        this.title = title;
        this.whyItMatters = whyItMatters;
        this.suggestedFix = suggestedFix;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        return delegate.evaluate(context).stream()
                .map(finding -> new Finding(
                        id,
                        severity == null ? finding.severity() : severity,
                        title,
                        finding.filePath(),
                        finding.line(),
                        finding.evidence(),
                        whyItMatters,
                        suggestedFix
                ))
                .toList();
    }
}
