package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Detects direct Maven dependency declarations that use conflicting versions across the reactor.
 *
 * @author p15518 - Simone Meneghetti
 */
public class MavenDependencyVersionConflictRule implements SpringRule {

    @Override
    public String id() {
        return "SPR093_MAVEN_DEPENDENCY_VERSION_CONFLICT";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Map<String, Set<String>> versionsByDependency = new LinkedHashMap<>();
        Map<String, String> firstLocationByDependency = new LinkedHashMap<>();

        for (Path pom : context.pomFiles()) {
            try {
                var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom.toFile());
                var dependencies = document.getElementsByTagName("dependency");
                String relative = context.root().relativize(pom).toString();
                for (int i = 0; i < dependencies.getLength(); i++) {
                    Node dependency = dependencies.item(i);
                    String groupId = childText(dependency, "groupId");
                    String artifactId = childText(dependency, "artifactId");
                    String version = childText(dependency, "version");
                    if (groupId == null || artifactId == null || version == null || version.isBlank()) {
                        continue;
                    }
                    String key = groupId + ":" + artifactId;
                    versionsByDependency.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(version);
                    firstLocationByDependency.putIfAbsent(key, relative);
                }
            } catch (Exception ignored) {
            }
        }

        List<Finding> findings = new ArrayList<>();
        versionsByDependency.forEach((dependency, versions) -> {
            if (versions.size() > 1) {
                findings.add(new Finding(
                        id(),
                        Severity.MAJOR,
                        "Maven dependency has conflicting versions",
                        firstLocationByDependency.get(dependency),
                        null,
                        "Dependency " + dependency + " is declared with versions " + versions + ".",
                        "Different versions of the same direct dependency can create unstable builds, classpath surprises and hard-to-debug runtime behavior.",
                        "Keep one version per dependency, preferably governed by the Spring Boot BOM, a parent POM or dependencyManagement."
                ));
            }
        });
        return findings;
    }

    private String childText(Node node, String childName) {
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
