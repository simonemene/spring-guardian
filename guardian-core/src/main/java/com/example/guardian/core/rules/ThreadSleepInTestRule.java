package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.List;

public class ThreadSleepInTestRule implements SpringRule {

    @Override
    public String id() {
        return "SPR052_THREAD_SLEEP_IN_TEST";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (!AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (MethodCallExpr call : file.compilationUnit().findAll(MethodCallExpr.class)) {
                boolean threadSleep = call.getNameAsString().equals("sleep")
                        && call.getScope().map(scope -> scope.toString().equals("Thread")).orElse(false);

                if (threadSleep) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Test uses Thread.sleep",
                            file.relativePath(),
                            AstUtils.line(call).orElse(null),
                            "Detected timing-based wait: " + call,
                            "Thread.sleep makes tests slower and flaky because it waits for time instead of a deterministic condition.",
                            "Use Awaitility, latches, virtual time, polling assertions or synchronous test seams."
                    ));
                }
            }
        }

        return findings;
    }
}
