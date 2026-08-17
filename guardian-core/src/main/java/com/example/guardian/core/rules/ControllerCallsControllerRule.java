package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Detects controller-to-controller orchestration and calls from one endpoint method to another endpoint method.
 *
 * @author Simone Meneghetti
 */
public class ControllerCallsControllerRule implements SpringRule {

    @Override
    public String id() {
        return "ARCH101_CONTROLLER_CALLS_CONTROLLER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Set<String> controllerTypes = new HashSet<>();
        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(this::isController)
                    .forEach(clazz -> controllerTypes.add(clazz.getNameAsString()));
        }

        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!isController(clazz)) {
                    continue;
                }

                Map<String, MethodDeclaration> endpoints = new HashMap<>();
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (AstUtils.hasRequestMapping(method)) {
                        endpoints.put(method.getNameAsString(), method);
                    }
                }

                Set<String> injectedControllerFields = new HashSet<>();
                for (FieldDeclaration field : clazz.getFields()) {
                    field.getVariables().forEach(variable -> {
                        String type = variable.getType().asString();
                        if (controllerTypes.contains(simpleType(type)) && !simpleType(type).equals(clazz.getNameAsString())) {
                            injectedControllerFields.add(variable.getNameAsString());
                        }
                    });
                }

                for (MethodDeclaration caller : clazz.getMethods()) {
                    for (MethodCallExpr call : caller.findAll(MethodCallExpr.class)) {
                        boolean endpointSelfCall = endpoints.containsKey(call.getNameAsString())
                                && !call.getNameAsString().equals(caller.getNameAsString())
                                && call.getScope().map(scope -> scope.toString().equals("this")).orElse(true);
                        boolean otherControllerCall = call.getScope()
                                .map(Object::toString)
                                .map(injectedControllerFields::contains)
                                .orElse(false);

                        if (!endpointSelfCall && !otherControllerCall) {
                            continue;
                        }

                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Controller orchestration detected",
                                file.relativePath(),
                                AstUtils.line(call).orElse(null),
                                endpointSelfCall
                                        ? "Controller method " + caller.getNameAsString() + "() calls endpoint method " + call.getNameAsString() + "() directly."
                                        : "Controller method " + caller.getNameAsString() + "() calls another controller through " + call.getScope().map(Object::toString).orElse("controller") + ".",
                                "Controllers are HTTP adapters. Reusing an endpoint method as application logic couples transport concerns, response mapping and business orchestration, and makes reuse/testing harder.",
                                "Extract the shared use case into a service/application component and let both endpoints call that component."
                        ));
                    }
                }
            }
        }
        return findings;
    }

    private boolean isController(ClassOrInterfaceDeclaration clazz) {
        return AstUtils.hasAnyAnnotation(clazz, "Controller", "RestController");
    }

    private String simpleType(String type) {
        String raw = type == null ? "" : type;
        int generic = raw.indexOf('<');
        if (generic >= 0) {
            raw = raw.substring(0, generic);
        }
        int dot = raw.lastIndexOf('.');
        return dot >= 0 ? raw.substring(dot + 1) : raw;
    }
}
