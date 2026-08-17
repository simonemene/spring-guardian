package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Advises explicit transaction-phase semantics for events published inside transactions.
 *
 * @author Simone Meneghetti
 */
public class TransactionalEventPublishAlternativeRule implements SpringRule {

    @Override
    public String id() {
        return "SPR_ALT050_EVENT_PUBLISHED_INSIDE_TRANSACTION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                Set<String> publisherFields = new HashSet<>();
                for (FieldDeclaration field : clazz.getFields()) {
                    field.getVariables().forEach(variable -> {
                        String type = variable.getType().asString();
                        if (type.endsWith("ApplicationEventPublisher")) {
                            publisherFields.add(variable.getNameAsString());
                        }
                    });
                }
                if (publisherFields.isEmpty()) {
                    continue;
                }
                boolean classTransactional = AstUtils.hasAnnotation(clazz, "Transactional");
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!classTransactional && !AstUtils.hasAnnotation(method, "Transactional")) {
                        continue;
                    }
                    for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                        if (!call.getNameAsString().equals("publishEvent") || call.getScope().isEmpty()
                                || !publisherFields.contains(call.getScope().get().toString())) {
                            continue;
                        }
                        findings.add(new Finding(
                                id(),
                                Severity.INFO,
                                "Application event published inside transaction",
                                file.relativePath(),
                                AstUtils.line(call).orElse(null),
                                "Transactional method " + method.getNameAsString() + "() calls ApplicationEventPublisher.publishEvent(...).",
                                "Synchronous event listeners can run before the transaction commits; external side effects may therefore occur even if the database transaction later rolls back.",
                                "If listeners represent post-commit side effects, use @TransactionalEventListener with the appropriate phase or an outbox for durable cross-process events."
                        ));
                    }
                }
            }
        }
        return findings;
    }
}
