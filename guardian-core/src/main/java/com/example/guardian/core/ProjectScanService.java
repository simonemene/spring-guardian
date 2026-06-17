package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureAreaReport;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.CategorySummary;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.QualityGate;
import com.example.guardian.core.model.RecommendedAction;
import com.example.guardian.core.model.ReleaseReadiness;
import com.example.guardian.core.model.ReleaseTarget;
import com.example.guardian.core.model.ReportExplanation;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.ReportSummary;
import com.example.guardian.core.model.Severity;
import com.example.guardian.core.rules.GuardianRules;
import com.example.guardian.core.rules.SpringRule;
import com.example.guardian.core.scanner.ProjectSourceScanner;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Executes deterministic Spring architecture rules and builds the final review report.
 *
 * @author p15518 - Simone Meneghetti
 */
public class ProjectScanService {

    private final ProjectSourceScanner scanner;
    private final List<SpringRule> rules;

    /**
     * Creates a scan service using the configured rule thresholds.
     *
     * @param settings scanner and rule settings
     */
    public ProjectScanService(GuardianSettings settings) {
        this.scanner = new ProjectSourceScanner();
        this.rules = GuardianRules.defaultRules(settings);
    }

    /**
     * Scans a project and returns the report in Italian.
     *
     * @param root project root
     * @return architecture review report
     */
    public ArchitectureReviewReport scan(Path root) {
        return scan(root, ReportLanguage.ITALIAN, ProjectProfile.defaults());
    }

    /**
     * Scans a project and returns the report in the selected language.
     *
     * @param root project root
     * @param language report language
     * @return architecture review report
     */
    public ArchitectureReviewReport scan(Path root, ReportLanguage language) {
        return scan(root, language, ProjectProfile.defaults());
    }

    /**
     * Scans a project with the selected language and stateless profile.
     *
     * @param root project root
     * @param language report language
     * @param profile stateless scan profile
     * @return architecture review report
     */
    public ArchitectureReviewReport scan(Path root, ReportLanguage language, ProjectProfile profile) {
        ReportLanguage resolvedLanguage = language == null ? ReportLanguage.ITALIAN : language;
        ProjectProfile resolvedProfile = profile == null ? ProjectProfile.defaults() : profile;
        ProjectScanContext context = scanner.scan(root, resolvedProfile);

        List<Finding> findings = rules.stream()
                .flatMap(rule -> rule.evaluate(context).stream())
                .sorted(Comparator
                        .comparing(Finding::severity)
                        .thenComparing(Finding::ruleId)
                        .thenComparing(finding -> finding.filePath() == null ? "" : finding.filePath())
                        .thenComparing(finding -> finding.line() == null ? 0 : finding.line()))
                .toList();

        Map<Severity, Long> bySeverity = findings.stream()
                .collect(Collectors.groupingBy(Finding::severity, LinkedHashMap::new, Collectors.counting()));

        for (Severity severity : Severity.values()) {
            bySeverity.putIfAbsent(severity, 0L);
        }

        int score = calculateScore(findings, resolvedProfile);
        String riskLevel = calculateRiskLevel(score, bySeverity);
        List<FindingGroup> groupedFindings = buildFindingGroups(findings, resolvedLanguage);
        List<ArchitectureAreaReport> architectureAreas = buildArchitectureAreas(findings, resolvedLanguage);
        List<QualityGate> qualityGates = buildQualityGates(findings, architectureAreas, context, resolvedLanguage);
        ReleaseReadiness releaseReadiness = buildReleaseReadiness(qualityGates, findings, resolvedProfile, resolvedLanguage);

        return new ArchitectureReviewReport(
                root.getFileName() == null ? root.toString() : root.getFileName().toString(),
                Instant.now(),
                resolvedProfile,
                context.capabilities(),
                buildSummary(findings, bySeverity, riskLevel, releaseReadiness.status(), resolvedLanguage),
                releaseReadiness,
                score,
                riskLevel,
                context.javaFiles().size(),
                context.pomFiles().size(),
                rules.size(),
                bySeverity,
                buildCategorySummaries(findings, resolvedLanguage),
                architectureAreas,
                qualityGates,
                buildRecommendedActions(groupedFindings, resolvedLanguage),
                buildExplanation(resolvedLanguage),
                groupedFindings
        );
    }

    private int calculateScore(List<Finding> findings, ProjectProfile profile) {
        int penalty = 0;
        for (Finding finding : findings) {
            int weight = switch (finding.severity()) {
                case CRITICAL -> 12;
                case MAJOR -> 6;
                case MINOR -> 2;
                case INFO -> 1;
            };
            if (profile.knownIssuesAccepted() && finding.severity() == Severity.MAJOR && !securityRule(finding.ruleId())) {
                weight = Math.max(2, weight / 2);
            }
            penalty += weight;
        }
        return Math.max(0, 100 - penalty);
    }

