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
 * Detects REST endpoints without OpenAPI operation metadata.
 *
 * @author p15518 - Simone Meneghetti
 */
public class EndpointMissingOpenApiOperationRule implements SpringRule {

    @Override
    public String id() {
        return "SPR060_ENDPOINT_WITHOUT_OPENAPI_OPERATION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
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
                    if (AstUtils.hasRequestMapping(method) && !AstUtils.hasAnnotation(method, "Operation")) {
                        findings.add(new Finding(
                                id(),
                                Severity.MINOR,
                                "REST endpoint has no OpenAPI operation metadata",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Endpoint method " + method.getNameAsString() + " has a request mapping but no @Operation.",
                                "Missing operation metadata makes generated API documentation less useful for consumers and reviewers.",
                                "Add @Operation with a clear summary and description, then document relevant responses with @ApiResponse."
                        ));
                    }
                }
            }
        }

        return findings;
    }
}
