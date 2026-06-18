package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Suggests MockMvcTester when a modern Spring Boot test suite still uses direct MockMvc assertions.
 *
 * @author Simone Meneghetti
 */
public class MockMvcTesterAdvisorRule implements SpringRule {

    @Override
    public String id() {
        return "SPR075_MOCKMVC_TESTER_ADVISOR";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!isModernBootProject(context)) {
            return List.of();
        }

        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (!SourceFilePredicates.isTestSource(file) || !file.content().contains("MockMvc")) {
                continue;
            }
            findings.add(new Finding(
                    id(),
                    Severity.INFO,
                    "MockMvcTester opportunity",
                    file.relativePath(),
                    null,
                    "MockMvc usage detected in a Spring Boot 3.4+ test source.",
                    "Spring Boot can auto-configure MockMvcTester as an AssertJ-based alternative for expressive MVC assertions.",
                    "Evaluate MockMvcTester for new controller slice tests while keeping existing MockMvc tests stable."
            ));
        }
        return findings;
    }

    private boolean isModernBootProject(ProjectScanContext context) {
        String pom = context.pomFiles().stream().map(this::read).reduce("", (left, right) -> left + "\n" + right).toLowerCase(Locale.ROOT);
        return pom.contains("spring-boot") && (pom.contains("3.4") || pom.contains("3.5") || pom.contains("4.0"));
    }

    private String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
