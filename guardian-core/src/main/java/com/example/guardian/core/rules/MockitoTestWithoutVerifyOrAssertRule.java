package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for MockitoTestWithoutVerifyOrAssertRule.
 *
 * @author Simone Meneghetti
 */
public class MockitoTestWithoutVerifyOrAssertRule implements SpringRule {

    @Override
    public String id() {
        return "SPR045_MOCKITO_TEST_WITHOUT_VERIFY_OR_ASSERT";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (!AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            boolean usesMockito = file.content().contains("Mockito")
                    || file.content().contains("@Mock")
                    || file.content().contains("@MockBean")
                    || file.content().contains("when(")
                    || file.content().contains("given(");

            if (!usesMockito) {
                continue;
            }

            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Test")) {
                    continue;
                }

                String body = method.getBody().map(Object::toString).orElse("");
                boolean hasVerifyOrAssert = body.contains("verify(")
                        || body.contains("then(")
                        || body.contains("assert")
                        || body.contains("andExpect(");

                if (!hasVerifyOrAssert) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Mockito test without verification or assertion",
                            file.relativePath(),
                            AstUtils.line(method).orElse(null),
                            "Test method " + method.getNameAsString() + " appears to use Mockito but has no verify/assert.",
                            "Mock-based tests without assertions or verifications rarely prove behavior.",
                            "Add assertions on returned values/state or verify expected mock interactions."
                    ));
                }
            }
        }

        return findings;
    }
}
