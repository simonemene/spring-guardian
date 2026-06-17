package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for FatControllerRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class FatControllerRule implements SpringRule {

    private final GuardianSettings settings;

    public FatControllerRule(GuardianSettings settings) {
        this.settings = settings;
    }

    @Override
    public String id() {
        return "SPR004_FAT_CONTROLLER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!AstUtils.hasAnyAnnotation(method, "GetMapping", "PostMapping", "PutMapping", "PatchMapping", "DeleteMapping", "RequestMapping")) {
                        continue;
                    }

                    int lines = AstUtils.methodLines(method);
                    long branches = method.findAll(Statement.class).stream()
                            .filter(this::isBranching)
                            .count();

                    boolean tooLong = lines > settings.maxControllerMethodLines();
                    boolean tooComplex = branches > settings.maxControllerBranchingStatements();

                    if (tooLong || tooComplex) {
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Fat controller method",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Method " + method.getNameAsString() + " has " + lines + " lines and " + branches + " branching statements.",
                                "Controllers with complex logic are harder to test and usually hide business rules that belong in services.",
                                "Move validation/orchestration/business logic into a Service and keep the controller focused on HTTP mapping."
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
