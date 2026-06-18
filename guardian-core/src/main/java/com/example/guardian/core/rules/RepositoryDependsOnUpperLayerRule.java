package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects repository classes depending on service or web layer details.
 *
 * @author Simone Meneghetti
 */
public class RepositoryDependsOnUpperLayerRule implements SpringRule {

    @Override
    public String id() {
        return "SPR057_REPOSITORY_DEPENDS_ON_UPPER_LAYER";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (!isRepository(file, clazz)) {
                    continue;
                }

                String content = file.content();
                if (content.contains(".controller.")
                        || content.contains(".service.")
                        || content.contains("org.springframework.web")
                        || content.contains("ResponseEntity")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Repository depends on upper layers",
                            file.relativePath(),
                            AstUtils.line(clazz).orElse(null),
                            "Repository " + clazz.getNameAsString() + " references service, controller or web types.",
                            "Repositories should stay focused on persistence and must not know orchestration or HTTP concerns.",
                            "Move orchestration to services and keep repositories limited to persistence queries and storage operations."
                    ));
                }
            }
        }

        return findings;
    }

    private boolean isRepository(JavaSourceFile file, ClassOrInterfaceDeclaration clazz) {
        String path = file.relativePath().replace("\\", "/").toLowerCase();
        return AstUtils.hasAnnotation(clazz, "Repository") || path.contains("/repository/") || clazz.getNameAsString().endsWith("Repository");
    }
}
