package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Advises typed response DTOs instead of Map/Object response contracts.
 *
 * @author Simone Meneghetti
 */
public class ControllerMapResponseAlternativeRule implements SpringRule {

    @Override
    public String id() {
        return "SPR_ALT049_UNTYPED_CONTROLLER_RESPONSE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringWeb()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!AstUtils.hasRequestMapping(method)) {
                        continue;
                    }
                    String type = method.getTypeAsString().replace(" ", "");
                    boolean untyped = type.equals("Object")
                            || type.startsWith("Map<")
                            || type.contains("ResponseEntity<Map<")
                            || type.contains("ResponseEntity<Object>");
                    if (!untyped) {
                        continue;
                    }
                    findings.add(new Finding(
                            id(),
                            Severity.INFO,
                            "Untyped REST response contract",
                            file.relativePath(),
                            AstUtils.line(method).orElse(null),
                            "Endpoint " + method.getNameAsString() + "() returns " + method.getTypeAsString() + ".",
                            "Map/Object response bodies make the HTTP schema implicit, weaken OpenAPI generation and allow unrelated shapes to escape through the same endpoint contract.",
                            "Return a dedicated response DTO/record (or ProblemDetail for errors) so the API contract is explicit and testable."
                    ));
                }
            }
        }
        return findings;
    }
}
