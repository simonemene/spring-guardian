package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for ControllerInjectsRepositoryRule.
 *
 * @author Simone Meneghetti
 */
public class ControllerInjectsRepositoryRule implements SpringRule {

    @Override
    public String id() {
        return "SPR003_CONTROLLER_INJECTS_REPOSITORY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                clazz.getFields().forEach(field -> inspectField(file, field, findings));
                clazz.getConstructors().forEach(constructor -> inspectConstructor(file, constructor, findings));
            }
        }

        return findings;
    }

    private void inspectField(JavaSourceFile file, FieldDeclaration field, List<Finding> findings) {
        field.getVariables().forEach(variable -> {
            String type = variable.getTypeAsString();
            if (isPersistenceDependency(type)) {
                findings.add(finding(file, AstUtils.line(field).orElse(null), type));
            }
        });
    }

    private void inspectConstructor(JavaSourceFile file, ConstructorDeclaration constructor, List<Finding> findings) {
        for (Parameter parameter : constructor.getParameters()) {
            String type = parameter.getTypeAsString();
            if (isPersistenceDependency(type)) {
                findings.add(finding(file, AstUtils.line(parameter).orElse(null), type));
            }
        }
    }

    private Finding finding(JavaSourceFile file, Integer line, String type) {
        return new Finding(
                id(),
                Severity.MAJOR,
                "Controller depends directly on persistence layer",
                file.relativePath(),
                line,
                "Controller dependency type: " + type,
                "A REST controller should orchestrate HTTP request/response mapping, not bypass the service layer.",
                "Introduce a Service and move business/persistence orchestration out of the controller."
        );
    }

    private boolean isPersistenceDependency(String type) {
        return type.endsWith("Repository")
                || type.contains("Repository<")
                || type.equals("JdbcTemplate")
                || type.equals("NamedParameterJdbcTemplate")
                || type.equals("EntityManager");
    }
}
