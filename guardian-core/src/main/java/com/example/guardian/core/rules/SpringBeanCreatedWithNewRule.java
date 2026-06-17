package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects application collaborators that look like Spring beans but are created with new.
 *
 * @author p15518 - Simone Meneghetti
 */
public class SpringBeanCreatedWithNewRule implements SpringRule {

    private static final Set<String> TECHNICAL_SUFFIXES = Set.of("Service", "Repository", "Client", "Gateway", "Adapter", "Component", "Facade", "Handler", "Processor", "Scheduler");
    private static final Set<String> SAFE_SUFFIXES = Set.of("Dto", "DTO", "Request", "Response", "Entity", "Command", "Query", "Event", "Model", "Value", "Builder", "Exception", "Error", "Record", "Spec", "Test");

    @Override
    public String id() {
        return "SPR072_SPRING_BEAN_CREATED_WITH_NEW";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            for (ObjectCreationExpr expression : file.compilationUnit().findAll(ObjectCreationExpr.class)) {
                String type = simpleName(expression.getTypeAsString());
                if (!looksLikeSpringCollaborator(type) || isInsideNonSpringClass(expression)) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Spring-like collaborator created with new",
                        file.relativePath(),
                        AstUtils.line(expression).orElse(null),
                        "Detected manual collaborator creation: " + expression,
                        "Creating application collaborators with new bypasses Spring lifecycle, configuration, proxies, transactions, validation and test replacement.",
                        "Register the collaborator as a bean and inject it through constructor injection."
                ));
            }
        }

        return findings;
    }

    private boolean isInsideNonSpringClass(ObjectCreationExpr expression) {
        return expression.findAncestor(ClassOrInterfaceDeclaration.class)
                .map(clazz -> !AstUtils.isSpringComponent(clazz))
                .orElse(false);
    }

    private boolean looksLikeSpringCollaborator(String type) {
        for (String suffix : SAFE_SUFFIXES) {
            if (type.endsWith(suffix)) {
                return false;
            }
        }
        for (String suffix : TECHNICAL_SUFFIXES) {
            if (type.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private String simpleName(String typeName) {
        int genericIndex = typeName.indexOf('<');
        String withoutGeneric = genericIndex >= 0 ? typeName.substring(0, genericIndex) : typeName;
        int dotIndex = withoutGeneric.lastIndexOf('.');
        return dotIndex >= 0 ? withoutGeneric.substring(dotIndex + 1) : withoutGeneric;
    }
}
