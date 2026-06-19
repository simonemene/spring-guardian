package com.example.guardian.core;

import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectModeReport;
import com.example.guardian.core.model.BusinessImpact;
import com.example.guardian.core.model.Effort;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ModernizationChecklistItem;
import com.example.guardian.core.model.OpenRewritePlan;
import com.example.guardian.core.model.OpenRewriteSuggestion;
import com.example.guardian.core.model.ProductionReadinessReport;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.example.guardian.core.model.SpringArchitectureCycle;
import com.example.guardian.core.model.SpringArchitectureMap;
import com.example.guardian.core.model.SpringCapability;
import com.example.guardian.core.model.SpringMaturityAreaScore;
import com.example.guardian.core.model.SpringMaturityScore;
import com.example.guardian.core.model.SpringModernizationPlan;
import com.example.guardian.core.model.SpringModuleDependency;
import com.example.guardian.core.model.SpringModuleSummary;
import com.example.guardian.core.model.SpringProjectFingerprint;
import com.example.guardian.core.model.SpringUpgradePath;
import com.example.guardian.core.model.UpgradeStep;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Builds the deterministic Spring Guardian Architect Mode.
 * <p>
 * This planner does not modify source code and does not call AI services. It turns scan evidence,
 * Spring capabilities and package dependencies into a roadmap that can be used in CI and architecture reviews.
 *
 * @author p15518 - Simone Meneghetti
 */
public class SpringArchitectModePlanner {

    private static final int MAX_CHECKLIST_ITEMS = 50;
    private static final Set<String> LAYER_SEGMENTS = Set.of(
            "controller", "controllers", "web", "api",
            "service", "services", "application",
            "repository", "repositories", "dao",
            "domain", "model", "entity", "entities",
            "client", "clients", "integration", "adapter", "adapters",
            "event", "events", "config", "configuration",
            "batch", "job", "security"
    );

    /**
     * Builds the Architect Mode report.
     *
     * @param context scan context
     * @param findings grouped findings
     * @return deterministic architect mode output
     */
    public ArchitectModeReport plan(ProjectScanContext context, List<FindingGroup> findings) {
        List<FindingGroup> safeFindings = findings == null ? List.of() : findings;
        SpringProjectFingerprint fingerprint = buildFingerprint(context);
        SpringArchitectureMap architectureMap = buildArchitectureMap(context);
        SpringMaturityScore maturityScore = buildMaturityScore(context, safeFindings, architectureMap);
        ProductionReadinessReport productionReadiness = buildProductionReadiness(context, safeFindings);
        SpringUpgradePath upgradePath = buildUpgradePath(context, fingerprint, safeFindings);
        SpringModernizationPlan modernizationPlan = buildModernizationPlan(safeFindings, architectureMap, productionReadiness, upgradePath);
        OpenRewritePlan openRewritePlan = buildOpenRewritePlan(safeFindings, fingerprint, upgradePath);
        return new ArchitectModeReport(
                fingerprint,
                maturityScore,
                architectureMap,
                modernizationPlan,
                productionReadiness,
                upgradePath,
                openRewritePlan
        );
    }

    private SpringProjectFingerprint buildFingerprint(ProjectScanContext context) {
        ProjectCapabilities capabilities = context.capabilities();
        Set<SpringCapability> springCapabilities = new LinkedHashSet<>();
        if (capabilities.usesSpringWeb()) {
            springCapabilities.add(SpringCapability.WEB_MVC);
        }
        if (hasText(context, "spring-boot-starter-webflux", "org.springframework.web.reactive", "webclient")) {
            springCapabilities.add(SpringCapability.WEBFLUX);
        }
        if (capabilities.usesSpringSecurity()) {
            springCapabilities.add(SpringCapability.SECURITY);
        }
        if (hasText(context, "spring-boot-starter-oauth2-resource-server", "oauth2resourceserver")) {
            springCapabilities.add(SpringCapability.OAUTH2_RESOURCE_SERVER);
        }
        if (capabilities.usesJpa()) {
            springCapabilities.add(SpringCapability.DATA_JPA);
        }
        if (hasText(context, "spring-boot-starter-jdbc", "jdbctemplate")) {
            springCapabilities.add(SpringCapability.JDBC);
        }
        if (capabilities.usesSpringBatch()) {
            springCapabilities.add(SpringCapability.BATCH);
        }
        if (capabilities.usesActuator()) {
            springCapabilities.add(SpringCapability.ACTUATOR);
        }
        if (capabilities.usesValidation()) {
            springCapabilities.add(SpringCapability.VALIDATION);
        }
        if (capabilities.usesOpenApi()) {
            springCapabilities.add(SpringCapability.OPENAPI);
        }
        if (hasText(context, "spring-modulith")) {
            springCapabilities.add(SpringCapability.MODULITH);
        }
        if (hasText(context, "spring-ai")) {
            springCapabilities.add(SpringCapability.SPRING_AI);
        }
        if (hasText(context, "testcontainers")) {
            springCapabilities.add(SpringCapability.TESTCONTAINERS);
        }
        if (capabilities.usesLombok()) {
            springCapabilities.add(SpringCapability.LOMBOK);
        }

        String pomText = pomText(context);
        String buildTool = context.pomFiles().isEmpty() ? detectGradle(context.root()) : "MAVEN";
        String javaVersion = firstMatch(pomText,
                "<java.version>\\s*([^<]+)\\s*</java.version>",
                "<maven.compiler.release>\\s*([^<]+)\\s*</maven.compiler.release>",
                "<maven.compiler.source>\\s*([^<]+)\\s*</maven.compiler.source>");
        String springBootVersion = firstMatch(pomText,
                "<spring-boot.version>\\s*([^<]+)\\s*</spring-boot.version>",
                "<artifactId>spring-boot-starter-parent</artifactId>\\s*<version>\\s*([^<]+)\\s*</version>",
                "<artifactId>spring-boot-dependencies</artifactId>\\s*<version>\\s*([^<]+)\\s*</version>");

        List<String> starters = detectedStarters(pomText);
        List<String> annotations = detectedAnnotations(context);
        String summary = buildFingerprintSummary(springCapabilities, capabilities, buildTool, javaVersion, springBootVersion);

        return new SpringProjectFingerprint(
                buildTool,
                blankToUnknown(javaVersion),
                blankToUnknown(springBootVersion),
                context.pomFiles().size() > 1 || pomText.contains("<modules>"),
                capabilities,
                Set.copyOf(springCapabilities),
                starters,
                annotations,
                capabilities.detectedArchitecturalStyles(),
                summary
        );
    }

