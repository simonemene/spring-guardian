package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Lombok all-arguments constructor usage on Spring components.
 *
 * @author Simone Meneghetti
 */
public class AllArgsConstructorOnSpringComponentRule implements SpringRule {

    @Override
    public String id() {
        return "SPR061_ALL_ARGS_CONSTRUCTOR_ON_SPRING_COMPONENT";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (AstUtils.isSpringComponent(clazz) && AstUtils.hasAnnotation(clazz, "AllArgsConstructor")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Spring component uses @AllArgsConstructor",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Component " + clazz.getNameAsString() + " is annotated with @AllArgsConstructor.",
                            "@AllArgsConstructor can inject every field, including fields that are not real dependencies.",
                            "Prefer explicit constructor injection or @RequiredArgsConstructor with final dependency fields."
                    ));
                }
            }
        }

        return findings;
    }
}
