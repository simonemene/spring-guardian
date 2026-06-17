package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for ConsoleLoggingRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ConsoleLoggingRule implements SpringRule {

    @Override
    public String id() {
        return "SPR021_CONSOLE_LOGGING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                String text = call.toString();
                if (text.startsWith("System.out.") || text.startsWith("System.err.")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Console logging detected",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected console call: " + text,
                            "Console output bypasses logging configuration, levels, appenders and observability tools.",
                            "Use SLF4J Logger instead of System.out/System.err."
                    ));
                }

                if (text.endsWith(".printStackTrace()")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "printStackTrace detected",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected call: " + text,
                            "printStackTrace bypasses structured logging and often loses operational context.",
                            "Use a logger with an appropriate level and pass the exception as parameter."
                    ));
                }
            }
        }

        return findings;
    }
}
