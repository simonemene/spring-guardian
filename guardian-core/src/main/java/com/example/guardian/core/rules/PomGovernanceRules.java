package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.ReleaseTarget;
import com.example.guardian.core.model.Severity;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Performs Spring Boot oriented Maven dependency and build governance checks.
 *
 * @author Simone Meneghetti
 */
public class PomGovernanceRules implements SpringRule {

    @Override
    public String id() {
        return "POM000_MAVEN_GOVERNANCE";
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
            document.getDocumentElement().normalize();
            String relative = context.root().relativize(pom).toString();
            String text = document.getDocumentElement().getTextContent().toLowerCase(Locale.ROOT);
            List<Dependency> dependencies = dependencies(document);

            boolean hasBootDependency = dependencies.stream().anyMatch(dep -> dep.groupId().startsWith("org.springframework.boot"));
            boolean hasBootParent = text.contains("spring-boot-starter-parent");
            boolean hasBootBom = text.contains("spring-boot-dependencies");
            if (hasBootDependency && !hasBootParent && !hasBootBom) {
                add(findings, "POM001_BOOT_WITHOUT_PARENT_OR_BOM", Severity.MAJOR, relative,
                        "Spring Boot dependencies found without spring-boot-starter-parent or spring-boot-dependencies BOM.",
                        "Spring Boot projects should rely on the Boot parent or BOM to keep dependency versions aligned.",
                        "Import spring-boot-dependencies in dependencyManagement or use spring-boot-starter-parent when appropriate.");
            }

            for (Dependency dep : dependencies) {
                String key = dep.groupId() + ":" + dep.artifactId();
                if (dep.groupId().equals("org.springframework.boot") && dep.version() != null && dep.artifactId().startsWith("spring-boot-starter")) {
                    add(findings, "POM002_BOOT_STARTER_VERSION_OVERRIDE", Severity.MINOR, relative,
                            key + " declares explicit version " + dep.version() + ".",
                            "Explicit versions on Boot starters can drift away from the Spring Boot BOM and create compatibility issues.",
                            "Remove the explicit version and let the Spring Boot BOM manage it.");
                }
                if (context.profile().releaseTarget() == ReleaseTarget.PRODUCTION && dep.version() != null && unstable(dep.version())) {
                    add(findings, "POM003_UNSTABLE_VERSION_IN_PRODUCTION", Severity.MAJOR, relative,
                            key + " declares unstable version " + dep.version() + ".",
                            "SNAPSHOT, LATEST, RELEASE and version ranges reduce reproducibility of production builds.",
                            "Use fixed released versions managed by dependencyManagement or the Spring Boot BOM.");
                }
                if ("system".equals(dep.scope())) {
                    add(findings, "POM004_SYSTEM_SCOPED_DEPENDENCY", Severity.MAJOR, relative,
                            key + " uses system scope.",
                            "System-scoped dependencies bypass repository governance and break CI reproducibility.",
                            "Publish the artifact to a Maven repository or replace it with a managed dependency.");
                }
                if ("org.projectlombok".equals(dep.groupId()) && (dep.scope() == null || !(dep.scope().equals("provided") || dep.scope().equals("test")))) {
                    add(findings, "POM005_LOMBOK_SCOPE_NOT_EXPLICIT", Severity.MINOR, relative,
                            "Lombok scope is " + (dep.scope() == null ? "not declared" : dep.scope()) + ".",
                            "Lombok should not become an accidental runtime dependency.",
                            "Declare Lombok as provided/test or configure it as an annotation processor.");
                }
                if (isTestLibrary(dep) && dep.scope() == null) {
                    add(findings, "POM006_TEST_DEPENDENCY_WITHOUT_TEST_SCOPE", Severity.MINOR, relative,
                            key + " has no test scope.",
                            "Test libraries in compile scope leak into production classpath and packaged artifacts.",
                            "Declare test libraries with scope test.");
                }
                if ("com.h2database".equals(dep.groupId()) && context.profile().releaseTarget() == ReleaseTarget.PRODUCTION && (dep.scope() == null || dep.scope().equals("compile"))) {
                    add(findings, "POM007_H2_COMPILE_SCOPE_IN_PRODUCTION", Severity.MAJOR, relative,
                            "H2 is available on production compile/runtime classpath.",
                            "Embedded development databases should not be accidentally available in production builds.",
                            "Move H2 to test/runtime dev profile only or remove it from production modules.");
                }
                if ("org.springframework.boot".equals(dep.groupId()) && "spring-boot-devtools".equals(dep.artifactId()) && dep.scope() == null) {
                    add(findings, "POM008_DEVTOOLS_SCOPE_NOT_RESTRICTED", Severity.MINOR, relative,
                            "spring-boot-devtools has no explicit scope/optional marker.",
                            "Devtools is for local development and should not be part of production artifacts.",
                            "Declare devtools as optional/runtime development-only dependency.");
                }
            }

            boolean web = hasArtifact(dependencies, "org.springframework.boot", "spring-boot-starter-web");
            boolean webflux = hasArtifact(dependencies, "org.springframework.boot", "spring-boot-starter-webflux");
            if (web && webflux) {
                add(findings, "POM009_MVC_AND_WEBFLUX_MIXED", Severity.MAJOR, relative,
                        "Both spring-boot-starter-web and spring-boot-starter-webflux are declared.",
                        "Mixing MVC and WebFlux is valid only for explicit hybrid designs and can surprise auto-configuration.",
                        "Keep one web stack unless the mixed stack is intentional and documented.");
            }
            if (text.contains("javax.persistence") && text.contains("jakarta.persistence")) {
                add(findings, "POM010_JAVAX_AND_JAKARTA_MIXED", Severity.MAJOR, relative,
                        "javax.persistence and jakarta.persistence are both present.",
                        "Mixing Java EE and Jakarta namespaces often indicates incomplete migration and runtime incompatibility.",
                        "Align dependencies to the namespace expected by the Spring Boot major version.");
            }
            if (text.contains("spring-boot-starter-logging") && (text.contains("log4j-core") || text.contains("log4j-slf4j"))) {
                add(findings, "POM011_LOGGING_STACK_MIXED", Severity.MINOR, relative,
                        "Default Boot logging and Log4j artifacts appear together.",
                        "Multiple logging stacks can produce duplicate bindings or inconsistent logging behavior.",
                        "Use the default Boot logging stack or explicitly replace it with a coherent alternative.");
            }
            if (text.contains("<repositories>") && context.profile().releaseTarget() == ReleaseTarget.PRODUCTION) {
                add(findings, "POM012_CUSTOM_REPOSITORIES_IN_PROJECT_POM", Severity.MINOR, relative,
                        "Custom repositories are declared in the project POM.",
                        "Project-level repositories can bypass enterprise repository governance and slow reproducible builds.",
                        "Prefer repository governance in Maven settings, corporate parent POM or artifact manager configuration.");
            }
            if (text.contains("<pluginrepositories>")) {
                add(findings, "POM013_CUSTOM_PLUGIN_REPOSITORIES", Severity.MINOR, relative,
                        "Custom pluginRepositories are declared.",
                        "Plugin repositories affect build reproducibility and should be centrally governed.",
                        "Move plugin repository policy to corporate Maven settings or a governed parent.");
            }
            if (hasArtifact(dependencies, "org.springframework.boot", "spring-boot-starter-security") && !context.capabilities().usesSpringSecurity()) {
                add(findings, "POM014_SECURITY_STARTER_WITHOUT_SECURITY_CODE", Severity.MINOR, relative,
                        "Security starter declared but no security code/capability was detected.",
                        "Unused starters increase auto-configuration surface and can confuse release readiness.",
                        "Remove unused starters or add explicit SecurityFilterChain configuration.");
            }
            if (hasArtifact(dependencies, "org.springframework.boot", "spring-boot-starter-actuator") && !text.contains("management.")) {
                add(findings, "POM015_ACTUATOR_WITHOUT_MANAGEMENT_CONFIG", Severity.MINOR, relative,
                        "Actuator starter declared but management configuration is not visible in the POM module scan.",
                        "Actuator should have intentional exposure, health and security settings for production readiness.",
                        "Configure actuator endpoints in application properties/yml and secure management endpoints.");
            }
            if (context.capabilities().usesValidation() && !hasArtifact(dependencies, "org.springframework.boot", "spring-boot-starter-validation") && hasBootDependency) {
                add(findings, "POM016_VALIDATION_USED_WITHOUT_STARTER", Severity.MAJOR, relative,
                        "Validation annotations are used but spring-boot-starter-validation is not declared.",
                        "Bean Validation annotations require a provider on the runtime classpath.",
                        "Add spring-boot-starter-validation or a clearly managed validation provider.");
            }
            if (!text.contains("maven-compiler-plugin") && !text.contains("<java.version>")) {
                add(findings, "POM017_JAVA_VERSION_NOT_GOVERNED", Severity.MINOR, relative,
                        "No java.version or compiler plugin governance detected.",
                        "The Java release level should be explicit and consistent across modules.",
                        "Define java.version and configure maven-compiler-plugin release in the parent.");
            }
            if (text.contains("<packaging>war</packaging>") && text.contains("spring-boot-starter-tomcat") && !text.contains("<scope>provided</scope>")) {
                add(findings, "POM018_WAR_WITH_EMBEDDED_TOMCAT_NOT_PROVIDED", Severity.MAJOR, relative,
                        "WAR packaging uses embedded Tomcat without provided scope.",
                        "WAR deployments to external servlet containers should not package an embedded container accidentally.",
                        "Set spring-boot-starter-tomcat scope to provided for external container WAR deployments.");
            }
            if (text.contains("<packaging>pom</packaging>") && !text.contains("<dependencymanagement>")) {
                add(findings, "POM019_PARENT_WITHOUT_DEPENDENCY_MANAGEMENT", Severity.MINOR, relative,
                        "Parent/aggregator POM has no dependencyManagement.",
                        "Multi-module projects should centralize dependency versions for consistency.",
                        "Add dependencyManagement or import the Spring Boot BOM in the parent.");
            }
            if (text.contains("<packaging>pom</packaging>") && !text.contains("<pluginmanagement>")) {
                add(findings, "POM020_PARENT_WITHOUT_PLUGIN_MANAGEMENT", Severity.MINOR, relative,
                        "Parent/aggregator POM has no pluginManagement.",
                        "Plugin versions and compiler/test settings should be consistent across modules.",
                        "Centralize plugin versions and configuration in pluginManagement.");
            }

            if (text.contains("<exclusions>") && text.contains("*") ) {
                add(findings, "POM021_BROAD_DEPENDENCY_EXCLUSION", Severity.MINOR, relative,
                        "A dependency exclusion appears broad or difficult to govern.",
                        "Broad exclusions can remove transitive libraries required by Spring Boot auto-configuration or security patches.",
                        "Use narrow exclusions and document why the transitive dependency must be removed.");
            }
            if (text.contains("spring-boot-starter-logging") && text.contains("<exclusions>")) {
                add(findings, "POM022_LOGGING_EXCLUSION_REQUIRES_REPLACEMENT", Severity.MAJOR, relative,
                        "Spring Boot default logging appears excluded.",
                        "Excluding the default logging stack without a coherent replacement can break diagnostics.",
                        "Add an explicit logging replacement strategy and test startup logging.");
            }
            if (text.contains("<artifactid>spring-boot-maven-plugin</artifactid>") && text.contains("<packaging>pom</packaging>")) {
                add(findings, "POM023_BOOT_PLUGIN_IN_AGGREGATOR", Severity.MINOR, relative,
                        "spring-boot-maven-plugin appears in an aggregator POM.",
                        "Aggregator or library modules should not normally be repackaged as boot applications.",
                        "Move executable plugin configuration to application modules or put shared settings in pluginManagement.");
            }
            if (text.contains("<packaging>jar</packaging>") && text.contains("<scope>provided</scope>") && text.contains("spring-boot-starter-tomcat")) {
                add(findings, "POM024_JAR_WITH_PROVIDED_SERVLET_CONTAINER", Severity.MINOR, relative,
                        "JAR packaging uses provided servlet container scope.",
                        "Executable JAR applications normally package the embedded servlet container.",
                        "Use provided scope only for WAR/external-container deployment.");
            }
            if (text.contains("junit:junit") && text.contains("org.junit.jupiter")) {
                add(findings, "POM025_JUNIT4_AND_JUNIT5_MIXED", Severity.MINOR, relative,
                        "JUnit 4 and JUnit Jupiter appear together.",
                        "Mixed test engines can be valid, but should be intentional during migration.",
                        "Use JUnit Jupiter consistently or add Vintage explicitly with a migration plan.");
            }
            if (text.contains("mockito-core") && text.contains("spring-boot-starter-test")) {
                add(findings, "POM026_MOCKITO_DUPLICATED_WITH_STARTER_TEST", Severity.MINOR, relative,
                        "mockito-core is declared together with spring-boot-starter-test.",
                        "Spring Boot starter-test already manages Mockito for most test scenarios.",
                        "Remove duplicate Mockito dependencies unless a specific version override is required and documented.");
            }
            if (text.contains("gson") && text.contains("jackson")) {
                add(findings, "POM027_GSON_AND_JACKSON_MIXED", Severity.MINOR, relative,
                        "Gson and Jackson appear together.",
                        "Multiple JSON stacks can produce inconsistent serialization rules.",
                        "Prefer the Spring Boot managed Jackson stack or document why Gson is required.");
            }
            if (text.contains("javax.validation") && text.contains("jakarta.validation")) {
                add(findings, "POM028_JAVAX_JAKARTA_VALIDATION_MIXED", Severity.MAJOR, relative,
                        "javax.validation and jakarta.validation appear together.",
                        "Mixed validation namespaces often indicate an incomplete Boot 2 to Boot 3 migration.",
                        "Align validation dependencies with the Spring Boot major version.");
            }
            if (text.contains("<maven.compiler.source>") && text.contains("<maven.compiler.release>")) {
                add(findings, "POM029_COMPILER_SOURCE_TARGET_RELEASE_DUPLICATED", Severity.MINOR, relative,
                        "Compiler source/target/release settings appear duplicated.",
                        "Duplicated compiler settings can diverge across modules.",
                        "Use a single maven-compiler-plugin release configuration governed by the parent.");
            }
            if (text.contains("<repositories>") && text.contains("snapshot")) {
                add(findings, "POM030_SNAPSHOT_REPOSITORY_IN_PROJECT", Severity.MINOR, relative,
                        "A snapshot repository is declared in the project POM.",
                        "Snapshot repositories reduce release reproducibility when not centrally governed.",
                        "Move repository policy to Maven settings or a governed corporate parent.");
            }
            if (text.contains("maven-surefire-plugin") && !text.contains("maven-failsafe-plugin") && context.profile().releaseTarget() == ReleaseTarget.PRODUCTION) {
                add(findings, "POM031_FAILSAFE_PLUGIN_MISSING_FOR_IT", Severity.INFO, relative,
                        "Failsafe plugin is not evident in a production profile scan.",
                        "Integration tests are easier to separate from unit tests when Failsafe is configured.",
                        "Evaluate maven-failsafe-plugin for integration and end-to-end test phases.");
            }
            if (text.contains("skiptests") || text.contains("maven.test.skip")) {
                add(findings, "POM032_TESTS_SKIPPED_IN_BUILD_CONFIG", Severity.MAJOR, relative,
                        "The build configuration can skip tests.",
                        "Skipping tests in committed build configuration can hide regressions in CI.",
                        "Avoid committed skip flags and configure CI profiles explicitly.");
            }
            if (text.contains("<scope>runtime</scope>") && text.contains("lombok")) {
                add(findings, "POM033_LOMBOK_RUNTIME_SCOPE", Severity.MINOR, relative,
                        "Lombok appears with runtime scope.",
                        "Lombok is an annotation processor and should not be required at runtime.",
                        "Use provided/optional or annotationProcessor configuration.");
            }
            if (text.contains("spring-cloud") && !text.contains("spring-cloud-dependencies")) {
                add(findings, "POM034_SPRING_CLOUD_WITHOUT_BOM", Severity.MAJOR, relative,
                        "Spring Cloud dependencies appear without Spring Cloud BOM.",
                        "Spring Cloud versions must be aligned with the Spring Boot release train.",
                        "Import spring-cloud-dependencies in dependencyManagement.");
            }
            if (text.contains("testcontainers") && !text.contains("<scope>test</scope>")) {
                add(findings, "POM035_TESTCONTAINERS_WITHOUT_TEST_SCOPE", Severity.MINOR, relative,
                        "Testcontainers dependency may not be scoped to test.",
                        "Testcontainers should not leak into production classpath.",
                        "Declare Testcontainers dependencies with scope test.");
            }
            if (text.contains("annotationProcessorPaths") && !text.contains("lombok")) {
                add(findings, "POM036_ANNOTATION_PROCESSOR_GOVERNANCE_REVIEW", Severity.INFO, relative,
                        "Annotation processor paths are configured.",
                        "Annotation processors affect generated code and should be governed consistently.",
                        "Keep processors centralized in the parent and document MapStruct/Lombok/query processors.");
            }
            if (text.contains("maven-enforcer-plugin") == false && context.profile().releaseTarget() == ReleaseTarget.PRODUCTION) {
                add(findings, "POM037_ENFORCER_PLUGIN_NOT_CONFIGURED", Severity.INFO, relative,
                        "Maven Enforcer plugin is not configured.",
                        "Enterprise builds benefit from enforcing Java, Maven and dependency convergence rules.",
                        "Evaluate maven-enforcer-plugin in the parent POM.");
            }
            if (text.contains("dependency-check-maven") == false && context.profile().releaseTarget() == ReleaseTarget.PRODUCTION) {
                add(findings, "POM038_DEPENDENCY_SECURITY_SCAN_NOT_EVIDENT", Severity.INFO, relative,
                        "Dependency security scan plugin is not evident.",
                        "Dependency vulnerability checks complement Spring architecture readiness.",
                        "Evaluate OWASP Dependency Check, Snyk, GitHub Dependabot or equivalent governance in CI.");
            }
            if (text.contains("<relativePath/>") && text.contains("spring-boot-starter-parent")) {
                add(findings, "POM039_PARENT_RELATIVE_PATH_DISABLED", Severity.INFO, relative,
                        "Parent relativePath is disabled.",
                        "This can be intentional, but parent resolution should be reproducible in CI.",
                        "Ensure the parent is resolvable from governed Maven repositories.");
            }
            if (text.contains("<modules>") && !text.contains("guardian") && !text.contains("<dependencyManagement>")) {
                add(findings, "POM040_MULTI_MODULE_WITHOUT_VERSION_GOVERNANCE", Severity.MAJOR, relative,
                        "Multi-module project without visible dependency governance.",
                        "Multi-module projects need centralized dependency and plugin versions to avoid drift.",
                        "Add dependencyManagement and pluginManagement to the parent POM.");
            }
        } catch (Exception ignored) {
        }
    }

    private List<Dependency> dependencies(org.w3c.dom.Document document) {
        List<Dependency> dependencies = new ArrayList<>();
        var nodes = document.getElementsByTagName("dependency");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (!(node instanceof Element element)) {
                continue;
            }
            String groupId = childText(element, "groupId");
            String artifactId = childText(element, "artifactId");
            if (groupId == null || artifactId == null) {
                continue;
            }
            dependencies.add(new Dependency(groupId, artifactId, childText(element, "version"), childText(element, "scope")));
        }
        return dependencies;
    }

    private void add(List<Finding> findings, String ruleId, Severity severity, String relative, String evidence, String why, String fix) {
        if (alreadyPresent(findings, ruleId, evidence)) {
            return;
        }
        findings.add(new Finding(ruleId, severity, titleFor(ruleId), relative, null, evidence, why, fix));
    }

    private boolean alreadyPresent(List<Finding> findings, String ruleId, String evidence) {
        return findings.stream().anyMatch(finding -> finding.ruleId().equals(ruleId) && finding.evidence().equals(evidence));
    }

    private String titleFor(String ruleId) {
        String normalized = ruleId.substring(ruleId.indexOf('_') + 1).replace('_', ' ').toLowerCase(Locale.ROOT);
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private boolean hasArtifact(List<Dependency> dependencies, String groupId, String artifactId) {
        return dependencies.stream().anyMatch(dep -> dep.groupId().equals(groupId) && dep.artifactId().equals(artifactId));
    }

    private boolean unstable(String version) {
        String normalized = version.toUpperCase(Locale.ROOT);
        return normalized.contains("SNAPSHOT") || normalized.equals("LATEST") || normalized.equals("RELEASE") || normalized.startsWith("[") || normalized.startsWith("(");
    }

    private boolean isTestLibrary(Dependency dependency) {
        Set<String> groups = new LinkedHashSet<>(Set.of("org.junit.jupiter", "org.junit", "org.mockito", "org.assertj", "org.testcontainers"));
        return groups.contains(dependency.groupId()) || dependency.artifactId().contains("junit") || dependency.artifactId().contains("mockito");
    }

    private String childText(Element node, String childName) {
        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (childName.equals(child.getNodeName())) {
                String value = child.getTextContent();
                return value == null || value.isBlank() ? null : value.trim();
            }
        }
        return null;
    }

    private record Dependency(String groupId, String artifactId, String version, String scope) {
    }
}