    private String calculateRiskLevel(int score, Map<Severity, Long> bySeverity) {
        if (bySeverity.getOrDefault(Severity.CRITICAL, 0L) > 0 || score < 50) {
            return "HIGH";
        }
        if (bySeverity.getOrDefault(Severity.MAJOR, 0L) >= 5 || score < 75) {
            return "MEDIUM";
        }
        if (bySeverity.getOrDefault(Severity.MINOR, 0L) > 0 || score < 90) {
            return "LOW";
        }
        return "HEALTHY";
    }

    private ReportSummary buildSummary(List<Finding> findings, Map<Severity, Long> bySeverity, String riskLevel, String readinessStatus, ReportLanguage language) {
        long critical = bySeverity.getOrDefault(Severity.CRITICAL, 0L);
        long major = bySeverity.getOrDefault(Severity.MAJOR, 0L);
        long minor = bySeverity.getOrDefault(Severity.MINOR, 0L);
        long info = bySeverity.getOrDefault(Severity.INFO, 0L);
        String status = switch (readinessStatus) {
            case "READY" -> "NO_FINDINGS";
            case "READY_WITH_WARNINGS" -> "REVIEW_RECOMMENDED";
            default -> critical > 0 ? "ACTION_REQUIRED" : major > 0 ? "IMPROVEMENT_REQUIRED" : findings.isEmpty() ? "NO_FINDINGS" : "REVIEW_RECOMMENDED";
        };
        String summary = language == ReportLanguage.ENGLISH ? englishSummaryFor(riskLevel, readinessStatus) : italianSummaryFor(riskLevel, readinessStatus);
        return new ReportSummary(findings.size(), critical, major, minor, info, riskLevel, status, summary);
    }

    private String italianSummaryFor(String riskLevel, String readinessStatus) {
        if ("NOT_READY".equals(readinessStatus)) {
            return "Il progetto non è ancora pronto per il rilascio secondo il profilo selezionato: correggi prima i blocchi critici e le aree ad alto impatto.";
        }
        if ("READY_WITH_WARNINGS".equals(readinessStatus)) {
            return "Il progetto può procedere solo con consapevolezza tecnica: non ci sono blocchi critici, ma restano miglioramenti da pianificare.";
        }
        return switch (riskLevel) {
            case "HIGH" -> "Il progetto contiene problemi architetturali, di sicurezza o correttezza da gestire prima di considerarlo pronto per produzione o pipeline CI.";
            case "MEDIUM" -> "Il progetto è utilizzabile, ma ci sono diversi interventi di manutenzione, qualità Spring o hardening da pianificare.";
            case "LOW" -> "Il progetto ha una base generalmente accettabile, con miglioramenti minori consigliati.";
            default -> "Le regole deterministiche abilitate non hanno rilevato rischi Spring rilevanti.";
        };
    }

    private String englishSummaryFor(String riskLevel, String readinessStatus) {
        if ("NOT_READY".equals(readinessStatus)) {
            return "The project is not release-ready for the selected profile yet: fix critical blockers and high-impact areas first.";
        }
        if ("READY_WITH_WARNINGS".equals(readinessStatus)) {
            return "The project can move forward only with technical awareness: no critical blockers remain, but improvements should be planned.";
        }
        return switch (riskLevel) {
            case "HIGH" -> "The project contains architecture, security or correctness issues that should be handled before considering it production-ready or CI-ready.";
            case "MEDIUM" -> "The project is usable, but several maintenance, Spring quality or hardening actions should be planned.";
            case "LOW" -> "The project has a generally acceptable baseline, with minor improvements recommended.";
            default -> "The enabled deterministic rules did not detect relevant Spring risks.";
        };
    }

    private List<CategorySummary> buildCategorySummaries(List<Finding> findings, ReportLanguage language) {
        Map<String, List<Finding>> byCategory = findings.stream()
                .collect(Collectors.groupingBy(finding -> categoryFor(finding, language), LinkedHashMap::new, Collectors.toList()));

        List<CategorySummary> summaries = new ArrayList<>();
        byCategory.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, List<Finding>>>comparingLong(entry -> -countSeverity(entry.getValue(), Severity.CRITICAL))
                        .thenComparingLong(entry -> -countSeverity(entry.getValue(), Severity.MAJOR))
                        .thenComparingLong(entry -> -severityWeight(entry.getValue()))
                        .thenComparing(Map.Entry::getKey))
                .forEach(entry -> {
                    List<Finding> categoryFindings = entry.getValue();
                    summaries.add(new CategorySummary(
                            entry.getKey(),
                            categoryFindings.size(),
                            countSeverity(categoryFindings, Severity.CRITICAL),
                            countSeverity(categoryFindings, Severity.MAJOR),
                            countSeverity(categoryFindings, Severity.MINOR),
                            countSeverity(categoryFindings, Severity.INFO),
                            categoryExplanation(entry.getKey(), language)
                    ));
                });

