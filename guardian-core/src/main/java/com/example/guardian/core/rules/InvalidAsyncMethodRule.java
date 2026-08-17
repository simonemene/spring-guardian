package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects @Async declarations that cannot be reliably proxied or expose an unsupported return contract.
 *
 * @author Simone Meneghetti
 */
public class InvalidAsyncMethodRule implements SpringRule {

    @Override
    public String id() {
        return "ASYNC101_INVALID_ASYNC_METHOD";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Async")) {
                    continue;
                }
                boolean nonInterceptable = method.isPrivate() || method.isStatic() || method.isFinal();
                String returnType = method.getType().asString().toLowerCase(Locale.ROOT);
                boolean supportedReturn = returnType.equals("void")
                        || returnType.contains("future")
                        || returnType.contains("completionstage");
                if (!nonInterceptable && supportedReturn) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        nonInterceptable ? Severity.CRITICAL : Severity.MAJOR,
                        "Invalid @Async method contract",
                        file.relativePath(),
                        AstUtils.line(method).orElse(null),
                        "@Async method " + method.getNameAsString() + "() is "
                                + (nonInterceptable ? "private/static/final" : "declared with return type " + method.getTypeAsString()) + ".",
                        nonInterceptable
                                ? "Proxy-based asynchronous interception may be bypassed, so the method can execute synchronously."
                                : "Spring asynchronous methods should expose a void/Future-style contract; other return values do not represent the asynchronous result reliably.",
                        "Use a public, non-final method invoked through the Spring proxy and return void, Future/CompletableFuture (or a compatible Future contract) when a result is required."
                ));
            }
        }
        return findings;
    }
}
