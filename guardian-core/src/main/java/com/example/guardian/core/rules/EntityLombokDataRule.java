package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Lombok @Data on JPA entities.
 *
 * @author Simone Meneghetti
 */
public class EntityLombokDataRule implements SpringRule {

    @Override
    public String id() {
        return "JPA103_ENTITY_LOMBOK_DATA";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesJpa()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Entity") || !AstUtils.hasAnnotation(clazz, "Data")) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MINOR,
                        "Lombok @Data on JPA entity",
                        file.relativePath(),
                        AstUtils.line(clazz).orElse(null),
                        "Entity " + clazz.getNameAsString() + " is annotated with @Data.",
                        "Generated equals/hashCode/toString across mutable identifiers and relationships can trigger lazy loading, recursion or unstable equality semantics for managed entities.",
                        "Prefer explicit getters/setters and deliberately implement equality/toString using stable fields; use targeted Lombok annotations only where their semantics are safe."
                ));
            }
        }
        return findings;
    }
}
