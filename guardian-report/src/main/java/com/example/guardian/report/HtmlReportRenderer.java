package com.example.guardian.report;

import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.CategorySummary;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ModernizationChecklistItem;
import com.example.guardian.core.model.SpringArchitectureCycle;
import com.example.guardian.core.model.SpringMaturityAreaScore;
import com.example.guardian.core.model.SpringModuleDependency;
import com.example.guardian.core.model.SpringModuleSummary;
import com.example.guardian.core.model.UpgradeStep;
import com.example.guardian.core.model.Severity;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

/**
 * Self-contained HTML renderer for executive, architecture and remediation reports.
 *
 * @author p15518 - Simone Meneghetti
 */
public class HtmlReportRenderer implements ReportRenderer {

    private static final int MAX_COMPONENTS_IN_HTML_REPORT = 12;

    @Override
    public String render(ArchitectureReviewReport report) {
        StringBuilder html = new StringBuilder(64_000);
        html.append("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                .append("<title>Spring Guardian - ").append(escape(report.projectName())).append("</title>")
                .append("<style>").append(styles()).append("</style></head><body>");

        html.append("<aside class=\"sidebar\"><div class=\"brand\"><span class=\"shield\">SG</span><div>")
                .append("<strong>Spring Guardian</strong><small>Architecture Review</small></div></div>")
                .append("<nav>")
                .append("<a href=\"#overview\">Overview</a>")
                .append("<a href=\"#architect\">Architect Mode</a>")
                .append("<a href=\"#maturity\">Maturity Score</a>")
                .append("<a href=\"#architecture-map\">Architecture Map</a>")
                .append("<a href=\"#roadmap\">Modernization Roadmap</a>")
                .append("<a href=\"#gates\">Quality Gates</a>")
                .append("<a href=\"#areas\">Architecture Areas</a>")
                .append("<a href=\"#actions\">Actions</a>")
                .append("<a href=\"#findings\">Findings</a>")
                .append("<a href=\"#alternatives\">Spring Alternatives</a>")
                .append("</nav></aside>");

        html.append("<main class=\"content\">")
                .append("<section id=\"overview\" class=\"hero card\">")
                .append("<div><p class=\"eyebrow\">Spring Architecture Review Tool</p>")
                .append("<h1>").append(escape(report.projectName())).append("</h1>")
                .append("<p>").append(escape(report.summary().executiveSummary())).append("</p>")
                .append("<div class=\"meta\">")
                .append("<span>Scanned at: ").append(escape(String.valueOf(report.scannedAt()))).append("</span>")
                .append("<span>Root: ").append(escape(report.projectRootPath())).append("</span>")
                .append("<span>Profile: ").append(escape(String.valueOf(report.profile().projectType()))).append(" / ")
                .append(escape(String.valueOf(report.profile().architectureStyle()))).append(" / ")
                .append(escape(String.valueOf(report.profile().releaseTarget()))).append("</span>")
                .append("</div></div>")
                .append("<div class=\"score ").append(scoreTone(report.architectureScore())).append("\">")
                .append("<span>Architecture score</span><strong>").append(report.architectureScore()).append("</strong><em>/100</em>")
                .append("<small>").append(escape(report.riskLevel())).append("</small></div>")
                .append("</section>");

        html.append("<section class=\"grid metrics\">");
        metric(html, "Occurrences", String.valueOf(report.summary().totalFindings()), "Grouped by deterministic Spring rules");
        metric(html, "Critical", severity(report, Severity.CRITICAL), "Production blockers");
        metric(html, "Major", severity(report, Severity.MAJOR), "Fix before release");
        metric(html, "Minor", severity(report, Severity.MINOR), "Technical debt");
        metric(html, "Info", severity(report, Severity.INFO), "Advisory notes");
        metric(html, "Rules", String.valueOf(report.rulesExecuted()), "Executed checks");
        html.append("</section>");

        appendArchitectMode(html, report);

        html.append("<section id=\"gates\" class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">Governance</p><h2>Quality gates</h2></div>")
                .append("<span class=\"readiness ").append(css(report.releaseReadiness().status())).append("\">")
                .append(escape(report.releaseReadiness().label())).append("</span></div>")
                .append("<p>").append(escape(report.releaseReadiness().explanation())).append("</p>")
                .append("<div class=\"gate-grid\">");
        report.qualityGates().forEach(gate -> html.append("<article class=\"gate ").append(css(gate.status())).append("\">")
                .append("<span>").append(escape(gate.status())).append("</span>")
                .append("<h3>").append(escape(gate.name())).append("</h3>")
                .append("<p>").append(escape(gate.explanation())).append("</p>")
                .append("<small>").append(gate.failingFindings()).append(" failing finding(s)</small>")
                .append("</article>"));
        html.append("</div></section>");

        html.append("<section id=\"areas\" class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">Spring architecture</p><h2>Impacted areas</h2></div></div>")
                .append("<div class=\"area-grid\">");
        report.architectureAreas().forEach(area -> html.append("<article class=\"area ").append(css(area.readinessStatus())).append("\">")
                .append("<span>").append(escape(area.readinessStatus())).append("</span>")
                .append("<h3>").append(escape(area.name())).append("</h3>")
                .append("<p>").append(escape(area.description())).append("</p>")
                .append("<div class=\"chips\"><b>").append(area.findings()).append(" findings</b><b>")
                .append(area.criticalFindings()).append(" critical</b><b>").append(area.majorFindings()).append(" major</b></div>")
                .append("</article>"));
        html.append("</div></section>");

        html.append("<section class=\"grid two\">");
        html.append("<article class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">Types</p><h2>Finding types</h2></div></div>");
        summaryTable(html, report.findingsByType());
        html.append("</article>");
        html.append("<article class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">Categories</p><h2>Rule categories</h2></div></div>");
        summaryTable(html, report.findingsByCategory());
        html.append("</article></section>");

        html.append("<section id=\"actions\" class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">Remediation</p><h2>Recommended actions</h2></div>")
                .append("<span class=\"pill\">").append(report.recommendedActions().size()).append(" priorities</span></div>");
        if (report.recommendedActions().isEmpty()) {
            html.append("<p class=\"empty\">No prioritized actions generated.</p>");
        } else {
            html.append("<div class=\"actions\">");
            report.recommendedActions().forEach(action -> html.append("<article class=\"action\">")
                    .append("<strong>").append(action.priority()).append("</strong><div><span class=\"severity ")
                    .append(css(action.severity().name())).append("\">").append(action.severity()).append("</span> ")
                    .append("<code>").append(escape(action.ruleId())).append("</code><h3>").append(escape(action.title())).append("</h3>")
                    .append("<p>").append(escape(action.reason())).append("</p>")
                    .append("<p><b>Where:</b> ").append(escape(action.location())).append("</p>")
                    .append("<p><b>Fix:</b> ").append(escape(action.action())).append("</p></div></article>"));
            html.append("</div>");
        }
        html.append("</section>");

        appendFindings(html, report, false);
        appendFindings(html, report, true);

        html.append("</main></body></html>");
        return html.toString();
    }


    private void appendArchitectMode(StringBuilder html, ArchitectureReviewReport report) {
        if (report.architectMode() == null) {
            return;
        }

        var architect = report.architectMode();
        html.append("<section id=\"architect\" class=\"card architect-card\"><div class=\"section-title\"><div>")
                .append("<p class=\"eyebrow\">Spring Guardian Architect Mode</p><h2>From scan to modernization roadmap</h2></div>")
                .append("<span class=\"pill\">").append(escape(architect.fingerprint().buildTool())).append(" · ")
                .append(escape(architect.fingerprint().javaVersion())).append(" · Boot ")
                .append(escape(architect.fingerprint().springBootVersion())).append("</span></div>")
                .append("<p>").append(escape(architect.fingerprint().summary())).append("</p>")
                .append("<div class=\"architect-grid\">")
                .append("<article><span>Maturity</span><strong>").append(architect.maturityScore().overallScore()).append("/100</strong><small>")
                .append(escape(architect.maturityScore().status())).append("</small></article>")
                .append("<article><span>Modules</span><strong>").append(architect.architectureMap().modules().size()).append("</strong><small>logical packages</small></article>")
                .append("<article><span>Dependencies</span><strong>").append(architect.architectureMap().dependencies().size()).append("</strong><small>cross-module imports</small></article>")
                .append("<article><span>Cycles</span><strong>").append(architect.architectureMap().cycles().size()).append("</strong><small>basic detection</small></article>")
                .append("<article><span>Checklist</span><strong>").append(architect.modernizationPlan().checklist().size()).append("</strong><small>exportable actions</small></article>")
                .append("<article><span>Production</span><strong>").append(architect.productionReadiness().score()).append("/100</strong><small>")
                .append(escape(architect.productionReadiness().status())).append("</small></article>")
                .append("</div></section>");

        appendMaturity(html, architect.maturityScore().areas());
        appendArchitectureMap(html, architect.architectureMap().modules(), architect.architectureMap().dependencies(), architect.architectureMap().cycles(), architect.architectureMap().mermaidDiagram());
        appendRoadmap(html, architect.modernizationPlan().checklist(), architect.upgradePath().steps(), architect.openRewritePlan().yaml());
    }

    private void appendMaturity(StringBuilder html, java.util.List<SpringMaturityAreaScore> areas) {
        html.append("<section id=\"maturity\" class=\"card\"><div class=\"section-title\"><div>")
                .append("<p class=\"eyebrow\">Spring Maturity Score</p><h2>How Spring-native is this project?</h2></div></div>")
                .append("<div class=\"maturity-list\">");
        for (SpringMaturityAreaScore area : areas) {
            html.append("<article class=\"maturity-row ").append(css(area.status())).append("\">")
                    .append("<div><strong>").append(escape(area.name())).append("</strong><small>")
                    .append(escape(String.join(" · ", area.drivers()))).append("</small></div>")
                    .append("<div class=\"maturity-meter\"><span style=\"width:").append(area.score()).append("%\"></span></div>")
                    .append("<b>").append(area.score()).append("</b>")
                    .append("</article>");
        }
        html.append("</div></section>");
    }

    private void appendArchitectureMap(
            StringBuilder html,
            java.util.List<SpringModuleSummary> modules,
            java.util.List<SpringModuleDependency> dependencies,
            java.util.List<SpringArchitectureCycle> cycles,
            String mermaid
    ) {
        html.append("<section id=\"architecture-map\" class=\"card\"><div class=\"section-title\"><div>")
                .append("<p class=\"eyebrow\">Spring Architecture Map</p><h2>Current package/module shape</h2></div>")
                .append("<span class=\"pill\">Mermaid export ready</span></div>")
                .append("<div class=\"module-grid\">");
        for (SpringModuleSummary module : modules) {
            html.append("<article class=\"module-card\"><h3>").append(escape(module.name())).append("</h3>")
                    .append("<small>").append(escape(module.basePackage())).append("</small>")
                    .append("<div class=\"chips\"><b>").append(module.controllers()).append(" ctrl</b><b>")
                    .append(module.services()).append(" svc</b><b>").append(module.repositories()).append(" repo</b><b>")
                    .append(module.entities()).append(" entity</b><b>").append(module.clients()).append(" client</b></div>");
            if (!module.risks().isEmpty()) {
                html.append("<ul>");
                module.risks().forEach(risk -> html.append("<li>").append(escape(risk)).append("</li>"));
                html.append("</ul>");
            }
            html.append("</article>");
        }
        html.append("</div>");

        if (!dependencies.isEmpty()) {
            html.append("<h3>Cross-module dependencies</h3><div class=\"dependency-list\">");
            for (SpringModuleDependency dependency : dependencies) {
                html.append("<div><b>").append(escape(dependency.fromModule())).append(" → ")
                        .append(escape(dependency.toModule())).append("</b><span>")
                        .append(dependency.weight()).append(" import(s)</span><small>")
                        .append(escape(String.join(" · ", dependency.examples()))).append("</small></div>");
            }
            html.append("</div>");
        }

        if (!cycles.isEmpty()) {
            html.append("<h3>Cycle detection</h3><div class=\"cycle-list\">");
            for (SpringArchitectureCycle cycle : cycles) {
                html.append("<article><strong>").append(escape(String.join(" → ", cycle.modules()))).append("</strong><p>")
                        .append(escape(cycle.remediation())).append("</p></article>");
            }
            html.append("</div>");
        }

        html.append("<details><summary>Mermaid module graph</summary><pre><code>")
                .append(escape(mermaid))
                .append("</code></pre></details></section>");
    }

    private void appendRoadmap(StringBuilder html, java.util.List<ModernizationChecklistItem> checklist, java.util.List<UpgradeStep> upgradeSteps, String openRewriteYaml) {
        html.append("<section id=\"roadmap\" class=\"card\"><div class=\"section-title\"><div>")
                .append("<p class=\"eyebrow\">Modernization Plan</p><h2>Checklist di miglioramento esportabile</h2></div>")
                .append("<span class=\"pill\">").append(checklist.size()).append(" actions</span></div>");
        html.append("<div class=\"checklist\">");
        int displayed = 0;
        for (ModernizationChecklistItem item : checklist) {
            if (displayed >= 25) {
                break;
            }
            html.append("<label class=\"check-item\"><input type=\"checkbox\">")
                    .append("<span><b>").append(escape(item.title())).append("</b><small>")
                    .append("Priority ").append(item.priority()).append(" · ").append(item.severity()).append(" · effort ")
                    .append(item.effort()).append(" · impact ").append(item.businessImpact()).append("</small>")
                    .append("<em>").append(escape(item.suggestedChange())).append("</em>");
            if (!item.files().isEmpty()) {
                html.append("<code>").append(escape(String.join(", ", item.files()))).append("</code>");
            }
            html.append("</span></label>");
            displayed++;
        }
        if (checklist.size() > displayed) {
            html.append("<p class=\"empty\">").append(checklist.size() - displayed).append(" more checklist item(s) available in JSON/Markdown export.</p>");
        }
        html.append("</div>");

        html.append("<h3>Spring Upgrade Path</h3><div class=\"upgrade-list\">");
        for (UpgradeStep step : upgradeSteps) {
            html.append("<article><strong>").append(step.order()).append(". ").append(escape(step.title())).append("</strong>")
                    .append("<span>").append(escape(step.risk())).append("</span><p>").append(escape(step.description())).append("</p></article>");
        }
        html.append("</div><details><summary>OpenRewrite suggestions YAML</summary><pre><code>")
                .append(escape(openRewriteYaml))
                .append("</code></pre></details></section>");
    }


    private void appendFindings(StringBuilder html, ArchitectureReviewReport report, boolean alternativesOnly) {
        String id = alternativesOnly ? "alternatives" : "findings";
        String title = alternativesOnly ? "Spring Alternatives" : "Findings";
        String eyebrow = alternativesOnly ? "Modernization advisor" : "Evidence-first review";
        html.append("<section id=\"").append(id).append("\" class=\"card\"><div class=\"section-title\"><div><p class=\"eyebrow\">")
                .append(eyebrow).append("</p><h2>").append(title).append("</h2></div></div>");

        var findings = report.findings().stream()
                .filter(group -> alternativesOnly == isSpringAlternative(group))
                .sorted(Comparator.comparing(FindingGroup::severity)
                        .thenComparing(FindingGroup::ruleId))
                .toList();

        if (findings.isEmpty()) {
            html.append("<p class=\"empty\">No entries in this section.</p></section>");
            return;
        }

        html.append("<div class=\"finding-list\">");
        for (FindingGroup group : findings) {
            html.append("<article class=\"finding ").append(css(group.severity().name())).append("\">")
                    .append("<header><div><span class=\"severity ").append(css(group.severity().name())).append("\">")
                    .append(group.severity()).append("</span> <code>").append(escape(group.ruleId())).append("</code>")
                    .append("<h3>").append(escape(group.title())).append("</h3></div>")
                    .append("<span class=\"pill\">").append(group.occurrences()).append(" occurrence(s)</span></header>")
                    .append("<div class=\"guidance\">")
                    .append("<div><span>Detected</span><p>").append(escape(guidanceValue(group, "detected"))).append("</p></div>")
                    .append("<div><span>Impact</span><p>").append(escape(guidanceValue(group, "risk"))).append("</p></div>")
                    .append("<div><span>Remediation</span><p>").append(escape(guidanceValue(group, "fix"))).append("</p></div>")
                    .append("</div>");
            if (group.guidance() != null && group.guidance().springAlternative() != null && !group.guidance().springAlternative().isBlank()) {
                html.append("<p class=\"alternative\"><b>Spring alternative:</b> ").append(escape(group.guidance().springAlternative())).append("</p>");
            }
            if (group.guidance() != null && group.guidance().documentationUrl() != null && !group.guidance().documentationUrl().isBlank()) {
                html.append("<a class=\"doc\" href=\"").append(escape(group.guidance().documentationUrl())).append("\">Official documentation</a>");
            }
            html.append("<details open><summary>Affected components</summary><div class=\"components\">");
            int displayed = 0;
            for (AffectedComponent component : group.affectedComponents()) {
                if (displayed >= MAX_COMPONENTS_IN_HTML_REPORT) {
                    break;
                }
                html.append("<div class=\"component\"><b>").append(escape(component.name())).append("</b><span>")
                        .append(escape(component.type())).append(" · ").append(escape(component.filePath()));
                if (component.line() != null) {
                    html.append(":").append(component.line());
                }
                html.append("</span><p>").append(escape(component.evidence())).append("</p>");
                if (component.codeSnippet() != null && !component.codeSnippet().isBlank()) {
                    html.append("<pre><code>").append(escape(component.codeSnippet())).append("</code></pre>");
                }
                html.append("</div>");
                displayed++;
            }
            if (group.occurrences() > displayed) {
                html.append("<p class=\"empty\">").append(group.occurrences() - displayed).append(" more occurrence(s) available in JSON output.</p>");
            }
            html.append("</div></details></article>");
        }
        html.append("</div></section>");
    }

    private void metric(StringBuilder html, String label, String value, String detail) {
        html.append("<article class=\"metric\"><span>").append(escape(label)).append("</span><strong>")
                .append(escape(value)).append("</strong><small>").append(escape(detail)).append("</small></article>");
    }

    private void summaryTable(StringBuilder html, java.util.List<CategorySummary> rows) {
        if (rows.isEmpty()) {
            html.append("<p class=\"empty\">No data.</p>");
            return;
        }
        html.append("<table><thead><tr><th>Name</th><th>Total</th><th>Critical</th><th>Major</th></tr></thead><tbody>");
        rows.forEach(row -> html.append("<tr><td><b>").append(escape(row.category())).append("</b><small>")
                .append(escape(row.explanation())).append("</small></td><td>").append(row.findings())
                .append("</td><td>").append(row.criticalFindings()).append("</td><td>").append(row.majorFindings()).append("</td></tr>"));
        html.append("</tbody></table>");
    }

    private String severity(ArchitectureReviewReport report, Severity severity) {
        Map<Severity, Long> values = report.findingsBySeverity();
        return String.valueOf(values == null ? 0 : values.getOrDefault(severity, 0L));
    }

    private boolean isSpringAlternative(FindingGroup group) {
        String type = group.findingType() == null ? "" : group.findingType();
        String rule = group.ruleId() == null ? "" : group.ruleId();
        return type.equals("SPRING_ALTERNATIVE") || rule.startsWith("SPR_ALT") || rule.startsWith("ADV") || rule.matches("SPR0(6[4-9]|7[0-9]|8[0-6]|8[8-9]|90).*");
    }

    private String guidanceValue(FindingGroup group, String kind) {
        if (group.guidance() == null) {
            return switch (kind) {
                case "detected" -> group.explanation();
                case "risk" -> group.whyItMatters();
                default -> group.suggestedFix();
            };
        }
        return switch (kind) {
            case "detected" -> nonBlank(group.guidance().detectedProblem(), group.explanation());
            case "risk" -> nonBlank(group.guidance().riskImpact(), group.whyItMatters());
            default -> nonBlank(group.guidance().recommendedApproach(), group.suggestedFix());
        };
    }

    private String nonBlank(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private String scoreTone(int score) {
        if (score >= 85) {
            return "good";
        }
        if (score >= 65) {
            return "warning";
        }
        return "danger";
    }

    private String css(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "-");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String styles() {
        return """
                :root{color-scheme:dark;--bg:#07130f;--panel:#0d1f19;--panel2:#102922;--text:#ecfdf5;--muted:#a7b8b0;--brand:#34d399;--brand2:#86efac;--border:rgba(187,247,208,.16);--danger:#fb7185;--warning:#fbbf24;--info:#60a5fa}
                *{box-sizing:border-box}body{margin:0;background:radial-gradient(circle at top left,rgba(52,211,153,.18),transparent 35%),linear-gradient(135deg,#030712,#07130f 52%,#052e24);color:var(--text);font-family:Inter,ui-sans-serif,system-ui,-apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
                .sidebar{position:fixed;inset:0 auto 0 0;width:280px;padding:26px;border-right:1px solid var(--border);background:rgba(2,6,23,.55);backdrop-filter:blur(20px)}
                .brand{display:flex;gap:12px;align-items:center;margin-bottom:28px}.brand strong{display:block}.brand small,.eyebrow,.meta,.metric span,.metric small,td small,.component span{color:var(--muted)}.shield{display:grid;place-items:center;width:48px;height:48px;border-radius:16px;background:linear-gradient(135deg,var(--brand),#0f766e);color:#042015;font-weight:950}
                nav{display:grid;gap:8px}nav a{color:var(--text);text-decoration:none;padding:11px 12px;border-radius:14px}nav a:hover{background:rgba(52,211,153,.12)}
                .content{margin-left:280px;padding:32px;width:min(1540px,calc(100% - 280px))}.card,.metric{border:1px solid var(--border);background:linear-gradient(145deg,rgba(255,255,255,.08),rgba(255,255,255,.035));box-shadow:0 24px 80px rgba(0,0,0,.26);border-radius:28px;padding:24px;margin-bottom:18px}
                .hero{display:grid;grid-template-columns:1fr 220px;gap:24px;align-items:center}.eyebrow{text-transform:uppercase;font-weight:900;letter-spacing:.12em;font-size:12px}.hero h1{font-size:clamp(34px,5vw,70px);line-height:.95;margin:8px 0 14px}.hero p{color:var(--muted);font-size:18px;line-height:1.6}.meta{display:flex;flex-wrap:wrap;gap:8px;margin-top:18px}.meta span,.pill,.readiness{border:1px solid var(--border);border-radius:999px;padding:7px 10px;background:rgba(255,255,255,.045)}
                .score{border-radius:28px;padding:24px;text-align:center;background:rgba(0,0,0,.18);border:1px solid var(--border)}.score strong{display:block;font-size:76px;letter-spacing:-.08em}.score.good strong{color:var(--brand2)}.score.warning strong{color:var(--warning)}.score.danger strong{color:var(--danger)}.score small{display:block;color:var(--muted);font-weight:800}
                .grid{display:grid;gap:18px}.metrics{grid-template-columns:repeat(6,minmax(0,1fr))}.two{grid-template-columns:1fr 1fr}.metric{margin:0}.metric strong{display:block;font-size:34px;margin:8px 0}.section-title{display:flex;align-items:center;justify-content:space-between;gap:16px;margin-bottom:12px}.section-title h2{margin:0;font-size:30px}
                .gate-grid,.area-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px}.gate,.area,.action,.component{border:1px solid var(--border);border-radius:22px;padding:18px;background:rgba(0,0,0,.16)}.gate span,.area span,.severity{display:inline-flex;border-radius:999px;padding:5px 9px;font-size:12px;font-weight:900;margin-bottom:8px}.fail,.not_ready,.blocked,.critical{border-color:rgba(251,113,133,.45)!important;color:#fecdd3}.warning,.ready_with_warnings,.attention,.major{border-color:rgba(251,191,36,.38)!important;color:#fde68a}.pass,.ready,.ok,.minor{border-color:rgba(52,211,153,.30)!important;color:#bbf7d0}.info{color:#bfdbfe}
                .chips{display:flex;gap:8px;flex-wrap:wrap}.chips b{border-radius:999px;padding:6px 9px;background:rgba(255,255,255,.06)}table{width:100%;border-collapse:collapse}th,td{text-align:left;border-bottom:1px solid var(--border);padding:12px;vertical-align:top}td small{display:block;margin-top:6px;line-height:1.45}
                .actions,.finding-list,.components{display:grid;gap:14px}.action{display:grid;grid-template-columns:54px 1fr;gap:16px}.action>strong{display:grid;place-items:center;width:54px;height:54px;border-radius:18px;background:rgba(52,211,153,.14);color:#bbf7d0;font-size:22px}
                .finding{border:1px solid var(--border);border-radius:26px;padding:20px;background:rgba(0,0,0,.18)}.finding header{display:flex;justify-content:space-between;gap:12px;align-items:flex-start}.finding h3{font-size:24px;margin:8px 0 0}code{color:#bbf7d0}.guidance{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:12px;margin:16px 0}.guidance div{border:1px solid var(--border);border-radius:18px;padding:14px;background:rgba(255,255,255,.04)}.guidance span{color:#bbf7d0;text-transform:uppercase;font-size:12px;font-weight:900;letter-spacing:.08em}.guidance p,.finding p,.gate p,.area p{color:var(--muted);line-height:1.58}.alternative{border-left:4px solid var(--brand);padding-left:12px}.doc{display:inline-flex;margin:4px 0 14px;color:#bbf7d0;text-decoration:none;font-weight:900}.component b,.component span{display:block}.component p{margin-bottom:0}pre{white-space:pre-wrap;overflow:auto;border-radius:16px;padding:14px;background:#020617;border:1px solid rgba(148,163,184,.22);color:#dbeafe}.empty{color:var(--muted);border:1px dashed var(--border);border-radius:18px;padding:18px;background:rgba(255,255,255,.03)}
                .architect-grid{display:grid;grid-template-columns:repeat(6,minmax(0,1fr));gap:12px}.architect-grid article{border:1px solid var(--border);border-radius:20px;padding:16px;background:rgba(52,211,153,.06)}.architect-grid span,.architect-grid small{display:block;color:var(--muted)}.architect-grid strong{display:block;font-size:30px;margin:6px 0}.maturity-list,.dependency-list,.cycle-list,.checklist,.upgrade-list{display:grid;gap:12px}.maturity-row{display:grid;grid-template-columns:230px 1fr 58px;gap:14px;align-items:center;border:1px solid var(--border);border-radius:18px;padding:14px;background:rgba(0,0,0,.13)}.maturity-row small{display:block;color:var(--muted);margin-top:5px}.maturity-meter{height:10px;border-radius:999px;background:rgba(255,255,255,.1);overflow:hidden}.maturity-meter span{display:block;height:100%;border-radius:999px;background:linear-gradient(90deg,var(--brand),var(--brand2))}.module-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px}.module-card{border:1px solid var(--border);border-radius:22px;padding:18px;background:rgba(0,0,0,.16)}.module-card ul{color:var(--warning);line-height:1.5}.dependency-list>div,.cycle-list article,.upgrade-list article{border:1px solid var(--border);border-radius:18px;padding:14px;background:rgba(255,255,255,.04)}.dependency-list span,.dependency-list small,.upgrade-list span{display:block;color:var(--muted);margin-top:5px}.check-item{display:grid;grid-template-columns:26px 1fr;gap:12px;align-items:start;border:1px solid var(--border);border-radius:18px;padding:14px;background:rgba(255,255,255,.045)}.check-item input{width:18px;height:18px;accent-color:var(--brand)}.check-item b,.check-item small,.check-item em,.check-item code{display:block}.check-item small{color:var(--muted);margin:5px 0}.check-item em{font-style:normal;color:#d1fae5}.check-item code{margin-top:8px}
                @media(max-width:1180px){.sidebar{position:static;width:auto}.content{margin:0;width:100%;padding:18px}.hero,.metrics,.two,.gate-grid,.area-grid,.guidance{grid-template-columns:1fr}}
                """;
    }
}