        return summaries;
    }

    private long severityWeight(List<Finding> findings) {
        return findings.stream()
                .mapToLong(finding -> switch (finding.severity()) {
                    case CRITICAL -> 100;
                    case MAJOR -> 40;
                    case MINOR -> 10;
                    case INFO -> 1;
                })
                .sum();
    }

    private long countSeverity(List<Finding> findings, Severity severity) {
        return findings.stream().filter(finding -> finding.severity() == severity).count();
    }

    private List<FindingGroup> buildFindingGroups(List<Finding> findings, ReportLanguage language) {
        Map<String, List<Finding>> byRule = findings.stream()
                .collect(Collectors.groupingBy(Finding::ruleId, LinkedHashMap::new, Collectors.toList()));

        List<FindingGroup> groups = new ArrayList<>();
        byRule.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, List<Finding>>>comparingInt(entry -> severityRank(entry.getValue().get(0).severity()))
                        .thenComparing(Map.Entry::getKey))
                .forEach(entry -> {
                    List<Finding> ruleFindings = entry.getValue().stream()
                            .sorted(Comparator
                                    .comparing((Finding finding) -> finding.filePath() == null ? "" : finding.filePath())
                                    .thenComparing(finding -> finding.line() == null ? 0 : finding.line()))
                            .toList();
                    Finding first = ruleFindings.get(0);
                    groups.add(new FindingGroup(
                            first.ruleId(),
                            first.severity(),
                            categoryFor(first, language),
                            RuleTextCatalog.title(first.ruleId(), first.title(), language),
                            ruleFindings.size(),
                            ruleFindings.stream().map(this::affectedComponentOf).toList(),
                            RuleTextCatalog.why(first.ruleId(), first.whyItMatters(), language),
                            RuleTextCatalog.fix(first.ruleId(), first.suggestedFix(), language),
                            groupExplanation(first, ruleFindings.size(), language)
                    ));
                });

        return groups;
    }

    private int severityRank(Severity severity) {
        return switch (severity) {
            case CRITICAL -> 0;
            case MAJOR -> 1;
            case MINOR -> 2;
            case INFO -> 3;
        };
    }

    private AffectedComponent affectedComponentOf(Finding finding) {
        return new AffectedComponent(
                componentTypeOf(finding),
                componentNameOf(finding),
                finding.filePath(),
                finding.line(),
                finding.evidence()
        );
    }

    private String componentTypeOf(Finding finding) {
        String path = finding.filePath();
        if (path == null || path.isBlank()) {
            return "PROJECT";
        }

        String normalized = path.replace("\\", "/");
        if (normalized.endsWith(".java")) {
            return normalized.contains("/src/test/") || normalized.startsWith("src/test/") ? "TEST_CLASS" : "JAVA_CLASS";
        }
        if (normalized.endsWith("pom.xml")) {
            return "MAVEN_POM";
        }
        if (normalized.endsWith(".yml") || normalized.endsWith(".yaml") || normalized.endsWith(".properties")) {
            return "CONFIG_FILE";
        }
        return "FILE";
    }

    private String componentNameOf(Finding finding) {
        String path = finding.filePath();
        if (path == null || path.isBlank()) {
            return "project";
        }

        String normalized = path.replace("\\", "/");
        String fileName = normalized.substring(normalized.lastIndexOf('/') + 1);
        if (fileName.endsWith(".java")) {
            return fileName.substring(0, fileName.length() - ".java".length());
        }
        return fileName;
    }

    private String groupExplanation(Finding first, int occurrences, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            String target = occurrences == 1 ? "1 affected component" : occurrences + " affected components";
            return target + " matched this rule. "
                    + RuleTextCatalog.why(first.ruleId(), first.whyItMatters(), language)
                    + " Recommended action: "
                    + RuleTextCatalog.fix(first.ruleId(), first.suggestedFix(), language);
        }

        String target = occurrences == 1 ? "1 componente coinvolto" : occurrences + " componenti coinvolti";
        return target + " per questa regola. "
                + RuleTextCatalog.why(first.ruleId(), first.whyItMatters(), language)
                + " Intervento consigliato: "
                + RuleTextCatalog.fix(first.ruleId(), first.suggestedFix(), language);
    }

    private List<RecommendedAction> buildRecommendedActions(List<FindingGroup> groupedFindings, ReportLanguage language) {
        List<FindingGroup> prioritized = groupedFindings.stream()
                .filter(group -> group.severity() == Severity.CRITICAL || group.severity() == Severity.MAJOR)
                .limit(12)
                .toList();

        List<RecommendedAction> actions = new ArrayList<>();
        for (int i = 0; i < prioritized.size(); i++) {
            FindingGroup group = prioritized.get(i);
            actions.add(new RecommendedAction(
                    i + 1,
                    group.severity(),
                    group.ruleId(),
                    group.title(),
                    locationOf(group, language),
                    group.whyItMatters(),
                    group.suggestedFix()
            ));
        }
        return actions;
    }

    private String locationOf(FindingGroup group, ReportLanguage language) {
        if (group.affectedComponents().isEmpty()) {
            return language == ReportLanguage.ENGLISH ? "project" : "progetto";
        }
        AffectedComponent first = group.affectedComponents().get(0);
        String firstLocation = first.filePath() == null || first.filePath().isBlank()
                ? first.name()
                : first.filePath() + (first.line() == null ? "" : ":" + first.line());

        if (group.occurrences() <= 1) {
            return firstLocation;
        }

        String suffix = language == ReportLanguage.ENGLISH
                ? " and " + (group.occurrences() - 1) + " more occurrences"
                : " e altre " + (group.occurrences() - 1) + " occorrenze";
        return firstLocation + suffix;
    }

    private ReportExplanation buildExplanation(ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return new ReportExplanation(
                    "The score starts from 100 and applies weighted penalties based on severity. Critical findings have the strongest impact. Legacy baseline mode reduces the weight of non-security high findings but does not hide them.",
                    "Critical means a potential production, security or architecture blocker. High means a concrete maintenance or correctness risk. Medium means a recommended improvement. Info is low-impact guidance.",
                    "Start from release readiness, then quality gates, then recommended actions. Findings are grouped by deterministic rule to keep the report readable on large projects.",
                    List.of(
                            "Fix every critical finding before considering the CI scan green.",
                            "Use the project profile only to calibrate the current stateless scan; Spring Guardian does not persist baselines.",
                            "Review findings by area: dependency injection, web layer, security, JPA, architecture, tests, build and Spring Alternative Advisor.",
                            "Keep deterministic rules as the source of truth. No AI calls, database calls or hidden state are required."
                    )
            );
        }

        return new ReportExplanation(
                "Il punteggio parte da 100 e applica penalità pesate in base alla severità. I problemi critici hanno il peso maggiore. La modalità baseline legacy riduce il peso dei problemi alti non di sicurezza, ma non li nasconde.",
                "Critico indica un possibile blocco per produzione, sicurezza o architettura. Alto indica un rischio concreto di manutenzione o correttezza. Medio indica un miglioramento consigliato. Info è un suggerimento a basso impatto.",
                "Parti dalla prontezza al rilascio, poi dai quality gate e infine dalle azioni consigliate. I problemi sono raggruppati per regola deterministica per mantenere leggibile il report anche su progetti grandi.",
                List.of(
                        "Correggi tutti i problemi critici prima di considerare verde la scansione in CI.",
                        "Usa il profilo progetto solo per calibrare la scansione corrente stateless: Spring Guardian non salva baseline.",
                        "Rivedi i problemi per area: dependency injection, web layer, sicurezza, JPA, architettura, test, build e Spring Alternative Advisor.",
                        "Mantieni le regole deterministiche come fonte di verità. Non servono chiamate AI, database o stato nascosto."
                )
        );
    }

    private List<ArchitectureAreaReport> buildArchitectureAreas(List<Finding> findings, ReportLanguage language) {
        List<AreaDefinition> definitions = areaDefinitions(language);
        List<ArchitectureAreaReport> reports = new ArrayList<>();
        for (AreaDefinition definition : definitions) {
            List<Finding> areaFindings = findings.stream().filter(finding -> definition.matches(finding.ruleId())).toList();
            long critical = countSeverity(areaFindings, Severity.CRITICAL);
            long major = countSeverity(areaFindings, Severity.MAJOR);
            String status = critical > 0 ? "BLOCKED" : major > 0 ? "ATTENTION" : areaFindings.isEmpty() ? "OK" : "REVIEW";
            reports.add(new ArchitectureAreaReport(
                    definition.code(),
                    definition.name(),
                    definition.description(),
                    areaFindings.size(),
                    critical,
                    major,
                    status
            ));
        }
        return reports;
    }

    private List<QualityGate> buildQualityGates(List<Finding> findings, List<ArchitectureAreaReport> areas, ProjectScanContext context, ReportLanguage language) {
        List<QualityGate> gates = new ArrayList<>();
        gates.add(gate("GATE_SECURITY", language, area(areas, "SECURITY"), true));
        gates.add(gate("GATE_WEB_LAYER", language, area(areas, "WEB_LAYER"), context.capabilities().usesSpringWeb()));
        gates.add(gate("GATE_JPA", language, area(areas, "JPA_PERSISTENCE"), context.capabilities().usesJpa()));
        gates.add(gate("GATE_DEPENDENCY_INJECTION", language, area(areas, "DEPENDENCY_INJECTION"), true));
        gates.add(gate("GATE_ARCHITECTURE_BOUNDARIES", language, area(areas, "ARCHITECTURE_BOUNDARIES"), true));
        gates.add(gate("GATE_TESTS", language, area(areas, "TESTS"), true));
        gates.add(gate("GATE_BUILD_CONFIG", language, area(areas, "BUILD_CONFIG"), true));
        gates.add(gate("GATE_SPRING_ALTERNATIVE_ADVISOR", language, area(areas, "SPRING_ALTERNATIVE_ADVISOR"), false));
        gates.add(new QualityGate(
                "GATE_PROFILE_ALIGNMENT",
                gateName("GATE_PROFILE_ALIGNMENT", language),
                profileAlignmentStatus(context),
                profileAlignmentExplanation(context, language),
                true,
                profileAlignmentStatus(context).equals("FAIL") ? 1 : 0
        ));
        return gates;
    }

    private QualityGate gate(String code, ReportLanguage language, ArchitectureAreaReport area, boolean required) {
        long failing = area == null ? 0 : area.criticalFindings() + area.majorFindings();
        String status = area == null || failing == 0 ? "PASS" : area.criticalFindings() > 0 ? "FAIL" : "WARNING";
        return new QualityGate(code, gateName(code, language), status, gateExplanation(code, area, language), required, failing);
    }

    private ArchitectureAreaReport area(List<ArchitectureAreaReport> areas, String code) {
        return areas.stream().filter(area -> area.code().equals(code)).findFirst().orElse(null);
    }

    private ReleaseReadiness buildReleaseReadiness(List<QualityGate> gates, List<Finding> findings, ProjectProfile profile, ReportLanguage language) {
        List<QualityGate> blockingGates = gates.stream()
                .filter(QualityGate::required)
                .filter(gate -> gate.status().equals("FAIL") || strictMajorBlocks(gate, profile))
                .toList();

        List<String> blockers = blockingGates.stream()
                .map(gate -> gate.name() + ": " + gate.explanation())
                .toList();

        List<String> warnings = gates.stream()
                .filter(gate -> gate.status().equals("WARNING") && !strictMajorBlocks(gate, profile))
                .map(gate -> gate.name() + ": " + gate.explanation())
                .collect(Collectors.toCollection(ArrayList::new));

        if (profile.knownIssuesAccepted() || profile.releaseTarget() == ReleaseTarget.LEGACY_BASELINE) {
            warnings.add(language == ReportLanguage.ENGLISH
                    ? "Legacy baseline calibration is active: non-security high findings are prioritized but do not automatically block this stateless scan."
                    : "Calibrazione baseline legacy attiva: i problemi alti non di sicurezza sono prioritari, ma non bloccano automaticamente questa scansione stateless.");
        }

        String status = !blockers.isEmpty() ? "NOT_READY" : warnings.isEmpty() && findings.isEmpty() ? "READY" : "READY_WITH_WARNINGS";
        String label = releaseLabel(status, language);
        String explanation = releaseExplanation(status, profile, language);
        return new ReleaseReadiness(status, label, explanation, blockers.isEmpty(), blockers, warnings);
    }

    private boolean strictMajorBlocks(QualityGate gate, ProjectProfile profile) {
        if (!gate.status().equals("WARNING")) {
            return false;
        }
        if (profile.releaseTarget() == ReleaseTarget.INTERNAL || profile.releaseTarget() == ReleaseTarget.LEGACY_BASELINE || profile.knownIssuesAccepted()) {
            return gate.code().equals("GATE_SECURITY");
        }
        return true;
    }

    private String profileAlignmentStatus(ProjectScanContext context) {
        boolean expectsDdd = context.profile().architectureStyle().name().equals("DOMAIN_DRIVEN_DESIGN") || context.profile().architectureStyle().name().equals("HEXAGONAL");
        if (expectsDdd && (!context.capabilities().hasDomainLayer() || !context.capabilities().hasApplicationLayer())) {
            return "FAIL";
        }
        return "PASS";
    }

    private String profileAlignmentExplanation(ProjectScanContext context, ReportLanguage language) {
        boolean expectsDdd = context.profile().architectureStyle().name().equals("DOMAIN_DRIVEN_DESIGN") || context.profile().architectureStyle().name().equals("HEXAGONAL");
        if (expectsDdd && (!context.capabilities().hasDomainLayer() || !context.capabilities().hasApplicationLayer())) {
            return language == ReportLanguage.ENGLISH
                    ? "The selected DDD or hexagonal profile expects clear domain and application layers, but they were not both detected."
                    : "Il profilo DDD o esagonale selezionato richiede layer domain e application chiari, ma non sono stati rilevati entrambi.";
        }
        return language == ReportLanguage.ENGLISH
                ? "The selected project profile is consistent with the detected structure for this stateless scan."
                : "Il profilo progetto selezionato è coerente con la struttura rilevata in questa scansione stateless.";
    }

    private String releaseLabel(String status, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return switch (status) {
                case "READY" -> "Ready for release";
                case "READY_WITH_WARNINGS" -> "Releasable with warnings";
                default -> "Not ready for release";
            };
        }
        return switch (status) {
            case "READY" -> "Pronto al rilascio";
            case "READY_WITH_WARNINGS" -> "Rilasciabile con avvertenze";
            default -> "Non pronto al rilascio";
        };
    }

    private String releaseExplanation(String status, ProjectProfile profile, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return switch (status) {
                case "READY" -> "No blocking findings were detected by the deterministic gates for the selected profile.";
                case "READY_WITH_WARNINGS" -> "The scan has no blocking failures for the selected profile, but the remaining warnings should be planned before scaling the project.";
                default -> "The selected profile blocks release until the listed gates are fixed.";
            };
        }
        return switch (status) {
            case "READY" -> "I gate deterministici non hanno rilevato blocchi per il profilo selezionato.";
            case "READY_WITH_WARNINGS" -> "La scansione non ha blocchi per il profilo selezionato, ma le avvertenze restanti vanno pianificate prima di far scalare il progetto.";
            default -> "Il profilo selezionato blocca il rilascio finché i gate indicati non vengono corretti.";
        };
    }

    private String gateName(String code, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return switch (code) {
                case "GATE_SECURITY" -> "Security";
                case "GATE_WEB_LAYER" -> "Web layer and API contracts";
                case "GATE_JPA" -> "JPA and persistence";
                case "GATE_DEPENDENCY_INJECTION" -> "Dependency injection";
                case "GATE_ARCHITECTURE_BOUNDARIES" -> "Architecture boundaries";
                case "GATE_TESTS" -> "Tests";
                case "GATE_BUILD_CONFIG" -> "Build and configuration";
                case "GATE_SPRING_ALTERNATIVE_ADVISOR" -> "Spring Alternative Advisor";
                case "GATE_PROFILE_ALIGNMENT" -> "Profile alignment";
                default -> code;
            };
        }
        return switch (code) {
            case "GATE_SECURITY" -> "Sicurezza";
            case "GATE_WEB_LAYER" -> "Layer web e contratti API";
            case "GATE_JPA" -> "JPA e persistenza";
            case "GATE_DEPENDENCY_INJECTION" -> "Dependency injection";
            case "GATE_ARCHITECTURE_BOUNDARIES" -> "Confini architetturali";
            case "GATE_TESTS" -> "Test";
            case "GATE_BUILD_CONFIG" -> "Build e configurazione";
            case "GATE_SPRING_ALTERNATIVE_ADVISOR" -> "Spring Alternative Advisor";
            case "GATE_PROFILE_ALIGNMENT" -> "Coerenza profilo";
            default -> code;
        };
    }

    private String gateExplanation(String code, ArchitectureAreaReport area, ReportLanguage language) {
        long findings = area == null ? 0 : area.findings();
        if (language == ReportLanguage.ENGLISH) {
            return findings == 0 ? "No blocking findings detected in this area." : findings + " findings require review in this area.";
        }
        return findings == 0 ? "Nessun problema bloccante rilevato in quest'area." : findings + " problemi richiedono revisione in quest'area.";
    }

    private List<AreaDefinition> areaDefinitions(ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return List.of(
                    new AreaDefinition("DEPENDENCY_INJECTION", "Dependency injection", "Constructor injection, field injection, immutable dependencies and Spring component wiring.", List.of("SPR002", "SPR029", "SPR061", "SPR062")),
                    new AreaDefinition("WEB_LAYER", "Web layer and API contracts", "Controller boundaries, DTOs, validation, versioning, HTTP semantics and OpenAPI documentation.", List.of("SPR003", "SPR004", "SPR006", "SPR010", "SPR013", "SPR014", "SPR019", "SPR023", "SPR024", "SPR050", "SPR051", "SPR056", "SPR060", "SPR063")),
                    new AreaDefinition("SECURITY", "Spring Security", "Spring Security, secrets, actuator exposure, CSRF, CORS and password handling.", List.of("SPR037", "SPR039", "SPR040", "SPR041", "SPR042", "SPR046", "SPR058", "SPR059")),
                    new AreaDefinition("JPA_PERSISTENCE", "JPA and persistence", "Entity mapping, transactions, repository calls, fetch plans, schema safety and persistence boundaries.", List.of("SPR009", "SPR017", "SPR038", "SPR048", "SPR049", "SPR053", "SPR054", "SPR057")),
                    new AreaDefinition("ARCHITECTURE_BOUNDARIES", "Architecture and boundaries", "Layer direction, DDD or hexagonal boundaries, package structure and class responsibilities.", List.of("SPR005", "SPR007", "SPR008", "SPR018", "SPR027", "SPR028", "SPR030", "SPR031", "SPR055")),
                    new AreaDefinition("RUNTIME_CORRECTNESS", "Runtime correctness", "Exception handling, null-safety, optional handling, hidden proxy effects and unsafe state changes.", List.of("SPR011", "SPR020", "SPR025", "SPR047")),
                    new AreaDefinition("TESTS", "Tests", "Test presence, assertions, test slicing, heavy context usage and time-based flakiness.", List.of("SPR012", "SPR043", "SPR044", "SPR045", "SPR052")),
                    new AreaDefinition("BUILD_CONFIG", "Build and configuration", "Maven quality, Spring BOM alignment, profiles, hardcoded values, logging and maintainability.", List.of("SPR001", "SPR015", "SPR016", "SPR021", "SPR022", "SPR032", "SPR033", "SPR036")),
                    new AreaDefinition("SPRING_ALTERNATIVE_ADVISOR", "Spring Alternative Advisor", "Manual Java objects, low-level APIs and modern Spring alternatives worth evaluating.", List.of("SPR064", "SPR065", "SPR066", "SPR067", "SPR068", "SPR069", "SPR070", "SPR071", "SPR072", "SPR073", "SPR074", "SPR075"))
            );
        }
        return List.of(
                new AreaDefinition("DEPENDENCY_INJECTION", "Dependency injection", "Constructor injection, field injection, dipendenze immutabili e cablaggio dei componenti Spring.", List.of("SPR002", "SPR029", "SPR061", "SPR062")),
                new AreaDefinition("WEB_LAYER", "Layer web e contratti API", "Confini dei controller, DTO, validazione, versionamento, semantica HTTP e documentazione OpenAPI.", List.of("SPR003", "SPR004", "SPR006", "SPR010", "SPR013", "SPR014", "SPR019", "SPR023", "SPR024", "SPR050", "SPR051", "SPR056", "SPR060", "SPR063")),
                new AreaDefinition("SECURITY", "Spring Security", "Spring Security, segreti, esposizione actuator, CSRF, CORS e gestione password.", List.of("SPR037", "SPR039", "SPR040", "SPR041", "SPR042", "SPR046", "SPR058", "SPR059")),
                new AreaDefinition("JPA_PERSISTENCE", "JPA e persistenza", "Mapping entity, transazioni, chiamate repository, fetch plan, sicurezza schema e confini di persistenza.", List.of("SPR009", "SPR017", "SPR038", "SPR048", "SPR049", "SPR053", "SPR054", "SPR057")),
                new AreaDefinition("ARCHITECTURE_BOUNDARIES", "Architettura e confini", "Direzione dei layer, confini DDD o esagonali, struttura package e responsabilità delle classi.", List.of("SPR005", "SPR007", "SPR008", "SPR018", "SPR027", "SPR028", "SPR030", "SPR031", "SPR055")),
                new AreaDefinition("RUNTIME_CORRECTNESS", "Correttezza runtime", "Gestione eccezioni, null-safety, Optional, effetti nascosti dei proxy e modifiche stato non sicure.", List.of("SPR011", "SPR020", "SPR025", "SPR047")),
                new AreaDefinition("TESTS", "Test", "Presenza test, assert, slice test, uso del contesto Spring e fragilità temporale.", List.of("SPR012", "SPR043", "SPR044", "SPR045", "SPR052")),
                new AreaDefinition("BUILD_CONFIG", "Build e configurazione", "Qualità Maven, allineamento BOM Spring, profili, valori hardcoded, logging e manutenibilità.", List.of("SPR001", "SPR015", "SPR016", "SPR021", "SPR022", "SPR032", "SPR033", "SPR036")),
                new AreaDefinition("SPRING_ALTERNATIVE_ADVISOR", "Spring Alternative Advisor", "Oggetti Java manuali, API di basso livello e alternative Spring moderne da valutare.", List.of("SPR064", "SPR065", "SPR066", "SPR067", "SPR068", "SPR069", "SPR070", "SPR071", "SPR072", "SPR073", "SPR074", "SPR075"))
        );
    }

    private String categoryFor(Finding finding, ReportLanguage language) {
        String ruleId = finding.ruleId();
        if (matches(ruleId, "SPR037", "SPR039", "SPR040", "SPR041", "SPR042", "SPR046", "SPR058", "SPR059")) {
            return language == ReportLanguage.ENGLISH ? "Spring Security" : "Spring Security";
        }
        if (matches(ruleId, "SPR006", "SPR010", "SPR013", "SPR014", "SPR023", "SPR024", "SPR050", "SPR051", "SPR056", "SPR060", "SPR063")) {
            return language == ReportLanguage.ENGLISH ? "Web layer and REST contracts" : "Layer web e contratti REST";
        }
        if (matches(ruleId, "SPR008", "SPR011", "SPR017", "SPR018", "SPR020", "SPR025", "SPR047")) {
            return language == ReportLanguage.ENGLISH ? "Runtime correctness" : "Correttezza runtime";
        }
        if (matches(ruleId, "SPR009", "SPR026", "SPR038", "SPR048", "SPR049", "SPR053", "SPR054", "SPR057")) {
            return language == ReportLanguage.ENGLISH ? "JPA, persistence and integrations" : "JPA, persistenza e integrazioni";
        }
        if (matches(ruleId, "SPR002", "SPR029", "SPR061", "SPR062")) {
            return language == ReportLanguage.ENGLISH ? "Dependency injection" : "Dependency injection";
        }
        if (matches(ruleId, "SPR003", "SPR004", "SPR005", "SPR007", "SPR019", "SPR027", "SPR028", "SPR030", "SPR031", "SPR055")) {
            return language == ReportLanguage.ENGLISH ? "Architecture and boundaries" : "Architettura e confini";
        }
        if (matches(ruleId, "SPR012", "SPR043", "SPR044", "SPR045", "SPR052")) {
            return language == ReportLanguage.ENGLISH ? "Tests" : "Test";
        }
        if (matches(ruleId, "SPR001", "SPR015", "SPR016", "SPR021", "SPR022", "SPR032", "SPR033", "SPR036")) {
            return language == ReportLanguage.ENGLISH ? "Configuration and maintainability" : "Configurazione e manutenibilità";
        }
        if (matches(ruleId, "SPR064", "SPR065", "SPR066", "SPR067", "SPR068", "SPR069", "SPR070", "SPR071", "SPR072", "SPR073", "SPR074", "SPR075")) {
            return "Spring Alternative Advisor";
        }
        return language == ReportLanguage.ENGLISH ? "General" : "Generale";
    }

    private boolean matches(String ruleId, String... prefixes) {
        for (String prefix : prefixes) {
            if (ruleId.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean securityRule(String ruleId) {
        return matches(ruleId, "SPR037", "SPR039", "SPR040", "SPR041", "SPR042", "SPR046", "SPR058", "SPR059");
    }

    private String categoryExplanation(String category, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return switch (category) {
                case "Spring Security" -> "Checks on Spring Security, sensitive configuration, actuator endpoints, CSRF, CORS and password handling.";
                case "Web layer and REST contracts" -> "Quality of the REST boundary: DTOs, validation, versioning, HTTP semantics, OpenAPI and stable response contracts.";
                case "Runtime correctness" -> "Patterns that may generate wrong behavior, hidden errors or ineffective Spring proxies.";
                case "JPA, persistence and integrations" -> "Risks on databases and external integrations: transactions, entity design, fetch plans, repeated calls and manually created clients.";
                case "Dependency injection" -> "Constructor injection, field injection, immutable dependencies and Spring component wiring.";
                case "Architecture and boundaries" -> "Layering, dependency direction, DDD or hexagonal boundaries, class size and package structure.";
                case "Tests" -> "Test presence, assertion quality, excessive usage of heavy Spring tests and time-based fragility.";
                case "Configuration and maintainability" -> "Build, logging, naming, hardcoded values and general maintainability hygiene.";
                case "Spring Alternative Advisor" -> "Manual Java objects, low-level APIs and modern Spring alternatives worth evaluating.";
                default -> "General deterministic findings detected by Spring Guardian.";
            };
        }

        return switch (category) {
            case "Spring Security" -> "Controlli su Spring Security, configurazioni sensibili, endpoint actuator, CSRF, CORS e gestione password.";
            case "Layer web e contratti REST" -> "Qualità del bordo REST: DTO, validazione, versionamento, semantica HTTP, OpenAPI e contratti di risposta stabili.";
            case "Correttezza runtime" -> "Pattern che possono generare comportamenti errati, errori nascosti o proxy Spring inefficaci.";
            case "JPA, persistenza e integrazioni" -> "Rischi su database e integrazioni esterne: transazioni, entity design, fetch plan, chiamate ripetute e client creati a mano.";
            case "Dependency injection" -> "Constructor injection, field injection, dipendenze immutabili e cablaggio dei componenti Spring.";
            case "Architettura e confini" -> "Layering, direzione delle dipendenze, confini DDD o esagonali, dimensione classi e struttura package.";
            case "Test" -> "Presenza dei test, qualità degli assert, uso eccessivo di test Spring pesanti e fragilità temporale.";
            case "Configurazione e manutenibilità" -> "Build, logging, naming, valori hardcoded e igiene generale di manutenzione.";
            case "Spring Alternative Advisor" -> "Oggetti Java manuali, API di basso livello e alternative Spring moderne da valutare.";
            default -> "Problemi deterministici generali rilevati da Spring Guardian.";
        };
    }

    private record AreaDefinition(String code, String name, String description, List<String> prefixes) {
        boolean matches(String ruleId) {
            return prefixes.stream().anyMatch(ruleId::startsWith);
        }
    }
}
