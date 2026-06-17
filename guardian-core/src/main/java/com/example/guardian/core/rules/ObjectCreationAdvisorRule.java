package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * Detects manual object creation that has a safer Spring or platform-managed alternative.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ObjectCreationAdvisorRule implements SpringRule {

    private final String id;
    private final Severity severity;
    private final Set<String> typeNames;
    private final String title;
    private final String whyItMatters;
    private final String suggestedFix;
    private final boolean skipBeanFactoryMethods;

    /**
     * Creates an advisor rule for selected object creation expressions.
     *
     * @param id rule identifier
     * @param severity rule severity
     * @param typeNames simple type names to detect
     * @param title technical title
     * @param whyItMatters explanation of the architectural risk
     * @param suggestedFix recommended Spring-oriented alternative
     * @param skipBeanFactoryMethods true when object creation is valid inside bean factory methods
     */
    public ObjectCreationAdvisorRule(String id, Severity severity, Set<String> typeNames, String title, String whyItMatters, String suggestedFix, boolean skipBeanFactoryMethods) {
        this.id = id;
        this.severity = severity;
        this.typeNames = Set.copyOf(typeNames);
        this.title = title;
        this.whyItMatters = whyItMatters;
        this.suggestedFix = suggestedFix;
        this.skipBeanFactoryMethods = skipBeanFactoryMethods;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            for (ObjectCreationExpr expression : file.compilationUnit().findAll(ObjectCreationExpr.class)) {
                if (!typeNames.contains(simpleName(expression.getTypeAsString()))) {
                    continue;
                }
                if (skipBeanFactoryMethods && isInsideBeanFactoryMethod(expression)) {
                    continue;
                }
                findings.add(new Finding(
                        id,
                        severity,
                        title,
                        file.relativePath(),
                        AstUtils.line(expression).orElse(null),
                        "Detected manual creation: " + expression,
                        whyItMatters,
                        suggestedFix
                ));
            }
        }

        return findings;
    }

    private boolean isInsideBeanFactoryMethod(ObjectCreationExpr expression) {
        return expression.findAncestor(MethodDeclaration.class)
                .map(method -> AstUtils.hasAnnotation(method, "Bean"))
                .orElse(false);
    }

    private String simpleName(String typeName) {
        int genericIndex = typeName.indexOf('<');
        String withoutGeneric = genericIndex >= 0 ? typeName.substring(0, genericIndex) : typeName;
        int dotIndex = withoutGeneric.lastIndexOf('.');
        return dotIndex >= 0 ? withoutGeneric.substring(dotIndex + 1) : withoutGeneric;
    }
}
