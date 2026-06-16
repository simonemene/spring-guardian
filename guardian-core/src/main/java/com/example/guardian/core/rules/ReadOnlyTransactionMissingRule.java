package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReadOnlyTransactionMissingRule implements SpringRule {

    @Override
    public String id() {
        return "SPR018_READONLY_TRANSACTION_MISSING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Service")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    String name = method.getNameAsString().toLowerCase(Locale.ROOT);
                    boolean readLike = name.startsWith("get") || name.startsWith("find") || name.startsWith("search")
                            || name.startsWith("list") || name.startsWith("read") || name.startsWith("load");

                    if (!readLike || AstUtils.hasAnnotation(method, "Transactional")) {
                        continue;
                    }

                    boolean callsRepository = method.findAll(MethodCallExpr.class).stream()
                            .anyMatch(call -> call.getScope()
                                    .map(scope -> scope.toString().toLowerCase(Locale.ROOT).contains("repository"))
                                    .orElse(false));

                    if (callsRepository) {
                        findings.add(new Finding(
                                id(),
                                Severity.INFO,
                                "Read-only service method without explicit read-only transaction",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " looks read-only and calls a repository without @Transactional(readOnly = true).",
                                "Explicit read-only transactions can document intent and help optimize persistence behavior in read paths.",
                                "Consider adding @Transactional(readOnly = true) if this method only reads data."
                        ));
                    }
                }
            }
        }

        return findings;
    }
}
