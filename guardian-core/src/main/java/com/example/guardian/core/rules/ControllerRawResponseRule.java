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
 * Deterministic Spring Guardian rule implementation for ControllerRawResponseRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ControllerRawResponseRule implements SpringRule {

    @Override
    public String id() {
        return "SPR051_CONTROLLER_RAW_RESPONSE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!hasRequestMapping(method)) {
                        continue;
                    }

                    String returnType = method.getTypeAsString().replace(" ", "");
                    if (returnType.equals("Object")
                            || returnType.equals("Map")
                            || returnType.startsWith("Map<")
                            || returnType.equals("ResponseEntity<Object>")
                            || returnType.equals("ResponseEntity<?>")) {
                        findings.add(new Finding(
                                id(),
                                Severity.MINOR,
                                "Controller returns a raw or weak response type",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " returns " + method.getTypeAsString() + ".",
                                "Raw response contracts are harder for clients to consume, document, validate and evolve safely.",
                                "Return a specific response DTO or a typed ResponseEntity<ResponseDto>."
                        ));
                    }
                }
            }
        }

        return findings;
    }

    private boolean hasRequestMapping(MethodDeclaration method) {
        return AstUtils.hasAnyAnnotation(method,
                "RequestMapping",
                "GetMapping",
                "PostMapping",
                "PutMapping",
                "PatchMapping",
                "DeleteMapping");
    }
}
