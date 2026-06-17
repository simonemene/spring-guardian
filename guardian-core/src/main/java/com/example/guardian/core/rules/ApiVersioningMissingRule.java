package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for ApiVersioningMissingRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ApiVersioningMissingRule implements SpringRule {

    private final GuardianSettings settings;

    public ApiVersioningMissingRule(GuardianSettings settings) {
        this.settings = settings;
    }

    @Override
    public String id() {
        return "SPR014_API_VERSIONING_MISSING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        String requiredPrefix = settings.requiredApiPrefix();

        for (JavaSourceFile file : context.javaFiles()) {
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!AstUtils.hasAnnotation(clazz, "RestController")) {
                    continue;
                }

                String mapping = clazz.getAnnotations().stream()
                        .filter(a -> a.getName().getIdentifier().equals("RequestMapping"))
                        .findFirst()
                        .map(this::extractFirstString)
                        .orElse("");

                if (mapping.isBlank() || !mapping.startsWith(requiredPrefix)) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "REST controller without configured API prefix",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Controller " + clazz.getNameAsString() + " has class-level mapping '" + mapping + "'. Required prefix: " + requiredPrefix,
                            "A consistent versioned API prefix improves routing, compatibility and API evolution.",
                            "Use @RequestMapping(\"" + requiredPrefix + "/...\") or externalize the base path with a property."
                    ));
                }
            }
        }

        return findings;
    }

    private String extractFirstString(AnnotationExpr annotation) {
        String text = annotation.toString();
        int firstQuote = text.indexOf('"');
        int secondQuote = text.indexOf('"', firstQuote + 1);
        if (firstQuote >= 0 && secondQuote > firstQuote) {
            return text.substring(firstQuote + 1, secondQuote);
        }
        return "";
    }
}
