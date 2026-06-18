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
 * Detects JPA to-one relationships without explicit lazy loading.
 *
 * @author Simone Meneghetti
 */
public class JpaToOneLazyRelationshipRule implements SpringRule {

    @Override
    public String id() {
        return "SPR054_JPA_TO_ONE_RELATIONSHIP_SHOULD_BE_LAZY";
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
                        if (isToOne(annotation) && !annotation.toString().contains("LAZY")) {
                            findings.add(new Finding(
                                    id(),
                                    Severity.MAJOR,
                                    "JPA to-one relation does not declare LAZY fetching",
                                    file.relativePath(),
                                    AstUtils.line(field).orElse(null),
                                    "Field " + field.getVariables() + " uses " + annotation.getNameAsString() + " without fetch = FetchType.LAZY.",
                                    "ManyToOne and OneToOne are eager by default and may load unexpected object graphs.",
                                    "Set fetch = FetchType.LAZY and load required data through explicit queries, projections or entity graphs."
                            ));
                        }
                    }
                }
            }
        }

        return findings;
    }

    private boolean isToOne(AnnotationExpr annotation) {
        String name = annotation.getName().getIdentifier();
        return name.equals("ManyToOne") || name.equals("OneToOne") || annotation.getNameAsString().endsWith(".ManyToOne") || annotation.getNameAsString().endsWith(".OneToOne");
    }
}
