package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for ComplexServiceMethodRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ComplexServiceMethodRule implements SpringRule {

    private final GuardianSettings settings;

    public ComplexServiceMethodRule(GuardianSettings settings) {
        this.settings = settings;
    }

    @Override
    public String id() {
        return "SPR031_COMPLEX_SERVICE_METHOD";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Service")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    int lines = AstUtils.methodLines(method);
                    long branches = method.findAll(Statement.class).stream().filter(this::isBranching).count();

                    if (lines > settings.maxServiceMethodLines() || branches > settings.maxServiceBranchingStatements()) {
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Complex service method",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " has " + lines + " lines and " + branches + " branching statements.",
                                "Complex service methods are hard to test and often mix validation, orchestration and domain rules.",
                                "Extract private methods or dedicated domain/application services. Consider strategy/policy objects for branching logic."
                        ));
                    }
                }
            }
        }

        return findings;
    }

    private boolean isBranching(Statement statement) {
        return statement instanceof IfStmt
                || statement instanceof ForStmt
                || statement instanceof ForEachStmt
                || statement instanceof WhileStmt
                || statement instanceof DoStmt
                || statement instanceof SwitchStmt
                || statement instanceof TryStmt;
    }
}
