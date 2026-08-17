package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects swallowed runtime exceptions inside transactional methods.
 *
 * @author Simone Meneghetti
 */
public class TransactionalExceptionSwallowedRule implements SpringRule {

    private static final Set<String> ROLLBACK_EXCEPTION_TYPES = Set.of("Exception", "RuntimeException", "Throwable");

    @Override
    public String id() {
        return "TX103_TRANSACTIONAL_EXCEPTION_SWALLOWED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                boolean classTransactional = AstUtils.hasAnnotation(clazz, "Transactional");
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!classTransactional && !AstUtils.hasAnnotation(method, "Transactional")) {
                        continue;
                    }
                    for (CatchClause catchClause : method.findAll(CatchClause.class)) {
                        String caught = catchClause.getParameter().getType().asString();
                        if (!ROLLBACK_EXCEPTION_TYPES.contains(caught) || !catchClause.getBody().findAll(ThrowStmt.class).isEmpty()) {
                            continue;
                        }
                        String body = catchClause.getBody().toString();
                        if (body.contains("setRollbackOnly")) {
                            continue;
                        }
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Transactional exception swallowed",
                                file.relativePath(),
                                AstUtils.line(catchClause).orElse(null),
                                "Transactional method " + method.getNameAsString() + "() catches " + caught + " without rethrowing or marking rollback.",
                                "Spring rolls back by observing failures leaving the transactional boundary. Swallowing a runtime exception can let a partially completed unit of work commit.",
                                "Rethrow/translate the exception, or explicitly mark the transaction rollback-only when continuing locally is intentional."
                        ));
                    }
                }
            }
        }
        return findings;
    }
}
