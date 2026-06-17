package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class SpringDependencyVersionOverrideRule implements SpringRule {

    @Override
    public String id() {
        return "SPR033_SPRING_DEPENDENCY_VERSION_OVERRIDDEN";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (Path pom : context.pomFiles()) {
            try {
                var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom.toFile());
                var deps = doc.getElementsByTagName("dependency");
                String relative = context.root().relativize(pom).toString();

                for (int i = 0; i < deps.getLength(); i++) {
                    var dep = deps.item(i);
                    String groupId = childText(dep, "groupId");
                    String artifactId = childText(dep, "artifactId");
                    String version = childText(dep, "version");

                    if (groupId != null && version != null
                            && (groupId.startsWith("org.springframework") || groupId.equals("org.springframework.boot"))) {
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Explicit version on Spring-managed dependency",
                                relative,
                                null,
                                "Dependency " + groupId + ":" + artifactId + " declares version " + version + ".",
                                "Spring Boot dependency management usually controls compatible Spring dependency versions.",
                                "Remove explicit versions for Spring-managed dependencies unless you have a documented reason."
                        ));
                    }
                }
            } catch (Exception ignored) {
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
