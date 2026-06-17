package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects JPA entities without a spec-compliant no-argument constructor.
 *
 * @author p15518 - Simone Meneghetti
 */
public class JpaEntityConstructorRule implements SpringRule {

    @Override
    public String id() {
        return "SPR053_JPA_ENTITY_ACCESSIBLE_NO_ARGS_CONSTRUCTOR";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Entity")) {
                    continue;
                }

                List<ConstructorDeclaration> constructors = clazz.getConstructors();
                if (constructors.isEmpty()) {
                    continue;
                }

                boolean hasCompliantNoArgs = constructors.stream()
                        .anyMatch(constructor -> constructor.getParameters().isEmpty()
                                && (constructor.isPublic() || constructor.isProtected()));

                if (!hasCompliantNoArgs) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "JPA entity has no public or protected no-argument constructor",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Entity " + clazz.getNameAsString() + " declares constructors but no public or protected no-argument constructor.",
                            "Jakarta Persistence requires a public or protected no-argument constructor so the provider can instantiate the entity.",
                            "Add a protected no-argument constructor and keep public constructors focused on valid domain creation."
                    ));
                }
            }
        }

        return findings;
    }
}
