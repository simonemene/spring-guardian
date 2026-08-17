package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects explicit flush/saveAndFlush calls inside loops.
 *
 * @author Simone Meneghetti
 */
public class FlushInsideLoopRule implements SpringRule {

    private static final Set<String> FLUSH_METHODS = Set.of("flush", "saveAndFlush");

    @Override
    public String id() {
        return "JPA102_FLUSH_INSIDE_LOOP";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesJpa()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            file.compilationUnit().findAll(ForStmt.class).forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), findings));
            file.compilationUnit().findAll(ForEachStmt.class).forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), findings));
            file.compilationUnit().findAll(WhileStmt.class).forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), findings));
            file.compilationUnit().findAll(DoStmt.class).forEach(loop -> inspectLoop(file, loop.findAll(MethodCallExpr.class), findings));
        }
        return findings;
    }

    private void inspectLoop(JavaSourceFile file, List<MethodCallExpr> calls, List<Finding> findings) {
        for (MethodCallExpr call : calls) {
            if (!FLUSH_METHODS.contains(call.getNameAsString()) || !looksLikePersistenceFlush(call)) {
                continue;
            }
            findings.add(new Finding(
                    id(),
                    Severity.MAJOR,
                    "Persistence flush inside loop",
                    file.relativePath(),
                    AstUtils.line(call).orElse(null),
                    "Detected " + call.getNameAsString() + "() inside an iteration.",
                    "Forcing a database flush for every element defeats batching and can turn one unit of work into many network round trips and synchronization points.",
                    "Batch writes and flush at controlled boundaries. For large workloads evaluate JDBC batching or Spring Batch chunk-oriented processing."
            ));
        }
    }
    private boolean looksLikePersistenceFlush(MethodCallExpr call) {
        if (call.getNameAsString().equals("saveAndFlush")) {
            return true;
        }
        String scope = call.getScope().map(Object::toString).orElse("").toLowerCase();
        return scope.contains("repository")
                || scope.contains("entitymanager")
                || scope.equals("em")
                || scope.endsWith("em");
    }

}
