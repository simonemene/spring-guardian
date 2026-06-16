package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class TestWithoutAssertionsRule implements SpringRule {

    @Override
    public String id() {
        return "SPR043_TEST_WITHOUT_ASSERTIONS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (!AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Test")) {
                    continue;
                }

                String body = method.getBody().map(Object::toString).orElse("");
                boolean hasAssertion = body.contains("assert")
                        || body.contains("verify(")
                        || body.contains("then(")
                        || body.contains("expect(")
                        || body.contains("andExpect(")
                        || body.contains("StepVerifier");

                if (!hasAssertion) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Test method without assertions/verifications",
                            file.relativePath(),
                            AstUtils.line(method).orElse(null),
                            "Test method " + method.getNameAsString() + " has no obvious assertion or verification.",
                            "Tests without assertions often only check that code does not crash and may miss regressions.",
                            "Add explicit assertions, Mockito verifications, MockMvc andExpect, or reactive StepVerifier expectations."
                    ));
                }
            }
        }

        return findings;
    }
}
