package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects REST controllers without a class-level base mapping.
 *
 * @author Simone Meneghetti
 */
public class RestControllerWithoutBaseMappingRule implements SpringRule {

    @Override
    public String id() {
        return "SPR063_REST_CONTROLLER_WITHOUT_BASE_MAPPING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (AstUtils.hasAnnotation(clazz, "RestController") && !AstUtils.hasAnnotation(clazz, "RequestMapping")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "REST controller has no class-level base mapping",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Controller " + clazz.getNameAsString() + " has @RestController but no class-level @RequestMapping.",
                            "Without a base mapping, endpoint organization and API versioning are less visible.",
                            "Add a class-level @RequestMapping such as /api/v1/resource-name and keep method mappings focused on operations."
                    ));
                }
            }
        }

        return findings;
    }
}
