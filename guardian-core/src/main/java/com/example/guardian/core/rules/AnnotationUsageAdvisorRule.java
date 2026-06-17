package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects annotation usages that can usually be replaced by stronger Spring patterns.
 *
 * @author p15518 - Simone Meneghetti
 */
public class AnnotationUsageAdvisorRule implements SpringRule {

    private final String id;
    private final Severity severity;
    private final Set<String> annotationNames;
    private final String title;
    private final String whyItMatters;
    private final String suggestedFix;
    private final boolean testSourcesAllowed;

    /**
     * Creates an advisor rule for selected annotations.
     *
     * @param id rule identifier
     * @param severity rule severity
     * @param annotationNames simple annotation names to detect
     * @param title technical title
     * @param whyItMatters explanation of the architectural risk
     * @param suggestedFix recommended Spring-oriented alternative
     * @param testSourcesAllowed true when test sources should be evaluated
     */
    public AnnotationUsageAdvisorRule(String id, Severity severity, Set<String> annotationNames, String title, String whyItMatters, String suggestedFix, boolean testSourcesAllowed) {
        this.id = id;
        this.severity = severity;
        this.annotationNames = Set.copyOf(annotationNames);
        this.title = title;
        this.whyItMatters = whyItMatters;
        this.suggestedFix = suggestedFix;
        this.testSourcesAllowed = testSourcesAllowed;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (!testSourcesAllowed && SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            for (AnnotationExpr annotation : file.compilationUnit().findAll(AnnotationExpr.class)) {
                if (!annotationNames.contains(simpleName(annotation.getNameAsString()))) {
                    continue;
                }
                findings.add(new Finding(
                        id,
                        severity,
                        title,
                        file.relativePath(),
                        AstUtils.line(annotation).orElse(null),
                        "Detected annotation: @" + annotation.getNameAsString(),
                        whyItMatters,
                        suggestedFix
                ));
            }
        }
        return findings;
    }

    private String simpleName(String name) {
        int dotIndex = name.lastIndexOf('.');
        return dotIndex >= 0 ? name.substring(dotIndex + 1) : name;
    }
}
