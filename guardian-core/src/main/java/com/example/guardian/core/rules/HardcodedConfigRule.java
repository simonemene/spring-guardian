package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Deterministic Spring Guardian rule implementation for HardcodedConfigRule.
 *
 * @author Simone Meneghetti
 */
public class HardcodedConfigRule implements SpringRule {

    private static final Pattern SUSPICIOUS_STRING = Pattern.compile(
            "(https?://|jdbc:|localhost|/opt/|/var/|C:\\\\|bucket|queue|topic|password|secret|apikey|api-key)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String id() {
        return "SPR001_HARDCODED_CONFIG";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(StringLiteralExpr.class).forEach(expr -> {
                String value = expr.asString();
                if (SUSPICIOUS_STRING.matcher(value).find()) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Potential hardcoded configuration value",
                            file.relativePath(),
                            AstUtils.line(expr).orElse(null),
                            "String literal: \"" + value + "\"",
                            "Environment-specific values in code make deployments fragile and can expose sensitive configuration.",
                            "Move this value to application.yml/properties and bind it with @ConfigurationProperties."
                    ));
                }
            });

            file.compilationUnit().findAll(IntegerLiteralExpr.class).forEach(expr -> inspectNumber(file, expr.toString(), AstUtils.line(expr).orElse(null), findings));
            file.compilationUnit().findAll(LongLiteralExpr.class).forEach(expr -> inspectNumber(file, expr.toString(), AstUtils.line(expr).orElse(null), findings));
        }

        return findings;
    }

    private void inspectNumber(JavaSourceFile file, String value, Integer line, List<Finding> findings) {
        try {
            long number = Long.parseLong(value.replace("_", "").replace("L", "").replace("l", ""));
            if (number == 1000 || number == 5000 || number == 10000 || number == 30000 || number == 60000) {
                findings.add(new Finding(
                        id(),
                        Severity.MINOR,
                        "Potential hardcoded timeout/duration",
                        file.relativePath(),
                        line,
                        "Numeric literal: " + value,
                        "Common timeout values should usually be configurable.",
                        "Move the value to configuration and bind it as Duration with @ConfigurationProperties."
                ));
            }
        } catch (NumberFormatException ignored) {
        }
    }
}
