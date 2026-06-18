package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for ManualConnectionManagementRule.
 *
 * @author Simone Meneghetti
 */
public class ManualConnectionManagementRule implements SpringRule {

    @Override
    public String id() {
        return "SPR009_MANUAL_CONNECTION_MANAGEMENT";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(MethodCallExpr.class).forEach(call -> {
                String text = call.toString();
                if (text.contains("DriverManager.getConnection")
                        || text.endsWith(".commit()")
                        || text.endsWith(".rollback()")
                        || text.endsWith(".setAutoCommit(false)")) {
                    findings.add(new Finding(
                            id(),
                            Severity.CRITICAL,
                            "Manual JDBC connection/transaction management detected",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected call: " + text,
                            "Manual connection handling can bypass Spring datasource, pooling and transaction management.",
                            "Use Spring-managed DataSource, JdbcTemplate/JPA repository and @Transactional boundaries."
                    ));
                }
            });

            file.compilationUnit().findAll(ObjectCreationExpr.class).forEach(expr -> {
                String type = expr.getTypeAsString();
                if (type.equals("Connection") || type.endsWith("Connection")) {
                    findings.add(new Finding(
                            id(),
                            Severity.CRITICAL,
                            "Manual JDBC connection object creation detected",
                            file.relativePath(),
                            AstUtils.line(expr).orElse(null),
                            "Detected creation: " + expr,
                            "Manual connection creation is dangerous in Spring applications.",
                            "Use Spring-managed DataSource or repository abstraction."
                    ));
                }
            });
        }

        return findings;
    }
}
