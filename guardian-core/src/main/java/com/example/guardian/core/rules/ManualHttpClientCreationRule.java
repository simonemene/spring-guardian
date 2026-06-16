package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.ArrayList;
import java.util.List;

public class ManualHttpClientCreationRule implements SpringRule {

    @Override
    public String id() {
        return "SPR026_HTTP_CLIENT_CREATED_MANUALLY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ObjectCreationExpr expr : file.compilationUnit().findAll(ObjectCreationExpr.class)) {
                String type = expr.getTypeAsString();
                if (type.equals("RestTemplate") || type.equals("WebClient") || type.equals("RestClient")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "HTTP client created manually",
                            file.relativePath(),
                            AstUtils.line(expr).orElse(null),
                            "Detected creation: " + expr,
                            "HTTP clients should usually be centrally configured with timeouts, interceptors, codecs, base URLs and observability.",
                            "Inject RestClient.Builder/WebClient.Builder or define a configured HTTP client bean."
                    ));
                }
            }
        }

        return findings;
    }
}
