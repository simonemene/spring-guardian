package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects literal JWT/HMAC signing material passed directly to common signing APIs.
 *
 * @author Simone Meneghetti
 */
public class HardcodedJwtSecretRule implements SpringRule {

    private static final Set<String> KEY_METHODS = Set.of("setSigningKey", "hmacShaKeyFor");

    @Override
    public String id() {
        return "SEC103_HARDCODED_SIGNING_SECRET";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!KEY_METHODS.contains(call.getNameAsString()) || call.getArguments().isEmpty() || !containsLiteralSecret(call.getArgument(0))) {
                    continue;
                }
                findings.add(finding(file, AstUtils.line(call).orElse(null), "Signing-key API receives literal key material."));
            }
            for (ObjectCreationExpr creation : file.compilationUnit().findAll(ObjectCreationExpr.class)) {
                if (!creation.getTypeAsString().equals("SecretKeySpec") || creation.getArguments().isEmpty() || !containsLiteralSecret(creation.getArgument(0))) {
                    continue;
                }
                findings.add(finding(file, AstUtils.line(creation).orElse(null), "SecretKeySpec is constructed from literal key material."));
            }
        }
        return findings;
    }

    private boolean containsLiteralSecret(com.github.javaparser.ast.expr.Expression expression) {
        return expression.findAll(StringLiteralExpr.class).stream()
                .anyMatch(literal -> literal.getValue().length() >= 8);
    }

    private Finding finding(JavaSourceFile file, Integer line, String evidence) {
        return new Finding(
                id(),
                Severity.CRITICAL,
                "Hardcoded signing secret",
                file.relativePath(),
                line,
                evidence,
                "JWT/HMAC signing material committed in source code can be copied from repository history and allows forged tokens/messages if leaked.",
                "Load signing keys from environment/secret storage or a key-management service and rotate any key that has already been committed."
        );
    }
}
