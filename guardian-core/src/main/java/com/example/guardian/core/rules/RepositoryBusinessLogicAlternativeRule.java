package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects repository classes that appear to contain orchestration or business logic.
 *
 * @author Simone Meneghetti
 */
final class RepositoryBusinessLogicAlternativeRule implements SpringRule {

    @Override
    public String id() {
        return "SPR_ALT011_REPOSITORY_WITH_BUSINESS_LOGIC";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!isRepository(clazz, file)) {
                    continue;
                }
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (method.getBody().isEmpty()) {
                        continue;
                    }
                    if (looksLikeBusinessLogic(method)) {
                        findings.add(new Finding(
                                id(),
                                Severity.INFO,
                                "Repository contains business logic",
                                file.relativePath(),
                                AstUtils.line(method).orElse(null),
                                "Repository method " + method.getNameAsString() + " contains branching, loops or orchestration calls.",
                                "Repositories should keep persistence access focused; business decisions in repositories couple data access to use cases and are harder to test.",
                                "Move orchestration and business decisions to a @Service and keep the repository focused on persistence queries."
                        ));
                    }
                }
            }
        }

        return findings;
    }

    private boolean isRepository(ClassOrInterfaceDeclaration clazz, JavaSourceFile file) {
        String className = clazz.getNameAsString().toLowerCase(Locale.ROOT);
        String path = file.relativePath().replace("\\", "/").toLowerCase(Locale.ROOT);
        return AstUtils.hasAnnotation(clazz, "Repository")
                || className.endsWith("repository")
                || path.contains("/repository/")
                || path.contains("/repositories/");
    }

    private boolean looksLikeBusinessLogic(MethodDeclaration method) {
        int methodLines = AstUtils.methodLines(method);
        boolean hasBranching = !method.findAll(IfStmt.class).isEmpty()
                || !method.findAll(SwitchStmt.class).isEmpty();
        boolean hasLoops = !method.findAll(ForStmt.class).isEmpty()
                || !method.findAll(ForEachStmt.class).isEmpty()
                || !method.findAll(WhileStmt.class).isEmpty()
                || !method.findAll(DoStmt.class).isEmpty();
        boolean callsServiceOrClient = method.findAll(MethodCallExpr.class).stream()
                .map(call -> call.getScope().map(Object::toString).orElse(""))
                .map(scope -> scope.toLowerCase(Locale.ROOT))
                .anyMatch(scope -> scope.contains("service") || scope.contains("client") || scope.contains("mapper") || scope.contains("publisher"));

        return methodLines >= 18 || hasBranching || hasLoops || callsServiceOrClient;
    }
}
