package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

public class GodClassRule implements SpringRule {

    private final GuardianSettings settings;

    public GodClassRule(GuardianSettings settings) {
        this.settings = settings;
    }

    @Override
    public String id() {
        return "SPR030_GOD_CLASS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                int lines = AstUtils.nodeLines(clazz);
                int methods = clazz.getMethods().size();

                if (lines > settings.maxClassLines() || methods > settings.maxMethodsPerClass()) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Large class / possible God Class",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Class " + clazz.getNameAsString() + " has " + lines + " lines and " + methods + " methods.",
                            "Very large classes usually collect unrelated responsibilities and are harder to test safely.",
                            "Split the class by responsibility and move cohesive operations into dedicated services/components."
                    ));
                }
            }
        }

        return findings;
    }
}
