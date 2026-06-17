package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for CsrfDisabledRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class CsrfDisabledRule implements SpringRule {

    @Override
    public String id() {
        return "SPR040_CSRF_DISABLED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                String text = call.toString();
                if (text.contains("csrf") && text.contains("disable()")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "CSRF disabled",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected CSRF disable call: " + text,
                            "Disabling CSRF can be correct for stateless APIs, but it should be intentional and documented.",
                            "Verify the app is stateless/token-based or restrict CSRF disabling to specific endpoints with clear rationale."
                    ));
                }
            }
        }

        return findings;
    }
}
