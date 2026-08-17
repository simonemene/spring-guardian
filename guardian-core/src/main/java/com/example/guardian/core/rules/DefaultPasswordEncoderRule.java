package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects User.withDefaultPasswordEncoder(), intended only for samples/getting started.
 *
 * @author Simone Meneghetti
 */
public class DefaultPasswordEncoderRule implements SpringRule {

    @Override
    public String id() {
        return "SEC102_DEFAULT_PASSWORD_ENCODER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringSecurity()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                if (!call.getNameAsString().equals("withDefaultPasswordEncoder")
                        || call.getScope().map(Object::toString).filter("User"::equals).isEmpty()) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Default sample password encoder used",
                        file.relativePath(),
                        AstUtils.line(call).orElse(null),
                        "User.withDefaultPasswordEncoder() is used.",
                        "This convenience API is designed for examples and embeds password encoding at user construction rather than enforcing a governed password policy.",
                        "Define one PasswordEncoder bean and encode persisted/configured credentials through the same controlled strategy."
                ));
            }
        }
        return findings;
    }
}
