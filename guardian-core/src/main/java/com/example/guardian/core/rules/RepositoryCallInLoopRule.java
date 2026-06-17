package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deterministic Spring Guardian rule implementation for RepositoryCallInLoopRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class RepositoryCallInLoopRule implements SpringRule {

    @Override
    public String id() {
        return "SPR049_REPOSITORY_CALL_INSIDE_LOOP";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(ForStmt.class)
                    .forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), AstUtils.line(loop).orElse(null), findings));
            file.compilationUnit().findAll(ForEachStmt.class)
                    .forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), AstUtils.line(loop).orElse(null), findings));
            file.compilationUnit().findAll(WhileStmt.class)
                    .forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), AstUtils.line(loop).orElse(null), findings));
            file.compilationUnit().findAll(DoStmt.class)
                    .forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), AstUtils.line(loop).orElse(null), findings));
        }

        return findings;
    }

    private void inspectLoop(JavaSourceFile file, List<MethodCallExpr> calls, Integer loopLine, List<Finding> findings) {
        for (MethodCallExpr call : calls) {
            if (call.getScope().map(this::looksLikeRepository).orElse(false)) {
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Repository call inside a loop",
                        file.relativePath(),
                        AstUtils.line(call).orElse(loopLine),
                        "Detected repository call in loop: " + call,
                        "Calling repositories inside loops can create N+1 database access patterns and unpredictable batch/API latency.",
                        "Load data in bulk before the loop, use repository methods with IN clauses, batching, paging or dedicated queries."
                ));
            }
        }
    }

    private boolean looksLikeRepository(Expression scope) {
        String value = scope.toString().toLowerCase(Locale.ROOT);
        return value.endsWith("repository")
                || value.contains("repository.")
                || value.endsWith("repo")
                || value.contains("repo.");
    }
}
