package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects MDC values put into a thread without cleanup in the same method.
 *
 * @author Simone Meneghetti
 */
public class MdcWithoutCleanupRule implements SpringRule {

    @Override
    public String id() {
        return "OBS102_MDC_WITHOUT_CLEANUP";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                List<MethodCallExpr> calls = method.findAll(MethodCallExpr.class);
                MethodCallExpr put = calls.stream().filter(call -> isMdcCall(call, "put")).findFirst().orElse(null);
                if (put == null) {
                    continue;
                }
                boolean cleanup = calls.stream().anyMatch(call -> isMdcCall(call, "remove") || isMdcCall(call, "clear"));
                if (cleanup) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MINOR,
                        "MDC value is not cleaned up",
                        file.relativePath(),
                        AstUtils.line(put).orElse(null),
                        "Method " + method.getNameAsString() + "() calls MDC.put(...) without MDC.remove/clear in the same method.",
                        "MDC is thread-local. Pooled request/executor threads can retain stale context and attach one request/user correlation value to unrelated work.",
                        "Clean MDC in a finally block, use MDC.putCloseable where appropriate, or rely on framework observation/context propagation facilities."
                ));
            }
        }
        return findings;
    }

    private boolean isMdcCall(MethodCallExpr call, String method) {
        return call.getNameAsString().equals(method)
                && call.getScope().map(Object::toString).map("MDC"::equals).orElse(false);
    }
}
