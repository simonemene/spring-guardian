package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

public class NamingConventionRule implements SpringRule {

    @Override
    public String id() {
        return "SPR016_NAMING_CONVENTION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                check(clazz, file, findings, "RestController", "Controller");
                check(clazz, file, findings, "Controller", "Controller");
                check(clazz, file, findings, "Service", "Service");
                check(clazz, file, findings, "Repository", "Repository");
                check(clazz, file, findings, "Component", "Component");
            }
        }

        return findings;
    }

    private void check(ClassOrInterfaceDeclaration clazz,
                       JavaSourceFile file,
                       List<Finding> findings,
                       String annotation,
                       String suffix) {
        if (AstUtils.hasAnnotation(clazz, annotation) && !clazz.getNameAsString().endsWith(suffix)) {
            findings.add(new Finding(
                    id(),
                    Severity.MINOR,
                    "Spring component naming convention not followed",
                    file.relativePath(),
                    AstUtils.line(clazz).orElse(null),
                    "Class " + clazz.getNameAsString() + " is annotated with @" + annotation + " but does not end with " + suffix + ".",
                    "Consistent naming helps developers understand layers and responsibilities quickly.",
                    "Rename the class using the configured suffix or disable this convention if your team uses another naming style."
            ));
        }
    }
}
