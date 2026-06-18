package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for MissingRestControllerAdviceRule.
 *
 * @author Simone Meneghetti
 */
public class MissingRestControllerAdviceRule implements SpringRule {

    @Override
    public String id() {
        return "SPR010_MISSING_REST_CONTROLLER_ADVICE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        boolean hasRestControllers = context.javaFiles().stream()
                .flatMap(file -> file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream())
                .anyMatch(clazz -> AstUtils.hasAnnotation(clazz, "RestController"));

        if (hasRestControllers && !context.hasRestControllerAdvice()) {
            return List.of(new Finding(
                    id(),
                    Severity.MAJOR,
                    "Missing centralized REST exception handling",
                    null,
                    null,
                    "Project contains @RestController classes but no @RestControllerAdvice/@ControllerAdvice was found.",
                    "Without centralized exception handling, errors tend to be handled inconsistently with duplicated try/catch logic.",
                    "Introduce @RestControllerAdvice with @ExceptionHandler methods and a consistent error response model."
            ));
        }

        return List.of();
    }
}
