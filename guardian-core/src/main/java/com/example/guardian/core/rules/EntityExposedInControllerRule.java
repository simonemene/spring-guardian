package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Deterministic Spring Guardian rule implementation for EntityExposedInControllerRule.
 *
 * @author Simone Meneghetti
 */
public class EntityExposedInControllerRule implements SpringRule {

    @Override
    public String id() {
        return "SPR006_ENTITY_EXPOSED_IN_CONTROLLER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Set<String> entities = context.entityClassNames();
        if (entities.isEmpty()) {
            return List.of();
        }

        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!AstUtils.hasAnyAnnotation(method, "GetMapping", "PostMapping", "PutMapping", "PatchMapping", "DeleteMapping", "RequestMapping")) {
                        continue;
                    }

                    String returnType = method.getTypeAsString();
                    if (containsEntity(returnType, entities)) {
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "JPA entity exposed by REST controller",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " returns " + returnType + ".",
                                "Exposing persistence entities leaks internal schema, can trigger lazy-loading issues and couples API contracts to the database model.",
                                "Return a dedicated response DTO/record and map the entity inside the service/application layer."
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
