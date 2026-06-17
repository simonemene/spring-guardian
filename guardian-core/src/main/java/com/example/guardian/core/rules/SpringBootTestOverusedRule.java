package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for SpringBootTestOverusedRule.
 *
 * @author p15518 - Simone Meneghetti
 */
public class SpringBootTestOverusedRule implements SpringRule {

    @Override
    public String id() {
        return "SPR044_SPRING_BOOT_TEST_OVERUSED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        long springBootTests = 0;
        long sliceTests = 0;

        for (JavaSourceFile file : context.javaFiles()) {
            if (!AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                if (AstUtils.hasAnnotation(clazz, "SpringBootTest")) {
                    springBootTests++;
                }
                if (AstUtils.hasAnyAnnotation(clazz, "WebMvcTest", "DataJpaTest", "JsonTest", "WebFluxTest")) {
                    sliceTests++;
                }
            }
        }

        if (springBootTests >= 3 && sliceTests == 0) {
            findings.add(new Finding(
                    id(),
                    Severity.MINOR,
                    "@SpringBootTest may be overused",
                    null,
                    null,
                    "Found " + springBootTests + " @SpringBootTest classes and no slice tests.",
                    "Loading the full Spring context for every test can make tests slow and hide layer-specific problems.",
                    "Use slice tests such as @WebMvcTest or @DataJpaTest when full context loading is not required."
            ));
        }

        return findings;
    }
}
