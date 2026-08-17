package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Advises review when @Async and @Transactional are combined on the same method.
 *
 * @author Simone Meneghetti
 */
public class AsyncTransactionalCombinedAlternativeRule implements SpringRule {

    @Override
    public String id() {
        return "SPR_ALT041_ASYNC_TRANSACTIONAL_SAME_METHOD";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                if (!AstUtils.hasAnnotation(method, "Async") || !AstUtils.hasAnnotation(method, "Transactional")) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.INFO,
                        "@Async and @Transactional share one method boundary",
                        file.relativePath(),
                        AstUtils.line(method).orElse(null),
                        "Method " + method.getNameAsString() + "() is annotated with both @Async and @Transactional.",
                        "Both features are proxy/interceptor based and the transaction belongs to the asynchronous execution thread, not the caller. The combined lifecycle is easy to misunderstand and complicates error/retry semantics.",
                        "Prefer a clear asynchronous entry point that delegates to a separate transactional service method, so thread and transaction boundaries are explicit."
                ));
            }
        }
        return findings;
    }
}
