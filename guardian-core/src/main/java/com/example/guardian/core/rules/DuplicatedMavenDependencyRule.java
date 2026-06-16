package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DuplicatedMavenDependencyRule implements SpringRule {

    @Override
    public String id() {
        return "SPR032_DUPLICATED_MAVEN_DEPENDENCY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (Path pom : context.pomFiles()) {
            try {
                var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom.toFile());
                var deps = doc.getElementsByTagName("dependency");
                Map<String, Integer> count = new HashMap<>();

                for (int i = 0; i < deps.getLength(); i++) {
                    var dep = deps.item(i);
                    String groupId = childText(dep, "groupId");
                    String artifactId = childText(dep, "artifactId");
                    if (groupId != null && artifactId != null) {
                        String key = groupId + ":" + artifactId;
                        count.merge(key, 1, Integer::sum);
                    }
                }

                String relative = context.root().relativize(pom).toString();
                count.entrySet().stream()
                        .filter(e -> e.getValue() > 1)
                        .forEach(e -> findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Duplicated Maven dependency",
                                relative,
                                null,
                                "Dependency " + e.getKey() + " is declared " + e.getValue() + " times.",
                                "Duplicated dependencies make dependency management noisy and can hide version conflicts.",
                                "Keep a single declaration and centralize versions in dependencyManagement/properties where needed."
                        )));
            } catch (Exception ignored) {
                // keep scan resilient
            }
        }

        return findings;
    }

    private String childText(org.w3c.dom.Node node, String childName) {
        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (childName.equals(child.getNodeName())) {
                return child.getTextContent().trim();
            }
        }
        return null;
    }
}
