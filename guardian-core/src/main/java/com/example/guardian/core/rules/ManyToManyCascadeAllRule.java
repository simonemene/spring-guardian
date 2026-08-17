package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects CascadeType.ALL on many-to-many associations.
 *
 * @author Simone Meneghetti
 */
public class ManyToManyCascadeAllRule implements SpringRule {

    @Override
    public String id() {
        return "JPA104_MANY_TO_MANY_CASCADE_ALL";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesJpa()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (FieldDeclaration field : file.compilationUnit().findAll(FieldDeclaration.class)) {
                AnnotationExpr annotation = field.getAnnotations().stream()
                        .filter(a -> a.getName().getIdentifier().equals("ManyToMany"))
                        .findFirst().orElse(null);
                if (annotation == null || !annotation.toString().contains("CascadeType.ALL")) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "CascadeType.ALL on @ManyToMany",
                        file.relativePath(),
                        AstUtils.line(annotation).orElse(null),
                        "@ManyToMany declares CascadeType.ALL.",
                        "Cascade ALL includes REMOVE. In a many-to-many association the related entities are usually shared, so deleting one aggregate can accidentally propagate deletion to shared rows.",
                        "Cascade only the operations that are truly owned by the aggregate (often PERSIST/MERGE) and manage link-table removal independently from deleting the related entity."
                ));
            }
        }
        return findings;
    }
}
