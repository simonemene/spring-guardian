package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for RequestBodyWithoutValidationRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class RequestBodyWithoutValidationRule implements SpringRule {

    @Override
    public String id() {
        return "SPR023_REQUEST_BODY_WITHOUT_VALIDATION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (Parameter parameter : file.compilationUnit().findAll(Parameter.class)) {
                if (AstUtils.hasAnnotation(parameter, "RequestBody")
                        && !AstUtils.hasAnyAnnotation(parameter, "Valid", "Validated")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "@RequestBody without validation",
                            file.relativePath(),
                            AstUtils.line(parameter).orElse(null),
                            "Parameter " + parameter.getNameAsString() + " is annotated with @RequestBody but not @Valid/@Validated.",
                            "Request DTOs should usually be validated at the controller boundary.",
                            "Add @Valid or @Validated and Bean Validation annotations on the request DTO."
                    ));
                }
            }
        }

        return findings;
    }
}
