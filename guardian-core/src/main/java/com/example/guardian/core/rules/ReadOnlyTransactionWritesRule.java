package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Detects write operations performed in a transaction declared read-only.
 *
 * @author Simone Meneghetti
 */
public class ReadOnlyTransactionWritesRule implements SpringRule {

    private static final Set<String> WRITE_METHODS = Set.of(
            "save", "saveall", "saveandflush", "delete", "deleteall", "deletebyid", "flush",
            "persist", "merge", "remove", "executeupdate", "batchupdate"
    );

    @Override
    public String id() {
        return "TX102_READ_ONLY_TRANSACTION_WRITES";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                boolean classReadOnly = hasReadOnlyTransactional(clazz.getAnnotations());
                for (MethodDeclaration method : clazz.getMethods()) {
                    boolean methodTransactional = AstUtils.hasAnnotation(method, "Transactional");
                    boolean methodReadOnly = hasReadOnlyTransactional(method.getAnnotations());
                    boolean effectiveReadOnly = methodReadOnly || (!methodTransactional && classReadOnly);
                    if (!effectiveReadOnly) {
                        continue;
                    }
                    for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                        if (!WRITE_METHODS.contains(call.getNameAsString().toLowerCase(Locale.ROOT))) {
                            continue;
                        }
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Write operation inside read-only transaction",
                                file.relativePath(),
                                AstUtils.line(call).orElse(null),
                                "Method " + method.getNameAsString() + "() is read-only but calls " + call.getNameAsString() + "().",
                                "The declared transaction intent conflicts with a write operation. Depending on the database/provider this can fail, be silently allowed, or make transaction semantics misleading.",
                                "Remove the write from the read-only use case or use a separate write transaction with an explicit service boundary."
                        ));
                    }
                }
            }
        }
        return findings;
    }

    private boolean hasReadOnlyTransactional(Iterable<AnnotationExpr> annotations) {
        for (AnnotationExpr annotation : annotations) {
            if ((annotation.getName().getIdentifier().equals("Transactional") || annotation.getNameAsString().endsWith(".Transactional"))
                    && annotation.toString().replace(" ", "").toLowerCase(Locale.ROOT).contains("readonly=true")) {
                return true;
            }
        }
        return false;
    }
}