    private SpringArchitectureMap buildArchitectureMap(ProjectScanContext context) {
        Map<String, SourceDescriptor> descriptors = new LinkedHashMap<>();
        Map<String, String> packageToModule = new HashMap<>();
        Set<String> projectPackages = new LinkedHashSet<>();

        for (JavaSourceFile file : context.javaFiles()) {
            SourceDescriptor descriptor = describe(file);
            descriptors.put(file.relativePath(), descriptor);
            if (!descriptor.packageName().isBlank()) {
                packageToModule.put(descriptor.packageName(), descriptor.module());
                projectPackages.add(descriptor.packageName());
            }
        }

        Map<String, MutableModule> modules = new LinkedHashMap<>();
        for (SourceDescriptor descriptor : descriptors.values()) {
            MutableModule module = modules.computeIfAbsent(descriptor.module(), MutableModule::new);
            module.basePackage = commonPackage(module.basePackage, descriptor.packageName());
            if (descriptor.controller()) {
                module.controllers++;
            }
            if (descriptor.service()) {
                module.services++;
            }
            if (descriptor.repository()) {
                module.repositories++;
            }
            if (descriptor.entity()) {
                module.entities++;
            }
            if (descriptor.configuration()) {
                module.configurations++;
            }
            if (descriptor.client()) {
                module.clients++;
            }
            if (descriptor.event()) {
                module.events++;
            }
            if (descriptor.batch()) {
                module.batchComponents++;
            }
        }

        Map<String, MutableDependency> dependencies = new LinkedHashMap<>();
        for (SourceDescriptor descriptor : descriptors.values()) {
            for (String importedPackage : descriptor.importedPackages()) {
                String targetModule = findModuleForImport(importedPackage, packageToModule, projectPackages);
                if (targetModule == null || targetModule.equals(descriptor.module())) {
                    continue;
                }
                String key = descriptor.module() + "->" + targetModule;
                MutableDependency dependency = dependencies.computeIfAbsent(key, ignored -> new MutableDependency(descriptor.module(), targetModule));
                dependency.weight++;
                if (dependency.examples.size() < 5) {
                    dependency.examples.add(descriptor.simpleName() + " imports " + importedPackage);
                }
            }
        }

        List<SpringModuleDependency> moduleDependencies = dependencies.values().stream()
                .sorted(Comparator.comparing((MutableDependency dependency) -> dependency.from).thenComparing(dependency -> dependency.to))
                .map(dependency -> new SpringModuleDependency(dependency.from, dependency.to, dependency.weight, List.copyOf(dependency.examples)))
                .toList();

        List<SpringArchitectureCycle> cycles = detectCycles(moduleDependencies);
        Set<String> cycleModules = cycles.stream().flatMap(cycle -> cycle.modules().stream()).collect(Collectors.toCollection(LinkedHashSet::new));

        for (SourceDescriptor descriptor : descriptors.values()) {
            MutableModule module = modules.computeIfAbsent(descriptor.module(), MutableModule::new);
            if (descriptor.controllerTalksToRepository()) {
                module.risks.add("Controller accesses a repository directly: introduce an application service boundary.");
            }
            if (descriptor.serviceDependsOnWebLayer()) {
                module.risks.add("Service depends on web/controller layer: keep web adapters outside service/domain code.");
            }
        }

        for (MutableModule module : modules.values()) {
            if (module.controllers > 0 && module.repositories > 0 && module.services == 0) {
                module.risks.add("Missing service layer between controllers and repositories.");
            }
            if (module.name.equals("shared") || module.name.equals("common") || module.name.equals("util") || module.name.equals("utils")) {
                module.risks.add("Shared/common package can become a dumping ground: keep contracts explicit and ownership clear.");
            }
            if (cycleModules.contains(module.name)) {
                module.risks.add("Module participates in an import cycle.");
            }
        }

        List<SpringModuleSummary> summaries = modules.values().stream()
                .sorted(Comparator.comparing(module -> module.name))
                .map(module -> new SpringModuleSummary(
                        module.name,
                        blankToUnknown(module.basePackage),
                        module.controllers,
                        module.services,
                        module.repositories,
                        module.entities,
                        module.configurations,
                        module.clients,
                        module.events,
                        module.batchComponents,
                        List.copyOf(module.risks)
                ))
                .toList();

        List<String> globalRisks = new ArrayList<>();
        if (summaries.stream().anyMatch(module -> module.controllers() > 0 && module.services() == 0 && module.repositories() > 0)) {
            globalRisks.add("At least one module has controllers and repositories without an application service layer.");
        }
        if (!cycles.isEmpty()) {
            globalRisks.add("Package/module dependency cycles detected; consider Spring Modulith-style boundaries and application events.");
        }
        if (moduleDependencies.size() > Math.max(4, summaries.size() * 2)) {
            globalRisks.add("High number of cross-module imports; validate ownership and boundary direction.");
        }

        return new SpringArchitectureMap(
                summaries,
                moduleDependencies,
                cycles,
                mermaid(moduleDependencies),
                List.copyOf(globalRisks)
        );
    }

