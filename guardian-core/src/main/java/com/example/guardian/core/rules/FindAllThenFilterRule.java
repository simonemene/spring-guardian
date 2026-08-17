package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects repository findAll() results filtered in memory in the same invocation chain.
 *
 * @author Simone Meneghetti
 */
public class FindAllThenFilterRule implements SpringRule {

    @Override
    public String id() {
        return "JPA101_FIND_ALL_FILTER_IN_MEMORY";
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
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!call.getNameAsString().equals("filter") || call.getScope().isEmpty()) {
                    continue;
                }
                String scope = call.getScope().get().toString().replace(" ", "");
                if (!scope.contains(".findAll()") || !scope.contains(".stream()")) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MINOR,
                        "Repository findAll() filtered in memory",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "Detected chained findAll().stream().filter(...).",
                        "Loading an entire table/collection before filtering transfers work and memory pressure from the database to the application and can degrade sharply as data grows.",
                        "Express the predicate in a repository query, Specification, Querydsl predicate or database projection and fetch only the required rows."
                ));
            }
        }
        return findings;
    }
}
