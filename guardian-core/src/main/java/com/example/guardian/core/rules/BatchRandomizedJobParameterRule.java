package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects time/random expressions used directly as Spring Batch JobParameter values.
 *
 * @author Simone Meneghetti
 */
public class BatchRandomizedJobParameterRule implements SpringRule {

    private static final Set<String> PARAMETER_METHODS = Set.of(
            "addLong", "addString", "addDate", "addLocalDate", "addLocalDateTime", "addDouble"
    );
    private static final Set<String> UNIQUE_EXPRESSIONS = Set.of(
            "System.currentTimeMillis()", "System.nanoTime()", "UUID.randomUUID()", "Instant.now()"
    );

    @Override
    public String id() {
        return "BAT043_RANDOMIZED_JOB_PARAMETER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringBatch()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!PARAMETER_METHODS.contains(call.getNameAsString()) || call.getArguments().size() < 2) {
                    continue;
                }
                String valueExpression = call.getArgument(1).toString().replace(" ", "");
                boolean uniqueValue = UNIQUE_EXPRESSIONS.stream().anyMatch(valueExpression::contains);
                if (!uniqueValue) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Random/time-based JobParameter",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "JobParameter " + call.getNameAsString() + " uses " + valueExpression + ".",
                        "A unique timestamp/UUID used as an identifying parameter creates a new JobInstance on every launch and can hide restartability problems.",
                        "Use stable identifying business parameters. If a non-identifying execution timestamp is needed, mark it non-identifying where supported instead of forcing a new JobInstance."
                ));
            }
        }
        return findings;
    }
}
