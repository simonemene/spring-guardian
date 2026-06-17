package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for NullReturnInServiceOrRepositoryRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class NullReturnInServiceOrRepositoryRule implements SpringRule {

    @Override
    public String id() {
        return "SPR025_NULL_RETURN_IN_REPOSITORY_OR_SERVICE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                boolean relevant = AstUtils.hasAnyAnnotation(clazz, "Service", "Repository")
                        || clazz.getNameAsString().endsWith("Service")
                        || clazz.getNameAsString().endsWith("Repository");

                if (!relevant) {
                    continue;
                }

                clazz.findAll(ReturnStmt.class).stream()
                        .filter(ret -> ret.getExpression().map(expr -> expr.toString().equals("null")).orElse(false))
                        .forEach(ret -> findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Null return in service/repository layer",
                                file.relativePath(),
                                AstUtils.line(ret).orElse(null),
                                "Detected 'return null' inside " + clazz.getNameAsString() + ".",
                                "Null returns from service/repository methods increase NPE risk and make API contracts unclear.",
                                "Prefer Optional, empty collections, or domain-specific exceptions depending on the use case."
                        )));
            }
        }

        return findings;
    }
}
