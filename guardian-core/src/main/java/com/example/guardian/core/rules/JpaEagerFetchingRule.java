package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for JpaEagerFetchingRule.
 *
 * @author Simone Meneghetti
 */
public class JpaEagerFetchingRule implements SpringRule {

    private static final String[] RELATION_ANNOTATIONS = {
            "OneToOne", "OneToMany", "ManyToOne", "ManyToMany"
    };

    @Override
    public String id() {
        return "SPR048_JPA_EAGER_FETCHING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Entity")) {
                    continue;
                }

                for (FieldDeclaration field : clazz.getFields()) {
                    for (AnnotationExpr annotation : field.getAnnotations()) {
                        if (isRelation(annotation) && annotation.toString().contains("EAGER")) {
                            findings.add(new Finding(
                                    id(),
                                    Severity.MAJOR,
                                    "JPA relation uses eager fetching",
                                    file.relativePath(),
                                    AstUtils.line(field).orElse(null),
                                    "Field " + field.getVariables() + " has relation annotation: " + annotation,
                                    "Eager fetching can create heavy object graphs, unexpected joins, N+1 problems and slow API responses.",
                                    "Prefer LAZY fetching by default and load required graphs explicitly with projections, fetch joins or entity graphs."
                            ));
                        }
                    }
                }
            }
        }

        return findings;
    }

    private boolean isRelation(AnnotationExpr annotation) {
        for (String relation : RELATION_ANNOTATIONS) {
            if (annotation.getName().getIdentifier().equals(relation)
                    || annotation.getNameAsString().endsWith("." + relation)) {
                return true;
            }
        }
        return false;
    }
}
