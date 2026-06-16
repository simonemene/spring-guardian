package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

import java.util.ArrayList;
import java.util.List;

public class TooManyDependenciesRule implements SpringRule {

    private final GuardianSettings settings;

    public TooManyDependenciesRule(GuardianSettings settings) {
        this.settings = settings;
    }

    @Override
    public String id() {
        return "SPR029_TOO_MANY_DEPENDENCIES";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                int maxConstructorParams = clazz.getConstructors().stream()
                        .mapToInt(constructor -> constructor.getParameters().size())
                        .max()
                        .orElse(0);

                if (maxConstructorParams > settings.maxInjectedDependencies()) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Class has too many injected dependencies",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Class " + clazz.getNameAsString() + " has a constructor with " + maxConstructorParams + " parameters.",
                            "Too many dependencies often indicate too many responsibilities or missing domain/application services.",
                            "Split responsibilities, introduce smaller collaborators or group related behavior behind a cohesive facade."
                    ));
                }
            }
        }

        return findings;
    }
}
