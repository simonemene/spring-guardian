package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects deprecated Spring Boot Mockito test annotations and suggests Spring Framework replacements.
 *
 * @author Simone Meneghetti
 */
public class MockBeanModernizationAdvisorRule implements SpringRule {

    @Override
    public String id() {
        return "SPR073_MOCKBEAN_MODERNIZATION_ADVISOR";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (!SourceFilePredicates.isTestSource(file)) {
                continue;
            }

            file.compilationUnit().findAll(Node.class).forEach(node -> {
                if (!(node instanceof NodeWithAnnotations<?> annotatedNode)) {
                    return;
                }
                if (AstUtils.hasAnyAnnotation(annotatedNode, "MockBean", "SpyBean", "MockBeans", "SpyBeans")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Deprecated Spring Boot Mockito test annotation",
                            file.relativePath(),
                            AstUtils.line(node).orElse(null),
                            "Detected deprecated Boot test mock annotation on: " + node,
                            "Spring Boot @MockBean and related Mockito test support are deprecated in favor of Spring Framework bean override annotations.",
                            "Evaluate @MockitoBean and @MockitoSpyBean from Spring Framework for new or modernized tests."
                    ));
                }
            });
        }

        return findings;
    }
}