    private SpringMaturityScore buildMaturityScore(ProjectScanContext context, List<FindingGroup> findings, SpringArchitectureMap map) {
        List<SpringMaturityAreaScore> areas = new ArrayList<>();
        areas.add(area("ARCHITECTURE", "Architecture", 100 - penalty(findings, 10, "ARCH", "SPR005", "SPR007", "SPR027", "SPR028", "SPR055", "SPR056", "SPR057") - (map.cycles().isEmpty() ? 0 : 18) - (map.globalRisks().isEmpty() ? 0 : 10),
                map.globalRisks(), List.of("Introduce clear service/application boundaries.", "Use Spring Modulith-style package ownership for modules.")));
        areas.add(area("WEB_API", "Web/API", 100 - penalty(findings, 8, "WEB", "SPR006", "SPR010", "SPR014", "SPR023", "SPR060", "SPR063", "SPR_ALT006", "SPR_ALT007", "SPR_ALT008", "SPR_ALT009"),
                findTitles(findings, "WEB", "SPR006", "SPR010", "SPR023"), List.of("Use DTOs, Bean Validation, @RestControllerAdvice and ProblemDetail.")));
        areas.add(area("SECURITY", "Security", 100 - penalty(findings, 9, "SEC", "SPR037", "SPR039", "SPR040", "SPR041", "SPR042", "SPR058", "SPR059", "SPR_ALT002", "SPR_ALT003", "SPR_ALT004", "SPR_ALT005"),
                findTitles(findings, "SEC", "SPR039", "SPR040", "SPR041"), List.of("Keep SecurityFilterChain explicit and protect actuator endpoints.")));
        areas.add(area("PERSISTENCE", "Persistence", 100 - penalty(findings, 8, "SPR009", "SPR017", "SPR018", "SPR048", "SPR049", "SPR053", "SPR054", "SPR057", "SPR096", "SPR_ALT010", "SPR_ALT011", "SPR_ALT012", "SPR_ALT013", "SPR_ALT014"),
                findTitles(findings, "SPR017", "SPR096", "SPR_ALT010"), List.of("Keep transactions in service layer, disable OSIV and use DTO/projection fetch plans.")));
        areas.add(area("CONFIGURATION", "Configuration", 100 - penalty(findings, 7, "SPR001", "SPR015", "SPR036", "SPR037", "SPR091", "SPR_ALT018", "SPR_ALT019", "CLD003"),
                findTitles(findings, "SPR037", "SPR091", "SPR_ALT019"), List.of("Externalize secrets and validate @ConfigurationProperties.")));
        areas.add(area("OBSERVABILITY", "Observability", 100 - penalty(findings, 8, "OBS", "CAP003", "SPR074", "SPR_ALT020") - (context.capabilities().usesActuator() ? 0 : 22),
                findTitles(findings, "OBS", "CAP003", "SPR074"), List.of("Add Actuator, Micrometer metrics and structured logging.")));
        areas.add(area("TESTING", "Testing", 100 - penalty(findings, 8, "SPR012", "SPR043", "SPR044", "SPR045", "SPR052") - (context.hasTests() ? 0 : 20),
                findTitles(findings, "SPR012", "SPR043", "SPR044"), List.of("Use focused Spring slice tests and keep a smoke context test.")));
        areas.add(area("PRODUCTION_READINESS", "Production Readiness", 100 - penalty(findings, 7, "CLD", "OBS", "SPR038", "SPR039", "SPR040", "SPR091", "POM037", "POM038") - (context.capabilities().usesActuator() ? 0 : 16),
                findTitles(findings, "CLD", "OBS", "SPR039", "SPR038"), List.of("Lock down management endpoints, externalize runtime config and add production diagnostics.")));
        areas.add(area("SPRING_MODERNITY", "Spring Modernity", 100 - penalty(findings, 4, "ADV", "SPR_ALT", "CAP", "SPR073", "SPR074", "SPR075"),
                findTitles(findings, "ADV", "SPR_ALT", "CAP"), List.of("Adopt Spring-native APIs where they reduce custom infrastructure.")));

        int overall = (int) Math.round(areas.stream().mapToInt(SpringMaturityAreaScore::score).average().orElse(100));
        List<String> weakAreas = areas.stream()
                .filter(area -> area.score() < 70)
                .map(SpringMaturityAreaScore::name)
                .toList();
        List<String> strengths = areas.stream()
                .filter(area -> area.score() >= 80)
                .map(area -> area.name() + " baseline is acceptable")
                .toList();

        return new SpringMaturityScore(
                clamp(overall),
                status(overall),
                List.copyOf(areas),
                strengths,
                weakAreas
        );
    }

    private ProductionReadinessReport buildProductionReadiness(ProjectScanContext context, List<FindingGroup> findings) {
        int score = 100;
        List<String> risks = new ArrayList<>();
        List<String> strengths = new ArrayList<>();

        if (context.capabilities().usesActuator()) {
            strengths.add("Actuator dependency or actuator usage detected.");
        } else if (context.capabilities().usesSpringWeb() || context.capabilities().usesSpringBatch()) {
            score -= 20;
            risks.add("Actuator is not detected for a runnable Spring application.");
        }
        if (hasFinding(findings, "SPR039", "SPR_ALT004")) {
            score -= 18;
            risks.add("Actuator endpoint exposure is too broad.");
        }
        if (hasFinding(findings, "OBS025", "CLD015", "SPR_ALT005")) {
            score -= 12;
            risks.add("Health details appear publicly exposed.");
        }
        if (hasFinding(findings, "SPR037", "CLD003", "SPR_ALT019")) {
            score -= 18;
            risks.add("Secret-like configuration is stored in project files.");
        }
        if (hasFinding(findings, "OBS006", "SPR021", "SPR_ALT020")) {
            score -= 10;
            risks.add("Console logging detected; structured logging is not evident.");
        }
        if (hasFinding(findings, "SPR036")) {
            score -= 8;
            risks.add("Environment/profile strategy is weak or missing.");
        }
        if (hasFinding(findings, "POM037", "POM038")) {
            score -= 8;
            risks.add("Build governance or dependency security checks are not evident.");
        }
        if (risks.isEmpty()) {
            strengths.add("No major production readiness gap detected by the enabled deterministic rules.");
        }

        List<ModernizationChecklistItem> actions = findings.stream()
                .filter(group -> startsWithAny(group.ruleId(), "SPR039", "SPR040", "SPR037", "CLD", "OBS", "POM037", "POM038", "SPR_ALT004", "SPR_ALT005", "SPR_ALT019", "SPR_ALT020"))
                .limit(12)
                .map(group -> toChecklistItem(group, 20, "Production readiness"))
                .toList();

        return new ProductionReadinessReport(
                clamp(score),
                status(score),
                List.copyOf(strengths),
                List.copyOf(risks),
                actions
        );
    }

