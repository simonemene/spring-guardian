package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects static Java API calls that usually bypass Spring configuration or lifecycle management.
 *
 * @author Simone Meneghetti
 */
public class StaticMethodAdvisorRule implements SpringRule {

    private final String id;
    private final Severity severity;
    private final String scopeName;
    private final Set<String> methodNames;
    private final String title;
    private final String whyItMatters;
    private final String suggestedFix;
    private final boolean testSourcesAllowed;

    /**
     * Creates an advisor rule for static method invocations.
     *
     * @param id rule identifier
     * @param severity rule severity
     * @param scopeName static scope name
     * @param methodNames method names to detect
     * @param title technical title
     * @param whyItMatters explanation of the architectural risk
     * @param suggestedFix recommended Spring-oriented alternative
     * @param testSourcesAllowed true when test sources should be evaluated
     */
    public StaticMethodAdvisorRule(String id, Severity severity, String scopeName, Set<String> methodNames, String title, String whyItMatters, String suggestedFix, boolean testSourcesAllowed) {
        this.id = id;
        this.severity = severity;
        this.scopeName = scopeName;
        this.methodNames = Set.copyOf(methodNames);
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

            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (call.getScope().isEmpty()) {
                    continue;
                }
                String scope = call.getScope().get().toString();
                if (!matchesScope(scope) || !methodNames.contains(call.getNameAsString())) {
                    continue;
                }
                findings.add(new Finding(
                        id,
                        severity,
                        title,
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "Detected call: " + call,
                        whyItMatters,
                        suggestedFix
                ));
            }
        }

        return findings;
    }

    private boolean matchesScope(String scope) {
        return scopeName.equals(scope) || scope.endsWith("." + scopeName);
    }
}
