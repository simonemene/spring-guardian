package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for CorsAllowAllWithCredentialsRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class CorsAllowAllWithCredentialsRule implements SpringRule {

    @Override
    public String id() {
        return "SPR046_CORS_ALLOW_ALL_WITH_CREDENTIALS";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            boolean allowsCredentials = false;
            boolean allowsWildcardOrigin = false;
            Integer evidenceLine = null;
            String evidence = null;

            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                String text = call.toString().replace(" ", "");

                if (text.contains("allowCredentials(true)") || text.contains("setAllowCredentials(true)")) {
                    allowsCredentials = true;
                    if (evidenceLine == null) {
                        evidenceLine = AstUtils.line(call).orElse(null);
                        evidence = call.toString();
                    }
                }

                if (text.contains("allowedOrigins(\"*\")")
                        || text.contains("allowedOriginPatterns(\"*\")")
                        || text.contains("addAllowedOrigin(\"*\")")
                        || text.contains("addAllowedOriginPattern(\"*\")")
                        || text.contains("setAllowedOrigins(List.of(\"*\"))")
                        || text.contains("setAllowedOriginPatterns(List.of(\"*\"))")) {
                    allowsWildcardOrigin = true;
                    evidenceLine = AstUtils.line(call).orElse(evidenceLine);
                    evidence = call.toString();
                }
            }

            if (allowsCredentials && allowsWildcardOrigin) {
                findings.add(new Finding(
                        id(),
                        Severity.CRITICAL,
                        "CORS allows wildcard origins with credentials",
                        file.relativePath(),
                        evidenceLine,
                        "Detected wildcard CORS origin combined with credentials. Example: " + evidence,
                        "Credentials with permissive origins can expose authenticated APIs to untrusted web origins or create browser/security misconfiguration risk.",
                        "Use an explicit allow-list of trusted origins per environment and avoid wildcard origins when credentials are enabled."
                ));
            }
        }

        return findings;
    }
}