    private SpringUpgradePath buildUpgradePath(ProjectScanContext context, SpringProjectFingerprint fingerprint, List<FindingGroup> findings) {
        List<UpgradeStep> steps = new ArrayList<>();
        String javaVersion = fingerprint.javaVersion();
        String bootVersion = fingerprint.springBootVersion();

        if (isOldJava(javaVersion)) {
            steps.add(new UpgradeStep(steps.size() + 1, "Move runtime and build to Java 17+",
                    "Modern Spring Boot 3.x and 4.x baselines require a modern Java baseline. Upgrade CI, Docker image and maven-compiler-plugin first.",
                    "HIGH", "Java 17 baseline for modern Spring Boot", List.of("Detected Java version: " + javaVersion)));
        }
        if (isSpringBoot2(bootVersion)) {
            steps.add(new UpgradeStep(steps.size() + 1, "Plan Spring Boot 3 migration",
                    "Move to Spring Boot 3.x before considering Spring Boot 4. Validate Spring Security, Actuator and Jakarta changes.",
                    "HIGH", "Spring Boot 3 migration path", List.of("Detected Spring Boot version: " + bootVersion)));
        }
        if (hasText(context, "javax.persistence", "javax.validation", "javax.servlet")) {
            steps.add(new UpgradeStep(steps.size() + 1, "Migrate javax.* imports to jakarta.*",
                    "Jakarta namespace migration is required for Spring Boot 3+ and should be handled before deeper framework modernization.",
                    "HIGH", "Jakarta EE 9+ namespaces", List.of("javax.* imports detected in source or POM")));
        }
        if (hasFinding(findings, "SPR040", "SPR041", "SPR058", "SPR059")) {
            steps.add(new UpgradeStep(steps.size() + 1, "Modernize Spring Security configuration",
                    "Move toward an explicit SecurityFilterChain, granular authorization and documented stateless API choices.",
                    "MEDIUM", "SecurityFilterChain DSL", matchingRuleIds(findings, "SPR040", "SPR041", "SPR058", "SPR059")));
        }
        if (hasFinding(findings, "SPR073", "SPR074", "SPR075", "ADV003", "SPR_ALT020")) {
            steps.add(new UpgradeStep(steps.size() + 1, "Adopt modern Spring Boot testing/logging/client APIs",
                    "Replace deprecated test annotations, modernize HTTP clients where useful and enable structured logging for production diagnostics.",
                    "MEDIUM", "Spring Boot modern APIs", matchingRuleIds(findings, "SPR073", "SPR074", "SPR075", "ADV003", "SPR_ALT020")));
        }
        if (steps.isEmpty()) {
            steps.add(new UpgradeStep(1, "Keep current Spring baseline governed",
                    "No blocking upgrade signal was detected. Keep dependency management, Java baseline and Spring Boot version explicit.",
                    "LOW", "Spring Boot dependency management", List.of("No major upgrade blocker detected")));
        }

        return new SpringUpgradePath(javaVersion, bootVersion, List.copyOf(steps));
    }

    private SpringModernizationPlan buildModernizationPlan(
            List<FindingGroup> findings,
            SpringArchitectureMap map,
            ProductionReadinessReport productionReadiness,
            SpringUpgradePath upgradePath
    ) {
        List<ModernizationChecklistItem> checklist = new ArrayList<>();

        int priority = 1;
        for (FindingGroup group : findings.stream()
                .sorted(Comparator.comparing(FindingGroup::severity).thenComparing(FindingGroup::ruleId))
                .limit(MAX_CHECKLIST_ITEMS)
                .toList()) {
            checklist.add(toChecklistItem(group, priority++, bucketFor(group)));
        }

        for (String risk : map.globalRisks()) {
            checklist.add(new ModernizationChecklistItem(
                    "ARCH-MAP-" + priority,
                    risk,
                    false,
                    Severity.MAJOR,
                    List.of("Architecture Map"),
                    "The project structure shows coupling or missing boundaries that can make changes risky.",
                    "Spring Modulith-style package boundaries and application events",
                    "Review module ownership, remove cross-boundary repository access and introduce service/event boundaries where needed.",
                    Effort.MEDIUM,
                    BusinessImpact.HIGH,
                    priority++,
                    List.of("ARCHITECTURE_MAP")
            ));
        }

        for (UpgradeStep step : upgradePath.steps()) {
            checklist.add(new ModernizationChecklistItem(
                    "UPGRADE-" + step.order(),
                    step.title(),
                    false,
                    "HIGH".equals(step.risk()) ? Severity.MAJOR : Severity.MINOR,
                    step.evidence(),
                    step.description(),
                    step.springAlternative(),
                    step.description(),
                    "HIGH".equals(step.risk()) ? Effort.HIGH : Effort.MEDIUM,
                    "LOW".equals(step.risk()) ? BusinessImpact.MEDIUM : BusinessImpact.HIGH,
                    priority++,
                    List.of("UPGRADE_PATH")
            ));
        }

        List<ModernizationChecklistItem> limited = checklist.stream()
                .sorted(Comparator.comparingInt(ModernizationChecklistItem::priority))
                .limit(MAX_CHECKLIST_ITEMS)
                .toList();

        List<ModernizationChecklistItem> quickWins = limited.stream()
                .filter(item -> item.effort() == Effort.LOW)
                .limit(12)
                .toList();
        List<ModernizationChecklistItem> architecture = limited.stream()
                .filter(item -> item.relatedFindings().stream().anyMatch(id -> id.startsWith("ARCH") || id.startsWith("SPR006") || id.startsWith("SPR017") || id.startsWith("SPR_ALT006") || id.startsWith("SPR_ALT011") || id.startsWith("ARCHITECTURE_MAP")))
                .limit(14)
                .toList();
        List<ModernizationChecklistItem> production = productionReadiness.requiredActions().isEmpty()
                ? limited.stream().filter(item -> item.relatedFindings().stream().anyMatch(id -> id.startsWith("CLD") || id.startsWith("OBS") || id.startsWith("SPR039") || id.startsWith("SPR040"))).limit(12).toList()
                : productionReadiness.requiredActions();
        List<ModernizationChecklistItem> upgrade = limited.stream()
                .filter(item -> item.relatedFindings().contains("UPGRADE_PATH"))
                .toList();

        return new SpringModernizationPlan(
                quickWins,
                architecture,
                production,
                upgrade,
                limited,
                markdown(limited, map, productionReadiness, upgradePath)
        );
    }

