package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.stmt.CatchClause;

import java.util.ArrayList;
import java.util.List;

public class GenericTryCatchRule implements SpringRule {

    @Override
    public String id() {
        return "SPR011_GENERIC_TRY_CATCH";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (CatchClause catchClause : file.compilationUnit().findAll(CatchClause.class)) {
                String type = catchClause.getParameter().getTypeAsString();
                if (type.equals("Exception") || type.equals("Throwable") || type.equals("RuntimeException")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Generic try/catch detected",
                            file.relativePath(),
                            AstUtils.line(catchClause).orElse(null),
                            "Catches generic type: " + type,
                            "Generic catch blocks often hide domain errors and duplicate error handling that should be centralized.",
                            "Prefer specific exceptions, let unchecked exceptions propagate, and map them centrally with @RestControllerAdvice."
                    ));
                }
            }
        }

        return findings;
    }
}
