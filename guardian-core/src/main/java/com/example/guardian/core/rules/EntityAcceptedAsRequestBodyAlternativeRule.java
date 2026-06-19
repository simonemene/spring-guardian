package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Detects JPA entities accepted as REST request bodies and recommends request DTOs.
 *
 * @author Simone Meneghetti
 */
final class EntityAcceptedAsRequestBodyAlternativeRule implements SpringRule {

    @Override
    public String id() {
        return "SPR_ALT007_ENTITY_ACCEPTED_AS_REQUEST_BODY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Set<String> entities = context.entityClassNames();
        if (entities.isEmpty()) {
            return List.of();
        }

        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller")) {
                    continue;
                }
                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!AstUtils.hasRequestMapping(method)) {
                        continue;
                    }
                    for (Parameter parameter : method.getParameters()) {
                        if (!AstUtils.hasAnnotation(parameter, "RequestBody")) {
                            continue;
                        }
                        String type = parameter.getTypeAsString();
                        if (containsEntity(type, entities)) {
                            findings.add(new Finding(
                                    id(),
                                    Severity.INFO,
                                    "JPA entity accepted as request body",
                                    file.relativePath(),
                                    AstUtils.line(parameter).orElse(AstUtils.line(method).orElse(null)),
                                    "Parameter " + parameter.getNameAsString() + " accepts " + type + " in a REST endpoint.",
                                    "Using JPA entities as input contracts lets external clients shape persistence objects directly and can expose internal fields or relations.",
                                    "Introduce a validated request DTO and map it to the domain/persistence model inside the service layer."
                            ));
                        }
                    }
                }
            }
        }
        return findings;
    }

    private boolean containsEntity(String type, Set<String> entities) {
        return entities.stream().anyMatch(entity ->
                type.equals(entity)
                        || type.endsWith("." + entity)
                        || type.contains("<" + entity + ">")
                        || type.contains("," + entity + ">")
                        || type.contains(" " + entity)
        );
    }
}
