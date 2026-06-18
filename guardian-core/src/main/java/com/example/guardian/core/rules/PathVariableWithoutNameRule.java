package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for PathVariableWithoutNameRule.
 *
 * @author Simone Meneghetti
 */
public class PathVariableWithoutNameRule implements SpringRule {

    @Override
    public String id() {
        return "SPR013_PATHVARIABLE_WITHOUT_NAME";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (Parameter parameter : file.compilationUnit().findAll(Parameter.class)) {
                for (AnnotationExpr annotation : parameter.getAnnotations()) {
                    if (!annotation.getName().getIdentifier().equals("PathVariable")) {
                        continue;
                    }

                    boolean hasExplicitName = annotation instanceof SingleMemberAnnotationExpr
                            || annotation instanceof NormalAnnotationExpr normal && normal.getPairs().stream()
                            .anyMatch(pair -> pair.getNameAsString().equals("value") || pair.getNameAsString().equals("name"));

                    if (!hasExplicitName) {
                        findings.add(new Finding(
                                id(),
                                Severity.MINOR,
                                "@PathVariable without explicit name",
                                file.relativePath(),
                                AstUtils.line(parameter).orElse(null),
                                "Parameter " + parameter.getNameAsString() + " uses @PathVariable without value/name.",
                                "Explicit names are more robust against compiler parameter metadata changes and refactoring.",
                                "Use @PathVariable(\"" + parameter.getNameAsString() + "\") or @PathVariable(name = \"" + parameter.getNameAsString() + "\")."
                        ));
                    }
                }
            }
        }

        return findings;
    }
}
