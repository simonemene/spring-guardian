package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detects constructor-injected dependency fields that are not final.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ConstructorDependencyFieldNotFinalRule implements SpringRule {

    @Override
    public String id() {
        return "SPR062_CONSTRUCTOR_DEPENDENCY_FIELD_NOT_FINAL";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.isSpringComponent(clazz)) {
                    continue;
                }

                Set<String> constructorAssignedFields = constructorAssignedFields(clazz);
                for (FieldDeclaration field : clazz.getFields()) {
                    if (field.isFinal()) {
                        continue;
                    }
                    field.getVariables().forEach(variable -> {
                        if (constructorAssignedFields.contains(variable.getNameAsString())) {
                            findings.add(new Finding(
                                    id(),
                                    Severity.MINOR,
                                    "Constructor-injected dependency field is not final",
                                    file.relativePath(),
                                    AstUtils.line(field).orElse(null),
                                    "Field " + variable.getNameAsString() + " is assigned by constructor but is not final.",
                                    "Constructor injection communicates mandatory dependencies better when dependency fields are immutable.",
                                    "Mark constructor-injected dependency fields as final or use @RequiredArgsConstructor with final fields."
                            ));
                        }
                    });
                }
            }
        }

        return findings;
    }

    private Set<String> constructorAssignedFields(ClassOrInterfaceDeclaration clazz) {
        Set<String> names = new HashSet<>();
        clazz.getConstructors().forEach(constructor -> {
            Set<String> parameters = new HashSet<>();
            for (Parameter parameter : constructor.getParameters()) {
                parameters.add(parameter.getNameAsString());
            }
            constructor.findAll(AssignExpr.class).forEach(assign -> {
                String target = assign.getTarget().toString();
                String value = assign.getValue().toString();
                if (target.startsWith("this.") && parameters.contains(value)) {
                    names.add(target.substring("this.".length()));
                }
            });
        });
        return names;
    }
}
