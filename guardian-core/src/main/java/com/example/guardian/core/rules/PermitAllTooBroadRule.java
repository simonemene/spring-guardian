package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for PermitAllTooBroadRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class PermitAllTooBroadRule implements SpringRule {

    @Override
    public String id() {
        return "SPR041_PERMIT_ALL_TOO_BROAD";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                String text = call.toString();

                boolean hasPermitAll = text.contains("permitAll()");
                boolean broadMatcher = text.contains("\"/**\"")
                        || text.contains("\"/*\"")
                        || text.contains("requestMatchers(\"/\")")
                        || text.contains("antMatchers(\"/**\")")
                        || text.contains("mvcMatchers(\"/**\")");

                if (hasPermitAll && broadMatcher) {
                    findings.add(new Finding(
                            id(),
                            Severity.CRITICAL,
                            "Broad permitAll security matcher",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected broad permitAll expression: " + text,
                            "A broad permitAll matcher can accidentally make the application public.",
                            "Restrict permitAll to explicit public endpoints and protect application APIs by default."
                    ));
                }
            }
        }

        return findings;
    }
}
