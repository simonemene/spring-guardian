package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.List;

/**
 * Detects feature annotations that require an enabling Spring annotation in the project.
 *
 * @author p15518 - Simone Meneghetti
 */
public class MissingEnableAnnotationAdvisorRule implements SpringRule {

    private final String id;
    private final Severity severity;
    private final String featureAnnotation;
    private final String requiredAnnotation;
    private final String title;
    private final String whyItMatters;
    private final String suggestedFix;

    /**
     * Creates a rule that checks if a Spring feature annotation has its enabling annotation.
     *
     * @param id rule identifier
     * @param severity rule severity
     * @param featureAnnotation annotation that triggers the check
     * @param requiredAnnotation annotation that should exist in the project
     * @param title technical title
     * @param whyItMatters explanation of the architectural risk
     * @param suggestedFix recommended Spring-oriented alternative
     */
    public MissingEnableAnnotationAdvisorRule(String id, Severity severity, String featureAnnotation, String requiredAnnotation, String title, String whyItMatters, String suggestedFix) {
        this.id = id;
        this.severity = severity;
        this.featureAnnotation = featureAnnotation;
        this.requiredAnnotation = requiredAnnotation;
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
        boolean featureUsed = hasAnnotation(context, featureAnnotation);
        boolean requiredPresent = hasAnnotation(context, requiredAnnotation);
        if (!featureUsed || requiredPresent) {
            return List.of();
        }
        return List.of(new Finding(
                id,
                severity,
                title,
                null,
                null,
                "Detected @" + featureAnnotation + " without @" + requiredAnnotation,
                whyItMatters,
                suggestedFix
        ));
    }

    private boolean hasAnnotation(ProjectScanContext context, String annotationName) {
        for (JavaSourceFile file : context.javaFiles()) {
            for (AnnotationExpr annotation : file.compilationUnit().findAll(AnnotationExpr.class)) {
                if (annotationName.equals(simpleName(annotation.getNameAsString()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String simpleName(String name) {
        int dotIndex = name.lastIndexOf('.');
        return dotIndex >= 0 ? name.substring(dotIndex + 1) : name;
    }
}
