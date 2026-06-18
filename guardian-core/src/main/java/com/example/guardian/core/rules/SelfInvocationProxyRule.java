package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.*;

/**
 * Deterministic Spring Guardian rule implementation for SelfInvocationProxyRule.
 *
 * @author Simone Meneghetti
 */
public class SelfInvocationProxyRule implements SpringRule {

    private static final String[] PROXY_ANNOTATIONS = {
            "Transactional", "Async", "Cacheable", "CacheEvict", "CachePut", "Retryable"
    };

    @Override
    public String id() {
        return "SPR007_SELF_INVOCATION_PROXY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                Map<String, MethodDeclaration> annotatedMethods = new HashMap<>();
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (AstUtils.hasAnyAnnotation(method, PROXY_ANNOTATIONS)) {
                        annotatedMethods.put(method.getNameAsString(), method);
                    }
                }

                if (annotatedMethods.isEmpty()) {
                    continue;
                }

                for (MethodDeclaration caller : clazz.getMethods()) {
                    for (MethodCallExpr call : caller.findAll(MethodCallExpr.class)) {
                        String calledName = call.getNameAsString();
                        if (!annotatedMethods.containsKey(calledName)) {
                            continue;
                        }

                        boolean explicitThis = call.getScope().map(scope -> scope.toString().equals("this")).orElse(false);
                        boolean implicitThis = call.getScope().isEmpty();

                        if (explicitThis || implicitThis) {
                            findings.add(new Finding(
                                    id(),
                                    Severity.CRITICAL,
                                    "Spring proxy self-invocation detected",
                                    file.relativePath(),
                                    AstUtils.line(call).orElse(null),
                                    "Method " + caller.getNameAsString() + " calls " + calledName + "() inside the same class.",
                                    "Spring proxy-based annotations like @Transactional, @Async, @Cacheable and @Retryable may be bypassed when invoked from the same class.",
                                    "Move the annotated method to another Spring bean, inject the bean, or redesign the transactional/proxy boundary."
                            ));
                        }
                    }
                }
            }
        }

        return findings;
    }
}
