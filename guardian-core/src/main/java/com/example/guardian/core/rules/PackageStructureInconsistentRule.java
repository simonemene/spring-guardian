package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deterministic Spring Guardian rule implementation for PackageStructureInconsistentRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class PackageStructureInconsistentRule implements SpringRule {

    @Override
    public String id() {
        return "SPR028_PACKAGE_STRUCTURE_INCONSISTENT";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            String path = file.relativePath().replace("\\", "/").toLowerCase(Locale.ROOT);

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (AstUtils.hasAnyAnnotation(clazz, "RestController", "Controller") && !path.contains("/controller/")) {
                    findings.add(finding(file, clazz, "Controller class is not under a controller package."));
                }
                if (AstUtils.hasAnnotation(clazz, "Service") && !path.contains("/service/")) {
                    findings.add(finding(file, clazz, "Service class is not under a service package."));
                }
                if (AstUtils.hasAnnotation(clazz, "Repository") && !path.contains("/repository/")) {
                    findings.add(finding(file, clazz, "Repository class is not under a repository package."));
                }
            }
        }

        return findings;
    }

    private Finding finding(JavaSourceFile file, ClassOrInterfaceDeclaration clazz, String evidence) {
        return new Finding(
                id(),
                Severity.MINOR,
                "Spring component package structure is inconsistent",
                file.relativePath(),
                AstUtils.line(clazz).orElse(null),
                evidence + " Class: " + clazz.getNameAsString(),
                "Consistent packages help developers recognize responsibilities and navigate large Spring applications.",
                "Move the class to the conventional package or configure/ignore this rule for feature-based package layouts."
        );
    }
}
