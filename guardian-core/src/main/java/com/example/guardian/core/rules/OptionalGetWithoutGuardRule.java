package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for OptionalGetWithoutGuardRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class OptionalGetWithoutGuardRule implements SpringRule {

    @Override
    public String id() {
        return "SPR020_OPTIONAL_GET_WITHOUT_GUARD";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!call.getNameAsString().equals("get") || call.getScope().isEmpty()) {
                    continue;
                }

                String scope = call.getScope().get().toString();
                if (scope.toLowerCase().contains("optional") || scope.toLowerCase().contains("findby")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Optional.get() used directly",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected direct get(): " + call,
                            "Direct Optional.get() can throw NoSuchElementException and usually hides missing-domain handling.",
                            "Use orElseThrow with a domain-specific exception, or handle the empty case explicitly."
                    ));
                }
            }
        }

        return findings;
    }
}