    private OpenRewritePlan buildOpenRewritePlan(List<FindingGroup> findings, SpringProjectFingerprint fingerprint, SpringUpgradePath upgradePath) {
        List<OpenRewriteSuggestion> suggestions = new ArrayList<>();

        if (isSpringBoot2(fingerprint.springBootVersion())) {
            suggestions.add(new OpenRewriteSuggestion(
                    "org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_0",
                    "Spring Boot 2.x baseline detected; use recipes as a guided migration aid.",
                    List.of("UPGRADE_PATH")
            ));
        }
        if (upgradePath.steps().stream().anyMatch(step -> step.title().toLowerCase(Locale.ROOT).contains("jakarta"))) {
            suggestions.add(new OpenRewriteSuggestion(
                    "org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta",
                    "javax.* imports detected; Jakarta migration can be supported by OpenRewrite.",
                    List.of("UPGRADE_PATH")
            ));
        }
        if (hasFinding(findings, "SPR073")) {
            suggestions.add(new OpenRewriteSuggestion(
                    "org.openrewrite.java.spring.boot3.ReplaceMockBeanAndSpyBean",
                    "Legacy Spring Boot mock annotations detected.",
                    matchingRuleIds(findings, "SPR073")
            ));
        }
        if (hasFinding(findings, "SPR091", "SPR_ALT018")) {
            suggestions.add(new OpenRewriteSuggestion(
                    "org.openrewrite.java.spring.boot2.SpringBootProperties_2_7",
                    "Configuration properties should be governed and modernized.",
                    matchingRuleIds(findings, "SPR091", "SPR_ALT018")
            ));
        }
        if (suggestions.isEmpty()) {
            suggestions.add(new OpenRewriteSuggestion(
                    "org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_5",
                    "No mandatory migration recipe detected; keep this as a placeholder for governed Spring Boot upgrades.",
                    List.of("SPRING_GUARDIAN_BASELINE")
            ));
        }

        return new OpenRewritePlan(
                "com.example.guardian.SpringGuardianRecommendedFixes",
                "Spring Guardian recommended fixes",
                List.copyOf(suggestions),
                openRewriteYaml(suggestions)
        );
    }

    private SourceDescriptor describe(JavaSourceFile file) {
        CompilationUnit cu = file.compilationUnit();
        String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
        String content = file.content() == null ? "" : file.content();
        String lower = content.toLowerCase(Locale.ROOT);
        String simpleName = file.absolutePath().getFileName() == null ? file.relativePath() : file.absolutePath().getFileName().toString().replace(".java", "");
        Optional<TypeDeclaration> primary = cu.findFirst(TypeDeclaration.class);
        if (primary.isPresent()) {
            simpleName = primary.get().getNameAsString();
        }

        boolean controller = hasAnnotation(cu, "RestController", "Controller") || lower.contains("@restcontroller") || lower.contains("@controller") || pathContains(file, "/controller/");
        boolean service = hasAnnotation(cu, "Service") || lower.contains("@service") || pathContains(file, "/service/");
        boolean repository = hasAnnotation(cu, "Repository") || lower.contains("@repository") || lower.contains("extends jparepository") || lower.contains("extends crudrepository") || pathContains(file, "/repository/");
        boolean entity = hasAnnotation(cu, "Entity") || lower.contains("@entity") || pathContains(file, "/entity/");
        boolean configuration = hasAnnotation(cu, "Configuration") || lower.contains("@configuration") || pathContains(file, "/config/");
        boolean client = lower.contains("restclient") || lower.contains("webclient") || lower.contains("resttemplate") || pathContains(file, "/client/");
        boolean event = lower.contains("applicationeventpublisher") || lower.contains("@eventlistener") || pathContains(file, "/event/");
        boolean batch = lower.contains("org.springframework.batch") || lower.contains("@enablebatchprocessing") || pathContains(file, "/batch/") || pathContains(file, "/job/");

        List<String> importedPackages = imports(cu);
        boolean controllerTalksToRepository = controller && (lower.contains("repository ") || lower.contains("repository;") || lower.contains("repository.") || importedPackages.stream().anyMatch(value -> value.toLowerCase(Locale.ROOT).contains(".repository")));
        boolean serviceDependsOnWebLayer = service && importedPackages.stream().anyMatch(value -> {
            String normalized = value.toLowerCase(Locale.ROOT);
            return normalized.contains(".controller") || normalized.contains(".web") || normalized.contains("org.springframework.web.bind.annotation");
        });

        String module = moduleName(packageName, file.relativePath());
        return new SourceDescriptor(
                file.relativePath(),
                simpleName,
                packageName,
                module,
                controller,
                service,
                repository,
                entity,
                configuration,
                client,
                event,
                batch,
                importedPackages,
                controllerTalksToRepository,
                serviceDependsOnWebLayer
        );
    }

    private boolean hasAnnotation(CompilationUnit cu, String... simpleNames) {
        Set<String> names = Set.of(simpleNames);
        return cu.findAll(com.github.javaparser.ast.expr.AnnotationExpr.class).stream()
                .map(annotation -> annotation.getName().getIdentifier())
                .anyMatch(names::contains);
    }

    private List<String> imports(CompilationUnit cu) {
        NodeList<ImportDeclaration> declarations = cu.getImports();
        return declarations.stream()
                .map(declaration -> declaration.getNameAsString())
                .toList();
    }

