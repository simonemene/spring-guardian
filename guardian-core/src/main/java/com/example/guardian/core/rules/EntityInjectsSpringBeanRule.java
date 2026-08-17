package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects dependency injection annotations inside JPA entities.
 *
 * @author Simone Meneghetti
 */
public class EntityInjectsSpringBeanRule implements SpringRule {

    @Override
    public String id() {
        return "ARCH102_ENTITY_INJECTS_SPRING_BEAN";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesJpa()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "Entity")) {
                    continue;
                }
                for (FieldDeclaration field : clazz.getFields()) {
                    if (!AstUtils.hasAnyAnnotation(field, "Autowired", "Inject", "Resource")) {
                        continue;
                    }
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Dependency injection inside JPA entity",
                            file.relativePath(),
                            AstUtils.line(field).orElse(null),
                            "Entity " + clazz.getNameAsString() + " injects dependency into field " + field.getVariables().get(0).getNameAsString() + ".",
                            "JPA entities are persistence/domain objects and are commonly instantiated by the persistence provider, not by the Spring container. Injected collaborators can be null and couple the domain model to application infrastructure.",
                            "Keep the entity focused on domain state/behavior and move infrastructure collaboration to a Spring service/domain service."
                    ));
                }
            }
        }
        return findings;
    }
}
