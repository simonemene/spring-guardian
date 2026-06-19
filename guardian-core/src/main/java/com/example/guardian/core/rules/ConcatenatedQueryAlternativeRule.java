package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects query strings built through concatenation.
 *
 * @author Simone Meneghetti
 */
final class ConcatenatedQueryAlternativeRule implements SpringRule {

    private static final Set<String> QUERY_METHODS = Set.of("createQuery", "createNativeQuery", "executeQuery", "prepareStatement");

    @Override
    public String id() {
        return "SPR_ALT012_CONCATENATED_QUERY_STRING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            for (AnnotationExpr annotation : file.compilationUnit().findAll(AnnotationExpr.class)) {
                if (isQueryAnnotation(annotation) && annotation.toString().contains("+")) {
                    findings.add(new Finding(
                            id(),
                            Severity.INFO,
                            "Query annotation uses string concatenation",
                            file.relativePath(),
                            AstUtils.line(annotation).orElse(null),
                            "Detected query annotation with concatenated string: " + trim(annotation.toString()),
                            "Concatenated query strings are harder to review and can hide unsafe parameters or vendor-specific SQL fragments.",
                            "Use bind parameters, repository methods, Specifications, Criteria API, Querydsl or a named query/projection."
                    ));
                }
            }

            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!QUERY_METHODS.contains(call.getNameAsString())) {
                    continue;
                }
                boolean concatenatedArgument = call.getArguments().stream().anyMatch(argument -> argument.toString().contains("+"));
                if (concatenatedArgument) {
                    findings.add(new Finding(
                            id(),
                            Severity.INFO,
                            "Query string built through concatenation",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected query call with concatenated argument: " + trim(call.toString()),
                            "Building SQL/JPQL by concatenating strings increases injection and maintainability risk.",
                            "Use bind parameters and Spring Data abstractions such as repository methods, @Query parameters, Specifications or Querydsl."
                    ));
                }
            }
        }

        return findings;
    }

    private boolean isQueryAnnotation(AnnotationExpr annotation) {
        String name = annotation.getName().getIdentifier();
        return name.equals("Query") || annotation.getNameAsString().endsWith(".Query");
    }

    private String trim(String value) {
        if (value.length() <= 180) {
            return value;
        }
        return value.substring(0, 177) + "...";
    }
}
