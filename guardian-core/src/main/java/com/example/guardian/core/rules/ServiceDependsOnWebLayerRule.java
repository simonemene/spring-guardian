package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects service classes depending on web layer details.
 *
 * @author Simone Meneghetti
 */
public class ServiceDependsOnWebLayerRule implements SpringRule {

    @Override
    public String id() {
        return "SPR056_SERVICE_DEPENDS_ON_WEB_LAYER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!isService(file, clazz)) {
                    continue;
                }

                String content = file.content();
                if (content.contains("org.springframework.web")
                        || content.contains("jakarta.servlet")
                        || content.contains("javax.servlet")
                        || content.contains("HttpServletRequest")
                        || content.contains("ResponseEntity")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Service depends on web layer details",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Service " + clazz.getNameAsString() + " references web or servlet types.",
                            "Application services should express use cases without depending on HTTP transport details.",
                            "Move HTTP request, response and status handling to controllers and pass domain/application DTOs into services."
                    ));
                }
            }
        }

        return findings;
    }

    private boolean isService(JavaSourceFile file, ClassOrInterfaceDeclaration clazz) {
        String path = file.relativePath().replace("\\", "/").toLowerCase();
        return AstUtils.hasAnnotation(clazz, "Service") || path.contains("/service/") || clazz.getNameAsString().endsWith("Service");
    }
}
