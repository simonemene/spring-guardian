package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.ReleaseTarget;
import com.example.guardian.core.model.Severity;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects Maven dependency declarations that make release builds less deterministic.
 *
 * @author Simone Meneghetti
 */
public class MavenDependencyHygieneRule implements SpringRule {

    @Override
    public String id() {
        return "SPR095_MAVEN_DEPENDENCY_HYGIENE";
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
            var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom.toFile());
            var dependencies = document.getElementsByTagName("dependency");
            String relative = context.root().relativize(pom).toString();
            for (int i = 0; i < dependencies.getLength(); i++) {
                Node dependency = dependencies.item(i);
                String groupId = childText(dependency, "groupId");
                String artifactId = childText(dependency, "artifactId");
                String version = childText(dependency, "version");
                String scope = childText(dependency, "scope");
                String systemPath = childText(dependency, "systemPath");
                if (groupId == null || artifactId == null) {
                    continue;
                }
                String key = groupId + ":" + artifactId;
                if (version != null && unstableVersion(version)) {
                    findings.add(new Finding(
                            id(),
                            context.profile().releaseTarget() == ReleaseTarget.PRODUCTION ? Severity.MAJOR : Severity.MINOR,
                            "Maven dependency version is not release-stable",
                            relative,
                            null,
                            "Dependency " + key + " declares version " + version + ".",
                            "SNAPSHOT, LATEST, RELEASE or version ranges reduce build reproducibility and make release behavior harder to audit.",
                            "Use a fixed released version managed by dependencyManagement or by the Spring Boot BOM."
                    ));
                }
                if (systemPath != null) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "System-scoped Maven dependency detected",
                            relative,
                            null,
                            "Dependency " + key + " declares systemPath " + systemPath + ".",
                            "System-scoped dependencies bypass repository governance and break reproducible builds on other machines or CI agents.",
                            "Publish the artifact to a Maven repository or replace it with a managed dependency."
                    ));
                }
                if ("org.projectlombok".equals(groupId) && (scope == null || (!scope.equals("provided") && !scope.equals("test")))) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "Lombok dependency scope should be explicit",
                            relative,
                            null,
                            "Lombok dependency has scope " + (scope == null ? "not declared" : scope) + ".",
                            "Annotation processors should not accidentally become runtime dependencies.",
                            "Declare Lombok with provided scope or configure it explicitly as an annotation processor."
                    ));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private boolean unstableVersion(String version) {
        String normalized = version.toUpperCase(Locale.ROOT);
        return normalized.contains("SNAPSHOT")
                || normalized.equals("LATEST")
                || normalized.equals("RELEASE")
                || normalized.startsWith("[")
                || normalized.startsWith("(");
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
