package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deterministic Spring Guardian rule implementation for GetEndpointMutatesStateRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class GetEndpointMutatesStateRule implements SpringRule {

    @Override
    public String id() {
        return "SPR050_GET_ENDPOINT_MUTATES_STATE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!isGetEndpoint(method)) {
                        continue;
                    }

                    boolean suspiciousMethodName = startsWithMutationVerb(method.getNameAsString());
                    MethodCallExpr mutatingCall = method.findAll(MethodCallExpr.class).stream()
                            .filter(call -> startsWithMutationVerb(call.getNameAsString()))
                            .findFirst()
                            .orElse(null);

                    if (suspiciousMethodName || mutatingCall != null) {
                        String evidence = mutatingCall == null
                                ? "GET handler method name suggests mutation: " + method.getNameAsString()
                                : "GET handler calls mutating method: " + mutatingCall;
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "GET endpoint appears to mutate state",
                                file.relativePath(),
                                mutatingCall == null ? AstUtils.line(method).orElse(null) : AstUtils.line(mutatingCall).orElse(null),
                                evidence,
                                "GET should be safe and idempotent from the client/cache perspective. Mutating state through GET can cause accidental writes from crawlers, retries, links or caches.",
                                "Use POST, PUT, PATCH or DELETE for state-changing operations and keep GET endpoints read-only."
                        ));
                    }
                }
            }
        }

        return findings;
    }

    private boolean isGetEndpoint(MethodDeclaration method) {
        if (AstUtils.hasAnnotation(method, "GetMapping")) {
            return true;
        }
        return method.getAnnotations().stream()
                .filter(annotation -> annotation.getName().getIdentifier().equals("RequestMapping")
                        || annotation.getNameAsString().endsWith(".RequestMapping"))
                .anyMatch(annotation -> annotation.toString().contains("RequestMethod.GET") || annotation.toString().contains("method = GET"));
    }

    private boolean startsWithMutationVerb(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.startsWith("save")
                || lower.startsWith("create")
                || lower.startsWith("insert")
                || lower.startsWith("update")
                || lower.startsWith("delete")
                || lower.startsWith("remove")
                || lower.startsWith("disable")
                || lower.startsWith("enable")
                || lower.startsWith("activate")
                || lower.startsWith("deactivate");
    }
}
