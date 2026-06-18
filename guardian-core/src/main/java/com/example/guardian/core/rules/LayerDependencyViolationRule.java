package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.ImportDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for LayerDependencyViolationRule.
 *
 * @author Simone Meneghetti
 */
public class LayerDependencyViolationRule implements SpringRule {

    @Override
    public String id() {
        return "SPR027_LAYER_DEPENDENCY_VIOLATION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            String path = file.relativePath().replace("\\", "/").toLowerCase();

            for (ImportDeclaration importDeclaration : file.compilationUnit().getImports()) {
                String imported = importDeclaration.getNameAsString().toLowerCase();

                if (path.contains("/repository/") && (imported.contains(".service.") || imported.contains(".controller."))) {
                    findings.add(finding(file, importDeclaration, "Repository layer depends on service/controller layer."));
                }

                if (path.contains("/entity/") && (imported.contains(".service.") || imported.contains(".controller.") || imported.contains(".repository."))) {
                    findings.add(finding(file, importDeclaration, "Entity layer depends on application/web/persistence components."));
                }

                if (path.contains("/dto/") && imported.contains(".entity.")) {
                    findings.add(finding(file, importDeclaration, "DTO layer depends on entity layer."));
                }
            }
        }

        return findings;
    }

    private Finding finding(JavaSourceFile file, ImportDeclaration importDeclaration, String evidence) {
        return new Finding(
                id(),
                Severity.MAJOR,
                "Invalid dependency direction between layers",
                file.relativePath(),
                AstUtils.line(importDeclaration).orElse(null),
                evidence + " Import: " + importDeclaration.getNameAsString(),
                "Layer dependency direction violations make the architecture harder to evolve and test.",
                "Keep dependencies flowing inward/downward according to your chosen architecture; move shared contracts to neutral packages if needed."
        );
    }
}
