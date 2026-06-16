package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.List;

public class MissingServiceLayerRule implements SpringRule {

    @Override
    public String id() {
        return "SPR005_MISSING_SERVICE_LAYER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        long controllerCount = context.javaFiles().stream()
                .flatMap(file -> file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream())
                .filter(clazz -> AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller"))
                .count();

        long repositoryCount = context.javaFiles().stream()
                .flatMap(file -> file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream())
                .filter(clazz -> AstUtils.hasAnnotation(clazz, "Repository") || clazz.getNameAsString().endsWith("Repository"))
                .count();

        long serviceCount = context.javaFiles().stream()
                .flatMap(file -> file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream())
                .filter(clazz -> AstUtils.hasAnnotation(clazz, "Service") || clazz.getNameAsString().endsWith("Service"))
                .count();

        if (controllerCount > 0 && repositoryCount > 0 && serviceCount == 0) {
            return List.of(new Finding(
                    id(),
                    Severity.MAJOR,
                    "No service layer detected",
                    null,
                    null,
                    "Project contains controllers and repositories but no service classes.",
                    "For non-trivial Spring applications, the service layer is usually the transactional/business boundary.",
                    "Introduce services to coordinate business logic and persistence access."
            ));
        }

        return List.of();
    }
}
