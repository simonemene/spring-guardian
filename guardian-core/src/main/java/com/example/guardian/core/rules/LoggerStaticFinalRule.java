package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for LoggerStaticFinalRule.
 *
 * @author Simone Meneghetti
 */
public class LoggerStaticFinalRule implements SpringRule {

    @Override
    public String id() {
        return "SPR022_LOGGER_SHOULD_BE_STATIC_FINAL";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (FieldDeclaration field : file.compilationUnit().findAll(FieldDeclaration.class)) {
                boolean loggerField = field.getVariables().stream()
                        .map(VariableDeclarator::getTypeAsString)
                        .anyMatch(type -> type.equals("Logger") || type.endsWith(".Logger"));

                if (loggerField && (!field.isStatic() || !field.isFinal() || !field.isPrivate())) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Logger field should be private static final",
                            file.relativePath(),
                            AstUtils.line(field).orElse(null),
                            "Logger field declaration: " + field,
                            "Logger instances are usually immutable per class and should not be instance state.",
                            "Declare logger as private static final Logger log = LoggerFactory.getLogger(CurrentClass.class)."
                    ));
                }
            }
        }

        return findings;
    }
}
