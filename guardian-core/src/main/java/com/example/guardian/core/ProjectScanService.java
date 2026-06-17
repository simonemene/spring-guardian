package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.CategorySummary;
import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.RecommendedAction;
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
        return scan(root, ReportLanguage.ITALIAN);
    }

    /**
     * Scans a project and returns the report in the selected language.
     *
     * @param root project root
     * @param language report language
     * @return architecture review report
     */
    public ArchitectureReviewReport scan(Path root, ReportLanguage language) {
        ProjectScanContext context = scanner.scan(root);
        ReportLanguage resolvedLanguage = language == null ? ReportLanguage.ITALIAN : language;

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

        int score = calculateScore(findings);
        String riskLevel = calculateRiskLevel(score, bySeverity);
        List<FindingGroup> groupedFindings = buildFindingGroups(findings, resolvedLanguage);

        return new ArchitectureReviewReport(
                root.getFileName() == null ? root.toString() : root.getFileName().toString(),
                Instant.now(),
                buildSummary(findings, bySeverity, riskLevel, resolvedLanguage),
                score,
                riskLevel,
                context.javaFiles().size(),
                context.pomFiles().size(),
                rules.size(),
                bySeverity,
                buildCategorySummaries(findings, resolvedLanguage),
                buildRecommendedActions(groupedFindings, resolvedLanguage),
                buildExplanation(resolvedLanguage),
                groupedFindings
        );
    }

    private int calculateScore(List<Finding> findings) {
        int penalty = 0;
        for (Finding finding : findings) {
            penalty += switch (finding.severity()) {
                case CRITICAL -> 12;
                case MAJOR -> 6;
                case MINOR -> 2;
                case INFO -> 1;
            };
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

    private ReportSummary buildSummary(List<Finding> findings, Map<Severity, Long> bySeverity, String riskLevel, ReportLanguage language) {
        long critical = bySeverity.getOrDefault(Severity.CRITICAL, 0L);
        long major = bySeverity.getOrDefault(Severity.MAJOR, 0L);
        long minor = bySeverity.getOrDefault(Severity.MINOR, 0L);
        long info = bySeverity.getOrDefault(Severity.INFO, 0L);

        String status = critical > 0 ? "ACTION_REQUIRED"
                : major > 0 ? "IMPROVEMENT_REQUIRED"
                : findings.isEmpty() ? "NO_FINDINGS"
                : "REVIEW_RECOMMENDED";

        String summary = language == ReportLanguage.ENGLISH
                ? englishSummaryFor(riskLevel)
                : italianSummaryFor(riskLevel);

        return new ReportSummary(findings.size(), critical, major, minor, info, riskLevel, status, summary);
    }

    private String italianSummaryFor(String riskLevel) {
        return switch (riskLevel) {
            case "HIGH" -> "Il progetto contiene problemi architetturali, di sicurezza o correttezza da gestire prima di considerarlo pronto per produzione o pipeline CI.";
            case "MEDIUM" -> "Il progetto è utilizzabile, ma ci sono diversi interventi di manutenzione, qualità Spring o hardening da pianificare.";
            case "LOW" -> "Il progetto ha una base generalmente accettabile, con miglioramenti minori consigliati.";
            default -> "Le regole deterministiche abilitate non hanno rilevato rischi Spring rilevanti.";
        };
    }

    private String englishSummaryFor(String riskLevel) {
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
                .limit(10)
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
                    "The score starts from 100 and applies weighted penalties based on severity. Critical findings have the strongest impact.",
                    "Critical means a potential production, security or architecture blocker. High means a concrete maintenance or correctness risk. Medium means a recommended improvement. Info is low-impact guidance.",
                    "Start from recommended actions, fix critical findings first and then high-severity findings by technical area. Findings are grouped by rule to keep the report readable on large projects.",
                    List.of(
                            "Fix every critical finding before considering the CI scan green.",
                            "Plan high-severity findings during refactoring, hardening or technical improvement sprints.",
                            "Use the category summary to understand whether the project is weaker in security, APIs, persistence, tests or maintainability.",
                            "Keep deterministic rules as the source of truth; any AI explanation should remain an optional layer."
                    )
            );
        }

        return new ReportExplanation(
                "Il punteggio parte da 100 e applica penalità pesate in base alla severità. I problemi critici hanno il peso maggiore.",
                "Critico indica un possibile blocco per produzione, sicurezza o architettura. Alto indica un rischio concreto di manutenzione o correttezza. Medio indica un miglioramento consigliato. Info è un suggerimento a basso impatto.",
                "Parti dalle azioni consigliate, correggi prima i problemi critici e poi quelli alti raggruppandoli per area tecnica. I problemi sono raggruppati per regola per mantenere leggibile il report anche su progetti grandi.",
                List.of(
                        "Correggi tutti i problemi critici prima di considerare verde la scansione in CI.",
                        "Pianifica i problemi alti durante refactor, hardening o sprint tecnici.",
                        "Usa il riepilogo per categoria per capire se il progetto è più debole su sicurezza, API, persistenza, test o manutenibilità.",
                        "Mantieni le regole deterministiche come fonte di verità; eventuali spiegazioni AI devono restare uno strato opzionale."
                )
        );
    }

    private String categoryFor(Finding finding, ReportLanguage language) {
        String ruleId = finding.ruleId();

        if (matches(ruleId, "SPR038", "SPR039", "SPR040", "SPR041", "SPR042", "SPR046")) {
            return language == ReportLanguage.ENGLISH ? "Security" : "Sicurezza";
        }
        if (matches(ruleId, "SPR006", "SPR010", "SPR013", "SPR014", "SPR023", "SPR024", "SPR050", "SPR051")) {
            return language == ReportLanguage.ENGLISH ? "REST API and contracts" : "API e contratti REST";
        }
        if (matches(ruleId, "SPR008", "SPR011", "SPR017", "SPR018", "SPR020", "SPR025", "SPR047")) {
            return language == ReportLanguage.ENGLISH ? "Runtime correctness" : "Correttezza runtime";
        }
        if (matches(ruleId, "SPR009", "SPR026", "SPR048", "SPR049")) {
            return language == ReportLanguage.ENGLISH ? "Persistence and integrations" : "Persistenza e integrazioni";
        }
        if (matches(ruleId, "SPR002", "SPR003", "SPR004", "SPR005", "SPR007", "SPR019", "SPR027", "SPR028", "SPR029", "SPR030", "SPR031")) {
            return language == ReportLanguage.ENGLISH ? "Architecture" : "Architettura";
        }
        if (matches(ruleId, "SPR012", "SPR043", "SPR044", "SPR045", "SPR052")) {
            return language == ReportLanguage.ENGLISH ? "Tests" : "Test";
        }
        if (matches(ruleId, "SPR001", "SPR015", "SPR016", "SPR021", "SPR022", "SPR032", "SPR033", "SPR036", "SPR037")) {
            return language == ReportLanguage.ENGLISH ? "Configuration and maintainability" : "Configurazione e manutenibilità";
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

    private String categoryExplanation(String category, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH) {
            return switch (category) {
                case "Security" -> "Checks on Spring Security, sensitive configuration, actuator endpoints, CSRF, CORS and password handling.";
                case "REST API and contracts" -> "Quality of the REST boundary: DTOs, validation, versioning, HTTP semantics and stable response contracts.";
                case "Runtime correctness" -> "Patterns that may generate wrong behavior, hidden errors or ineffective Spring proxies.";
                case "Persistence and integrations" -> "Risks on databases and external integrations: manual resources, eager fetching, repeated calls and manually created clients.";
                case "Architecture" -> "Layering, dependency direction, class size, fat controllers and boundaries between Spring components.";
                case "Tests" -> "Test presence, assertion quality, excessive usage of heavy Spring tests and time-based fragility.";
                case "Configuration and maintainability" -> "Build, logging, naming, hardcoded values and general maintainability hygiene.";
                default -> "General deterministic findings detected by Spring Guardian.";
            };
        }

        return switch (category) {
            case "Sicurezza" -> "Controlli su Spring Security, configurazioni sensibili, endpoint actuator, CSRF, CORS e gestione password.";
            case "API e contratti REST" -> "Qualità del bordo REST: DTO, validazione, versionamento, semantica HTTP e contratti di risposta stabili.";
            case "Correttezza runtime" -> "Pattern che possono generare comportamenti errati, errori nascosti o proxy Spring inefficaci.";
            case "Persistenza e integrazioni" -> "Rischi su database e integrazioni esterne: risorse manuali, fetch eager, chiamate ripetute e client creati a mano.";
            case "Architettura" -> "Layering, direzione delle dipendenze, dimensione delle classi, controller troppo ricchi e confini tra componenti Spring.";
            case "Test" -> "Presenza dei test, qualità degli assert, uso eccessivo di test Spring pesanti e fragilità temporale.";
            case "Configurazione e manutenibilità" -> "Build, logging, naming, valori hardcoded e igiene generale di manutenzione.";
            default -> "Problemi deterministici generali rilevati da Spring Guardian.";
        };
    }
}
