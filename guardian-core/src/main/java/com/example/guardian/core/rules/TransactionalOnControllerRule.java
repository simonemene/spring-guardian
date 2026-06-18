package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for TransactionalOnControllerRule.
 *
 * @author Simone Meneghetti
 */
public class TransactionalOnControllerRule implements SpringRule {

    @Override
    public String id() {
        return "SPR017_TRANSACTIONAL_ON_CONTROLLER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                if (AstUtils.hasAnnotation(clazz, "Transactional")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "@Transactional on controller class",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Controller " + clazz.getNameAsString() + " is annotated with @Transactional.",
                            "The controller layer should handle HTTP mapping; transactional boundaries usually belong in the service layer.",
                            "Move @Transactional to a public service method that performs the business operation."
                    ));
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (AstUtils.hasAnnotation(method, "Transactional")) {
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "@Transactional on controller method",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Controller method " + method.getNameAsString() + " is annotated with @Transactional.",
                                "Transactions in controllers blur HTTP and business responsibilities and make reuse/testing harder.",
                                "Move the transaction boundary to a service method."
                        ));
                    }
                }
            }
        }

        return findings;
    }
}
