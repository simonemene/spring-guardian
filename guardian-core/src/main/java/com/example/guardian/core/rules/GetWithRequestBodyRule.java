package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects GET endpoints that require a request body.
 *
 * @author Simone Meneghetti
 */
public class GetWithRequestBodyRule implements SpringRule {

    @Override
    public String id() {
        return "WEB101_GET_WITH_REQUEST_BODY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!context.capabilities().usesSpringWeb()) {
            return List.of();
        }
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (MethodDeclaration method : file.compilationUnit().findAll(MethodDeclaration.class)) {
                boolean getEndpoint = AstUtils.hasAnnotation(method, "GetMapping")
                        || method.getAnnotations().stream().anyMatch(annotation ->
                        annotation.getName().getIdentifier().equals("RequestMapping")
                                && annotation.toString().contains("RequestMethod.GET"));
                if (!getEndpoint) {
                    continue;
                }
                boolean requestBody = method.getParameters().stream().anyMatch(parameter -> AstUtils.hasAnnotation(parameter, "RequestBody"));
                if (!requestBody) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "GET endpoint requires request body",
                        file.relativePath(),
                        AstUtils.line(method).orElse(null),
                        "GET endpoint " + method.getNameAsString() + "() contains a @RequestBody parameter.",
                        "HTTP intermediaries, clients and caches do not consistently support or preserve request bodies on GET. The API contract becomes less interoperable and harder to cache safely.",
                        "Move filters to query parameters for a read operation, or use POST for a complex search request body when the payload cannot be represented safely in the URL."
                ));
            }
        }
        return findings;
    }
}
