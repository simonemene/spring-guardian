package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects direct bean lookup through ApplicationContext or BeanFactory.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ApplicationContextGetBeanAdvisorRule implements SpringRule {

    @Override
    public String id() {
        return "SPR086_APPLICATION_CONTEXT_GET_BEAN";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!"getBean".equals(call.getNameAsString()) || call.getScope().isEmpty()) {
                    continue;
                }
                String scope = call.getScope().get().toString().toLowerCase();
                if (!scope.contains("context") && !scope.contains("beanfactory")) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Bean resolved programmatically",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "Detected bean lookup: " + call,
                        "Programmatic bean lookup hides dependencies and bypasses the readability benefits of constructor injection.",
                        "Inject the required collaborator through constructor injection, or isolate dynamic lookup behind a small factory bean."
                ));
            }
        }
        return findings;
    }
}
