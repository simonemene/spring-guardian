package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects late-bound Spring Batch job/step parameters used without a batch scope.
 *
 * @author Simone Meneghetti
 */
public class BatchJobParameterScopeRule implements SpringRule {

    @Override
    public String id() {
        return "BAT052_JOB_PARAMETER_WITHOUT_BATCH_SCOPE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringBatch()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath()) || (!file.content().contains("#{jobParameters") && !file.content().contains("#{stepExecutionContext") && !file.content().contains("#{jobExecutionContext"))) {
                continue;
            }
            boolean scoped = file.compilationUnit().findAll(AnnotationExpr.class).stream()
                    .anyMatch(annotation -> annotation.getName().getIdentifier().equals("StepScope")
                            || annotation.getName().getIdentifier().equals("JobScope"));
            if (scoped) {
                continue;
            }
            AnnotationExpr lateBound = file.compilationUnit().findAll(AnnotationExpr.class).stream()
                    .filter(annotation -> annotation.toString().contains("#{jobParameters")
                            || annotation.toString().contains("#{stepExecutionContext")
                            || annotation.toString().contains("#{jobExecutionContext"))
                    .findFirst().orElse(null);
            findings.add(new Finding(
                    id(),
                    Severity.MAJOR,
                    "Late-bound Batch parameter without @StepScope/@JobScope",
                    file.relativePath(),
                    lateBound == null ? null : AstUtils.line(lateBound).orElse(null),
                    "Batch execution context/job parameter expression is used without an evident batch scope in the same source file.",
                    "Job parameters and execution context values are resolved at job/step execution time. Singleton beans can be created before those values exist or accidentally share state between executions.",
                    "Declare the late-bound reader/writer/processor/tasklet bean @StepScope (or @JobScope where appropriate) and keep execution-specific state scoped to the job/step."
            ));
        }
        return findings;
    }
}
