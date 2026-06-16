package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class InvalidTransactionalUsageRule implements SpringRule {

    @Override
    public String id() {
        return "SPR008_INVALID_TRANSACTIONAL_USAGE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Transactional")) {
                    continue;
                }

                if (method.isPrivate() || method.isStatic() || method.isFinal()) {
                    findings.add(new Finding(
                            id(),
                            Severity.CRITICAL,
                            "Potentially ineffective @Transactional usage",
                            file.relativePath(),
                            AstUtils.line(method).orElse(null),
                            "@Transactional method " + method.getNameAsString() + " is private/static/final.",
                            "With proxy-based Spring AOP, non-overridable or non-interceptable methods may not receive transactional advice.",
                            "Put @Transactional on a public service method that is invoked through the Spring bean proxy."
                    ));
                }
            }
        }

        return findings;
    }
}
