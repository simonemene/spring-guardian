package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects dependency combinations that usually deserve an explicit architectural decision.
 *
 * @author Simone Meneghetti
 */
public class MavenMixedStackDependencyRule implements SpringRule {

    @Override
    public String id() {
        return "SPR094_MAVEN_MIXED_STACK_DEPENDENCIES";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (Path pom : context.pomFiles()) {
            inspectPom(context, pom, findings);
        }
        return findings;
    }

    private void inspectPom(ProjectScanContext context, Path pom, List<Finding> findings) {
        try {
            String xml = Files.readString(pom, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
            String relative = context.root().relativize(pom).toString();

            if (xml.contains("spring-boot-starter-web") && xml.contains("spring-boot-starter-webflux")) {
                findings.add(finding(relative, "spring-boot-starter-web and spring-boot-starter-webflux are both declared.", "Mixed servlet and reactive web starters detected"));
            }
            if (xml.contains("javax.persistence") && xml.contains("jakarta.persistence")) {
                findings.add(finding(relative, "Both javax.persistence and jakarta.persistence dependencies are present.", "Mixed javax and jakarta persistence APIs detected"));
            }
            if (xml.contains("gson") && xml.contains("jackson-databind")) {
                findings.add(finding(relative, "Both Gson and Jackson are declared in the dependency graph.", "Multiple JSON libraries detected"));
            }
            if ((xml.contains("log4j-core") || xml.contains("log4j-slf4j")) && xml.contains("spring-boot-starter-logging")) {
                findings.add(finding(relative, "Log4j dependencies are declared together with Spring Boot default logging.", "Multiple logging stacks detected"));
            }
        } catch (Exception ignored) {
        }
    }

    private Finding finding(String relative, String evidence, String title) {
        return new Finding(
                id(),
                Severity.MAJOR,
                title,
                relative,
                null,
                evidence,
                "Mixed framework stacks can be valid, but without an explicit decision they often create duplicated auto-configuration, confusing classpaths or inconsistent behavior.",
                "Keep only the required stack or document the reason for the combination and isolate its configuration."
        );
    }
}
