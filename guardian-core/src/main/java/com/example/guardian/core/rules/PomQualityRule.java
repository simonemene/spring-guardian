package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PomQualityRule implements SpringRule {

    @Override
    public String id() {
        return "SPR015_MAVEN_VERSION_AND_PROFILE_POLICY";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (Path pom : context.pomFiles()) {
            try {
                String xml = Files.readString(pom, StandardCharsets.UTF_8);
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new InputSource(new StringReader(xml)));

                int dependencyVersions = document.getElementsByTagName("dependency").getLength() == 0
                        ? 0
                        : countDependencyVersionTags(xml);

                boolean hasDependencyManagement = xml.contains("<dependencyManagement>");
                boolean hasProfiles = xml.contains("<profiles>");

                String relative = context.root().relativize(pom).toString();

                if (dependencyVersions >= 3 && !hasDependencyManagement) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Maven dependency versions may be hardcoded",
                            relative,
                            null,
                            "Found several <version> tags under dependencies but no <dependencyManagement> section.",
                            "Repeated dependency versions make upgrades harder and can create inconsistent dependency graphs.",
                            "Use a BOM/dependencyManagement or centralize versions in properties."
                    ));
                }

                if (!hasProfiles) {
                    findings.add(new Finding(
                            id(),
                            Severity.MINOR,
                            "No Maven profiles detected",
                            relative,
                            null,
                            "No <profiles> section found in pom.xml.",
                            "Projects often need explicit dev/test/prod build profiles or at least a conscious profile strategy.",
                            "Add Maven profiles only if build-time differences are needed; otherwise document that runtime Spring profiles are used."
                    ));
                }
            } catch (Exception ignored) {
                // Bad POM parsing should not fail the whole scan in MVP.
            }
        }

        return findings;
    }

    private int countDependencyVersionTags(String xml) {
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<version>", idx)) >= 0) {
            count++;
            idx += "<version>".length();
        }
        return count;
    }
}
