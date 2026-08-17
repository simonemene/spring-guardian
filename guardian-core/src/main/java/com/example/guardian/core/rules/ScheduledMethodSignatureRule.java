package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects invalid @Scheduled method parameters.
 *
 * @author Simone Meneghetti
 */
public class ScheduledMethodSignatureRule implements SpringRule {

    @Override
    public String id() {
        return "ASYNC102_SCHEDULED_METHOD_HAS_PARAMETERS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Scheduled") || method.getParameters().isEmpty()) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.CRITICAL,
                        "@Scheduled method has parameters",
                        file.relativePath(),
                        AstUtils.line(method).orElse(null),
                        "Scheduled method " + method.getNameAsString() + "() declares " + method.getParameters().size() + " parameter(s).",
                        "Spring scheduled methods are invoked by the scheduler rather than by application callers and must not require invocation arguments. Invalid signatures can fail scheduler registration at startup.",
                        "Make the @Scheduled method parameterless and inject required collaborators/state through the bean or configuration."
                ));
            }
        }
        return findings;
    }
}
