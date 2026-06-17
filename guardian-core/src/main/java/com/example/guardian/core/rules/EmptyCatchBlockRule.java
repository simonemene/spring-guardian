package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.stmt.CatchClause;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for EmptyCatchBlockRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class EmptyCatchBlockRule implements SpringRule {

    @Override
    public String id() {
        return "SPR047_EMPTY_CATCH_BLOCK";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (CatchClause catchClause : file.compilationUnit().findAll(CatchClause.class)) {
                if (catchClause.getBody().getStatements().isEmpty()) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Empty catch block",
                            file.relativePath(),
                            AstUtils.line(catchClause).orElse(null),
                            "Exception " + catchClause.getParameter().getNameAsString() + " is caught but nothing is done.",
                            "Swallowing exceptions hides production defects, makes observability weaker and can leave data in an inconsistent state.",
                            "Log the exception with context, convert it to a domain/application error, or deliberately rethrow it."
                    ));
                }
            }
        }

        return findings;
    }
}
