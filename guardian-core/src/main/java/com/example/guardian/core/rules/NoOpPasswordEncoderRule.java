package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects use of NoOpPasswordEncoder.
 *
 * @author Simone Meneghetti
 */
public class NoOpPasswordEncoderRule implements SpringRule {

    @Override
    public String id() {
        return "SEC101_NOOP_PASSWORD_ENCODER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringSecurity()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!call.getNameAsString().equals("getInstance")
                        || call.getScope().map(Object::toString).filter("NoOpPasswordEncoder"::equals).isEmpty()) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.CRITICAL,
                        "NoOpPasswordEncoder detected",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "NoOpPasswordEncoder.getInstance() is used.",
                        "NoOpPasswordEncoder stores/compares passwords without one-way password hashing and is unsuitable for real credentials.",
                        "Use a centrally configured DelegatingPasswordEncoder/PasswordEncoder with an adaptive hash such as BCrypt, PBKDF2, scrypt or Argon2."
                ));
            }
        }
        return findings;
    }
}
