package com.example.guardian.core.rules;

import com.example.guardian.core.model.ArchitectureStyle;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Spring framework dependencies inside the domain layer for DDD or hexagonal profiles.
 *
 * @author p15518 - Simone Meneghetti
 */
public class DddDomainDependsOnSpringRule implements SpringRule {

    @Override
    public String id() {
        return "SPR055_DOMAIN_LAYER_DEPENDS_ON_SPRING";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        ArchitectureStyle style = context.profile().architectureStyle();
        if (style != ArchitectureStyle.DOMAIN_DRIVEN_DESIGN && style != ArchitectureStyle.HEXAGONAL) {
            return findings;
        }

        for (JavaSourceFile file : context.javaFiles()) {
            String path = file.relativePath().replace("\\", "/").toLowerCase();
            if (!path.contains("/domain/")) {
                continue;
            }
            if (file.content().contains("org.springframework") || file.content().contains("@Service") || file.content().contains("@Component")) {
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Domain layer depends on Spring",
                        file.relativePath(),
                        1,
                        "Domain file imports or uses Spring framework types.",
                        "In DDD or hexagonal architecture the domain should remain independent from framework concerns.",
                        "Move Spring annotations and adapters to application or infrastructure layers and keep the domain model framework-free."
                ));
            }
        }

        return findings;
    }
}
