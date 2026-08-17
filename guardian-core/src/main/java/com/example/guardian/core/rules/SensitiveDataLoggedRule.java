package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Detects likely secret/credential values passed to logger calls.
 *
 * @author Simone Meneghetti
 */
public class SensitiveDataLoggedRule implements SpringRule {

    private static final Set<String> LOG_METHODS = Set.of("trace", "debug", "info", "warn", "error");
    private static final Set<String> SENSITIVE_TERMS = Set.of(
            "password", "passwd", "secret", "token", "authorization", "apikey", "api_key", "credential"
    );

    @Override
    public String id() {
        return "OBS101_SENSITIVE_DATA_LOGGED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!LOG_METHODS.contains(call.getNameAsString()) || !looksLikeLogger(call)) {
                    continue;
                }
                if (!containsSensitiveDynamicArgument(call)) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.CRITICAL,
                        "Potential secret written to application logs",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "Logger call contains a credential-like dynamic value.",
                        "Logs are routinely copied to consoles, files, collectors and support systems. Logging passwords, tokens or authorization material can turn an application log into a credential leak.",
                        "Never log credential values. Redact sensitive fields centrally and log only non-secret identifiers or metadata needed for diagnostics."
                ));
            }
        }
        return findings;
    }

    private boolean looksLikeLogger(MethodCallExpr call) {
        return call.getScope().map(Object::toString)
                .map(scope -> scope.toLowerCase(Locale.ROOT))
                .map(scope -> scope.equals("log") || scope.equals("logger") || scope.endsWith("logger"))
                .orElse(false);
    }

    private boolean containsSensitiveDynamicArgument(MethodCallExpr call) {
        for (Expression argument : call.getArguments()) {
            if (argument.isStringLiteralExpr()) {
                continue;
            }
            String normalized = argument.toString().replace("-", "").toLowerCase(Locale.ROOT);
            for (String term : SENSITIVE_TERMS) {
                if (normalized.contains(term)) {
                    return true;
                }
            }
        }
        return false;
    }
}
