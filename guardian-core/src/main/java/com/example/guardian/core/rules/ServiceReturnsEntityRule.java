package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Deterministic Spring Guardian rule implementation for ServiceReturnsEntityRule.
 *
 * @author Simone Meneghetti
 */
public class ServiceReturnsEntityRule implements SpringRule {

    @Override
    public String id() {
        return "SPR019_SERVICE_RETURNS_ENTITY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Set<String> entities = context.entityClassNames();
        if (entities.isEmpty()) {
            return List.of();
        }

        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Service")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    String returnType = method.getTypeAsString();
                    if (containsEntity(returnType, entities)) {
                        findings.add(new Finding(
                                id(),
                                Severity.MINOR,
                                "Service method returns JPA entity",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " returns " + returnType + ".",
                                "For API-facing flows, returning entities from services often leaks persistence concerns upward.",
                                "Consider returning DTOs/application models from service methods used by controllers."
                        ));
                    }
                }
            }
        }

        return findings;
    }

    private boolean containsEntity(String type, Set<String> entities) {
        return entities.stream().anyMatch(entity ->
                type.equals(entity)
                        || type.contains("<" + entity + ">")
                        || type.contains("," + entity + ">")
                        || type.contains(" " + entity)
        );
    }
}