    private String moduleName(String packageName, String relativePath) {
        String normalizedPackage = packageName == null || packageName.isBlank()
                ? packageFromPath(relativePath)
                : packageName;
        String[] parts = normalizedPackage.split("\\.");
        int layerIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if (LAYER_SEGMENTS.contains(parts[i].toLowerCase(Locale.ROOT))) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex > 0) {
            String candidate = parts[layerIndex - 1].toLowerCase(Locale.ROOT);
            if (!candidate.equals("acme") && !candidate.equals("example") && !candidate.equals("company")) {
                return candidate;
            }
        }
        if (parts.length >= 4 && (parts[0].equals("com") || parts[0].equals("org") || parts[0].equals("it"))) {
            return parts[2].equalsIgnoreCase("example") && parts.length >= 5 ? parts[3].toLowerCase(Locale.ROOT) : parts[2].toLowerCase(Locale.ROOT);
        }
        if (parts.length >= 2) {
            return parts[parts.length - 2].toLowerCase(Locale.ROOT);
        }
        return "root";
    }

    private String packageFromPath(String relativePath) {
        String normalized = relativePath == null ? "" : relativePath.replace("\\", "/");
        int javaIndex = normalized.indexOf("/java/");
        if (javaIndex >= 0) {
            normalized = normalized.substring(javaIndex + "/java/".length());
        }
        if (normalized.endsWith(".java")) {
            normalized = normalized.substring(0, normalized.length() - ".java".length());
        }
        int lastSlash = normalized.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return normalized.substring(0, lastSlash).replace("/", ".");
    }

    private String findModuleForImport(String importedPackage, Map<String, String> packageToModule, Set<String> projectPackages) {
        String bestPackage = "";
        String bestModule = null;
        for (String projectPackage : projectPackages) {
            if ((importedPackage.equals(projectPackage) || importedPackage.startsWith(projectPackage + ".")) && projectPackage.length() > bestPackage.length()) {
                bestPackage = projectPackage;
                bestModule = packageToModule.get(projectPackage);
            }
        }
        return bestModule;
    }

    private List<SpringArchitectureCycle> detectCycles(List<SpringModuleDependency> dependencies) {
        Map<String, Set<String>> graph = new LinkedHashMap<>();
        for (SpringModuleDependency dependency : dependencies) {
            graph.computeIfAbsent(dependency.fromModule(), ignored -> new LinkedHashSet<>()).add(dependency.toModule());
            graph.computeIfAbsent(dependency.toModule(), ignored -> new LinkedHashSet<>());
        }

        Set<String> unique = new LinkedHashSet<>();
        List<SpringArchitectureCycle> cycles = new ArrayList<>();
        for (String start : graph.keySet()) {
            dfsCycles(start, start, graph, new ArrayDeque<>(), new HashSet<>(), unique, cycles);
            if (cycles.size() >= 20) {
                break;
            }
        }
        return List.copyOf(cycles);
    }

    private void dfsCycles(
            String start,
            String current,
            Map<String, Set<String>> graph,
            Deque<String> path,
            Set<String> localVisited,
            Set<String> unique,
            List<SpringArchitectureCycle> cycles
    ) {
        path.addLast(current);
        localVisited.add(current);
        for (String next : graph.getOrDefault(current, Set.of())) {
            if (next.equals(start) && path.size() > 1) {
                List<String> cycle = new ArrayList<>(path);
                cycle.add(start);
                String key = canonicalCycleKey(cycle);
                if (unique.add(key)) {
                    cycles.add(new SpringArchitectureCycle(
                            List.copyOf(cycle),
                            "Introduce application events, move shared contracts to an owned package and keep repository access inside the owning module."
                    ));
                }
            } else if (!localVisited.contains(next) && path.size() < 8) {
                dfsCycles(start, next, graph, path, localVisited, unique, cycles);
            }
        }
        path.removeLast();
        localVisited.remove(current);
    }

    private String canonicalCycleKey(List<String> cycle) {
        List<String> withoutLast = new ArrayList<>(cycle);
        if (!withoutLast.isEmpty()) {
            withoutLast.remove(withoutLast.size() - 1);
        }
        return withoutLast.stream().sorted().collect(Collectors.joining("->"));
    }

    private String mermaid(List<SpringModuleDependency> dependencies) {
        StringBuilder builder = new StringBuilder();
        builder.append("flowchart LR\n");
        if (dependencies.isEmpty()) {
            builder.append("    root[No cross-module imports detected]\n");
            return builder.toString();
        }
        dependencies.stream()
                .sorted(Comparator.comparing(SpringModuleDependency::fromModule).thenComparing(SpringModuleDependency::toModule))
                .forEach(dependency -> builder.append("    ")
                        .append(mermaidNode(dependency.fromModule()))
                        .append(" -->|").append(dependency.weight()).append("| ")
                        .append(mermaidNode(dependency.toModule()))
                        .append("\n"));
        return builder.toString();
    }

    private String mermaidNode(String value) {
        String id = value.replaceAll("[^A-Za-z0-9_]", "_");
        return id + "[\"" + value.replace("\"", "") + "\"]";
    }

    private ModernizationChecklistItem toChecklistItem(FindingGroup group, int priority, String bucket) {
        List<String> files = group.affectedComponents() == null ? List.of() : group.affectedComponents().stream()
                .map(this::componentLocation)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .limit(8)
                .toList();
        String alternative = group.guidance() == null ? "" : group.guidance().springAlternative();
        String suggestedChange = group.guidance() == null || group.guidance().recommendedApproach() == null || group.guidance().recommendedApproach().isBlank()
                ? group.suggestedFix()
                : group.guidance().recommendedApproach();

        return new ModernizationChecklistItem(
                "ITEM-" + String.format("%03d", priority),
                "[" + bucket + "] " + group.title(),
                false,
                group.severity(),
                files,
                group.whyItMatters(),
                alternative == null || alternative.isBlank() ? group.category() : alternative,
                suggestedChange,
                effortFor(group),
                impactFor(group),
                priority,
                List.of(group.ruleId())
        );
    }

    private String bucketFor(FindingGroup group) {
        if (group.findingType() != null && group.findingType().equals("SPRING_ALTERNATIVE")) {
            return "Spring alternative";
        }
        return switch (group.findingType() == null ? "" : group.findingType()) {
            case "SECURITY" -> "Security";
            case "WEB_LAYER" -> "API layer";
            case "JPA" -> "Persistence";
            case "OBSERVABILITY", "CLOUD_READINESS" -> "Production readiness";
            case "ARCHITECTURE" -> "Architecture";
            case "TEST" -> "Testing";
            default -> "Modernization";
        };
    }

    private Effort effortFor(FindingGroup group) {
        if (group.occurrences() > 10 || group.severity() == Severity.CRITICAL) {
            return Effort.HIGH;
        }
        if (group.severity() == Severity.MAJOR || group.occurrences() > 3) {
            return Effort.MEDIUM;
        }
        return Effort.LOW;
    }

    private BusinessImpact impactFor(FindingGroup group) {
        if (group.severity() == Severity.CRITICAL || startsWithAny(group.ruleId(), "SPR039", "SPR040", "SPR037", "SPR006", "SPR017", "CLD", "OBS")) {
            return BusinessImpact.HIGH;
        }
        if (group.severity() == Severity.MAJOR || group.findingType() != null && group.findingType().equals("SPRING_ALTERNATIVE")) {
            return BusinessImpact.MEDIUM;
        }
        return BusinessImpact.LOW;
    }

    private String componentLocation(AffectedComponent component) {
        if (component == null) {
            return "";
        }
        String file = component.filePath() == null ? component.name() : component.filePath();
        if (component.line() != null) {
            return file + ":" + component.line();
        }
        return file;
    }

    private String markdown(
            List<ModernizationChecklistItem> checklist,
            SpringArchitectureMap map,
            ProductionReadinessReport productionReadiness,
            SpringUpgradePath upgradePath
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Spring Guardian Modernization Plan\n\n");
        builder.append("## Architecture Map\n\n");
        for (SpringModuleSummary module : map.modules()) {
            builder.append("- **").append(module.name()).append("**: ")
                    .append(module.controllers()).append(" controller, ")
                    .append(module.services()).append(" service, ")
                    .append(module.repositories()).append(" repository, ")
                    .append(module.entities()).append(" entity\n");
            for (String risk : module.risks()) {
                builder.append("  - Risk: ").append(risk).append("\n");
            }
        }
        if (!map.cycles().isEmpty()) {
            builder.append("\n## Cycles\n\n");
            for (SpringArchitectureCycle cycle : map.cycles()) {
                builder.append("- ").append(String.join(" -> ", cycle.modules())).append("\n");
            }
        }
        builder.append("\n## Production Readiness\n\n");
        builder.append("Score: ").append(productionReadiness.score()).append("/100 (").append(productionReadiness.status()).append(")\n\n");
        for (String risk : productionReadiness.risks()) {
            builder.append("- ").append(risk).append("\n");
        }
        builder.append("\n## Upgrade Path\n\n");
        for (UpgradeStep step : upgradePath.steps()) {
            builder.append(step.order()).append(". **").append(step.title()).append("** - ").append(step.risk()).append("\n");
            builder.append("   ").append(step.description()).append("\n");
        }
        builder.append("\n## Checklist\n\n");
        for (ModernizationChecklistItem item : checklist) {
            builder.append("- [ ] ").append(item.title()).append("\n");
            builder.append("  - Priority: ").append(item.priority()).append(" · Effort: ").append(item.effort()).append(" · Impact: ").append(item.businessImpact()).append("\n");
            if (!item.files().isEmpty()) {
                builder.append("  - Files: ").append(String.join(", ", item.files())).append("\n");
            }
            builder.append("  - Suggested change: ").append(item.suggestedChange()).append("\n");
        }
        return builder.toString();
    }

    private String openRewriteYaml(List<OpenRewriteSuggestion> suggestions) {
        StringBuilder builder = new StringBuilder();
        builder.append("type: specs.openrewrite.org/v1beta/recipe\n");
        builder.append("name: com.example.guardian.SpringGuardianRecommendedFixes\n");
        builder.append("displayName: Spring Guardian recommended fixes\n");
        builder.append("recipeList:\n");
        for (OpenRewriteSuggestion suggestion : suggestions) {
            builder.append("  - ").append(suggestion.recipe()).append("\n");
        }
        return builder.toString();
    }

    private SpringMaturityAreaScore area(String code, String name, int score, List<String> drivers, List<String> recommendations) {
        int clamped = clamp(score);
        List<String> safeDrivers = drivers == null || drivers.isEmpty() ? List.of("No blocking signal detected for this area.") : drivers.stream().limit(5).toList();
        return new SpringMaturityAreaScore(code, name, clamped, status(clamped), safeDrivers, recommendations == null ? List.of() : recommendations);
    }

    private int penalty(List<FindingGroup> findings, int severityMultiplier, String... prefixes) {
        int total = 0;
        for (FindingGroup group : findings) {
            if (!startsWithAny(group.ruleId(), prefixes)) {
                continue;
            }
            int base = switch (group.severity()) {
                case CRITICAL -> 4;
                case MAJOR -> 2;
                case MINOR -> 1;
                case INFO -> 1;
            };
            total += base * severityMultiplier;
        }
        return Math.min(85, total);
    }

    private List<String> findTitles(List<FindingGroup> findings, String... prefixes) {
        return findings.stream()
                .filter(group -> startsWithAny(group.ruleId(), prefixes))
                .map(group -> group.ruleId() + " - " + group.title())
                .distinct()
                .limit(5)
                .toList();
    }

    private boolean hasFinding(List<FindingGroup> findings, String... prefixes) {
        return findings.stream().anyMatch(group -> startsWithAny(group.ruleId(), prefixes));
    }

    private List<String> matchingRuleIds(List<FindingGroup> findings, String... prefixes) {
        return findings.stream()
                .map(FindingGroup::ruleId)
                .filter(ruleId -> startsWithAny(ruleId, prefixes))
                .distinct()
                .toList();
    }

    private boolean startsWithAny(String value, String... prefixes) {
        if (value == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private String status(int score) {
        if (score >= 80) {
            return "GOOD";
        }
        if (score >= 60) {
            return "WARNING";
        }
        return "CRITICAL";
    }

    private String buildFingerprintSummary(Set<SpringCapability> capabilities, ProjectCapabilities existing, String buildTool, String javaVersion, String bootVersion) {
        List<String> pieces = new ArrayList<>();
        if (capabilities.contains(SpringCapability.WEB_MVC)) {
            pieces.add("Spring Boot MVC/API");
        }
        if (capabilities.contains(SpringCapability.SECURITY)) {
            pieces.add("Security");
        }
        if (capabilities.contains(SpringCapability.DATA_JPA)) {
            pieces.add("JPA");
        }
        if (capabilities.contains(SpringCapability.BATCH)) {
            pieces.add("Batch");
        }
        if (pieces.isEmpty()) {
            pieces.add("Spring/Java project");
        }
        pieces.add(buildTool);
        if (javaVersion != null && !javaVersion.isBlank()) {
            pieces.add("Java " + javaVersion);
        }
        if (bootVersion != null && !bootVersion.isBlank()) {
            pieces.add("Spring Boot " + bootVersion);
        }
        if (!existing.usesActuator()) {
            pieces.add("Actuator missing");
        }
        if (!existing.usesValidation() && existing.usesSpringWeb()) {
            pieces.add("Validation missing");
        }
        if (!existing.usesOpenApi() && existing.usesSpringWeb()) {
            pieces.add("OpenAPI missing");
        }
        return String.join(" · ", pieces);
    }

    private List<String> detectedStarters(String pomText) {
        Matcher matcher = Pattern.compile("<artifactId>\\s*([^<]*spring[^<]*)\\s*</artifactId>", Pattern.CASE_INSENSITIVE).matcher(pomText);
        List<String> starters = new ArrayList<>();
        while (matcher.find()) {
            String starter = matcher.group(1).trim();
            if (!starter.isBlank() && !starters.contains(starter)) {
                starters.add(starter);
            }
        }
        return List.copyOf(starters);
    }

    private List<String> detectedAnnotations(ProjectScanContext context) {
        Set<String> annotations = new LinkedHashSet<>();
        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(com.github.javaparser.ast.expr.AnnotationExpr.class).stream()
                    .map(annotation -> annotation.getName().getIdentifier())
                    .filter(name -> name.startsWith("Rest") || name.startsWith("Controller") || name.startsWith("Service")
                            || name.startsWith("Repository") || name.startsWith("Entity") || name.startsWith("Transactional")
                            || name.startsWith("Scheduled") || name.startsWith("Async") || name.startsWith("Configuration")
                            || name.startsWith("Bean") || name.startsWith("Validated") || name.startsWith("Valid"))
                    .forEach(annotations::add);
        }
        return List.copyOf(annotations);
    }

    private String pomText(ProjectScanContext context) {
        return context.pomFiles().stream()
                .map(this::readSafely)
                .collect(Collectors.joining("\n"));
    }

    private String firstMatch(String text, String... patterns) {
        if (text == null) {
            return "";
        }
        for (String pattern : patterns) {
            Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return "";
    }

    private boolean hasText(ProjectScanContext context, String... values) {
        String joined = (pomText(context) + "\n" + context.javaFiles().stream().map(JavaSourceFile::content).collect(Collectors.joining("\n"))).toLowerCase(Locale.ROOT);
        for (String value : values) {
            if (joined.contains(value.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String detectGradle(Path root) {
        return Files.exists(root.resolve("build.gradle")) || Files.exists(root.resolve("build.gradle.kts")) ? "GRADLE" : "UNKNOWN";
    }

    private String readSafely(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            return "";
        }
    }

    private boolean isOldJava(String javaVersion) {
        if (javaVersion == null || javaVersion.isBlank() || javaVersion.equals("UNKNOWN")) {
            return false;
        }
        String normalized = javaVersion.replace("1.", "");
        Matcher matcher = Pattern.compile("(\\d+)").matcher(normalized);
        return matcher.find() && Integer.parseInt(matcher.group(1)) < 17;
    }

    private boolean isSpringBoot2(String bootVersion) {
        return bootVersion != null && bootVersion.startsWith("2.");
    }

    private String blankToUnknown(String value) {
        return value == null || value.isBlank() ? "UNKNOWN" : value;
    }

    private String commonPackage(String current, String next) {
        if (current == null || current.isBlank()) {
            return next == null ? "" : next;
        }
        if (next == null || next.isBlank()) {
            return current;
        }
        String[] left = current.split("\\.");
        String[] right = next.split("\\.");
        int max = Math.min(left.length, right.length);
        List<String> shared = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if (!left[i].equals(right[i])) {
                break;
            }
            shared.add(left[i]);
        }
        return shared.isEmpty() ? current : String.join(".", shared);
    }

    private boolean pathContains(JavaSourceFile file, String fragment) {
        String normalized = "/" + file.relativePath().replace("\\", "/").toLowerCase(Locale.ROOT);
        return normalized.contains(fragment);
    }

    private record SourceDescriptor(
            String relativePath,
            String simpleName,
            String packageName,
            String module,
            boolean controller,
            boolean service,
            boolean repository,
            boolean entity,
            boolean configuration,
            boolean client,
            boolean event,
            boolean batch,
            List<String> importedPackages,
            boolean controllerTalksToRepository,
            boolean serviceDependsOnWebLayer
    ) {
    }

    private static final class MutableModule {
        private final String name;
        private String basePackage = "";
        private long controllers;
        private long services;
        private long repositories;
        private long entities;
        private long configurations;
        private long clients;
        private long events;
        private long batchComponents;
        private final Set<String> risks = new LinkedHashSet<>();

        private MutableModule(String name) {
            this.name = name;
        }
    }

    private static final class MutableDependency {
        private final String from;
        private final String to;
        private int weight;
        private final List<String> examples = new ArrayList<>();

        private MutableDependency(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
