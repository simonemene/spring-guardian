package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects explicitly weak BCrypt cost values.
 *
 * @author Simone Meneghetti
 */
public class BCryptLowStrengthRule implements SpringRule {

    @Override
    public String id() {
        return "SEC104_BCRYPT_LOW_STRENGTH";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringSecurity()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (ObjectCreationExpr creation : file.compilationUnit().findAll(ObjectCreationExpr.class)) {
                if (!creation.getTypeAsString().equals("BCryptPasswordEncoder") || creation.getArguments().isEmpty()) {
                    continue;
                }
                if (!creation.getArgument(0).isIntegerLiteralExpr()) {
                    continue;
                }
                IntegerLiteralExpr literal = creation.getArgument(0).asIntegerLiteralExpr();
                int strength;
                try {
                    strength = Integer.decode(literal.getValue().replace("_", ""));
                } catch (RuntimeException exception) {
                    continue;
                }
                if (strength > 7) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Low BCrypt work factor",
                        file.relativePath(),
                        AstUtils.line(creation).orElse(null),
                        "BCryptPasswordEncoder is configured with strength " + strength + ".",
                        "A very low BCrypt cost reduces the computational work required for offline password guessing if hashes are leaked.",
                        "Benchmark an adaptive cost appropriate for your hardware and authentication latency budget; avoid explicitly weak work factors."
                ));
            }
        }
        return findings;
    }
}
