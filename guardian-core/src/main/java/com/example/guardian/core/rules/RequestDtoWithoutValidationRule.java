package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.RecordDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for RequestDtoWithoutValidationRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class RequestDtoWithoutValidationRule implements SpringRule {

    @Override
    public String id() {
        return "SPR024_REQUEST_DTO_WITHOUT_VALIDATION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                String name = clazz.getNameAsString();
                if (isRequestDtoName(name) && clazz.getAnnotations().isEmpty()
                        && clazz.findAll(com.github.javaparser.ast.expr.AnnotationExpr.class).stream().noneMatch(this::isValidationAnnotation)) {
                    findings.add(finding(file, AstUtils.line(clazz).orElse(null), name));
                }
            });

            file.compilationUnit().findAll(RecordDeclaration.class).forEach(record -> {
                String name = record.getNameAsString();
                if (isRequestDtoName(name)
                        && record.findAll(com.github.javaparser.ast.expr.AnnotationExpr.class).stream().noneMatch(this::isValidationAnnotation)) {
                    findings.add(finding(file, AstUtils.line(record).orElse(null), name));
                }
            });
        }

        return findings;
    }

    private boolean isRequestDtoName(String name) {
        return name.endsWith("Request") || name.endsWith("RequestDto") || name.endsWith("Command");
    }

    private boolean isValidationAnnotation(com.github.javaparser.ast.expr.AnnotationExpr annotation) {
        String name = annotation.getName().getIdentifier();
        return name.startsWith("Not")
                || name.equals("Email")
                || name.equals("Size")
                || name.equals("Min")
                || name.equals("Max")
                || name.equals("Pattern")
                || name.equals("Positive")
                || name.equals("Past")
                || name.equals("Future")
                || name.equals("Valid");
    }

    private Finding finding(JavaSourceFile file, Integer line, String name) {
        return new Finding(
                id(),
                Severity.MINOR,
                "Request DTO without Bean Validation annotations",
                file.relativePath(),
                line,
                "DTO " + name + " appears to be a request model but no validation annotations were found.",
                "Validation close to the API boundary reduces invalid state and duplicated manual checks.",
                "Add Bean Validation annotations such as @NotBlank, @NotNull, @Email, @Size, etc."
        );
    }
}
