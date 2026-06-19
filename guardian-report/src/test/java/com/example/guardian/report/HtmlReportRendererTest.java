package com.example.guardian.report;

import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.ArchitectureAreaReport;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.CategorySummary;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.QualityGate;
import com.example.guardian.core.model.RecommendedAction;
import com.example.guardian.core.model.ReleaseReadiness;
import com.example.guardian.core.model.ReportExplanation;
import com.example.guardian.core.model.ReportSummary;
import com.example.guardian.core.model.RuleGuidance;
import com.example.guardian.core.model.Severity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlReportRendererTest {

    @Test
    void rendersSelfContainedHtmlReportWithSpringAlternatives() {
        String html = new HtmlReportRenderer().render(report());

        assertTrue(html.startsWith("<!doctype html>"));
        assertTrue(html.contains("Spring Guardian"));
        assertTrue(html.contains("Quality gates"));
        assertTrue(html.contains("Spring Alternatives"));
        assertTrue(html.contains("https://docs.spring.io/spring-boot/reference/features/external-config.html"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    private ArchitectureReviewReport report() {
        Map<Severity, Long> severity = new LinkedHashMap<>();
        severity.put(Severity.CRITICAL, 0L);
        severity.put(Severity.MAJOR, 1L);
        severity.put(Severity.MINOR, 0L);
        severity.put(Severity.INFO, 1L);

        FindingGroup finding = new FindingGroup(
                "SPR002_FIELD_INJECTION",
                Severity.MAJOR,
                "Dependency injection",
                "DEPENDENCY_INJECTION",
                "Dependency injection",
                "Field injection detected",
                1,
                List.of(new AffectedComponent("JAVA_CLASS", "DemoService", "src/main/java/DemoService.java", 7, "@Autowired private Repo repo;", "<script>bad</script>")),
                "Field injection hides dependencies.",
                "Use constructor injection.",
                "Field injection detected in a Spring component.",
                new RuleGuidance("A field is injected with @Autowired.", "Dependencies are hidden.", "Use constructor injection.", "Constructor injection", "https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html", "", "")
        );

        FindingGroup advisor = new FindingGroup(
                "ADV013_SCATTERED_VALUE",
                Severity.INFO,
                "Spring Alternative Advisor",
                "SPRING_ALTERNATIVE",
                "Spring Alternative Advisor",
                "@Value used for application configuration",
                1,
                List.of(new AffectedComponent("JAVA_CLASS", "Client", "src/main/java/Client.java", 10, "@Value", "@Value(\"${api.url}\")")),
                "Scattered values are hard to validate.",
                "Use @ConfigurationProperties.",
                "Configuration should be strongly typed.",
                new RuleGuidance("@Value was detected.", "Configuration is harder to document and validate.", "Group settings with @ConfigurationProperties.", "@ConfigurationProperties", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "", "")
        );

        return new ArchitectureReviewReport(
                "demo",
                Instant.parse("2026-01-01T00:00:00Z"),
                "/tmp/demo",
                ProjectProfile.defaults(),
                new ProjectCapabilities(true, false, false, true, true, true, false, false, true, true, true, false, false, false, List.of("LAYERED")),
                new ReportSummary(2, 0, 1, 0, 1, "MEDIUM", "ACTION_REQUIRED", "Fix important Spring architecture findings."),
                new ReleaseReadiness("READY_WITH_WARNINGS", "Ready with warnings", "Release possible after review.", true, List.of(), List.of("One major finding.")),
                78,
                "MEDIUM",
                5,
                1,
                80,
                severity,
                List.of(new CategorySummary("Dependency injection", 1, 0, 1, 0, 0, "DI issues")),
                List.of(new CategorySummary("Spring Alternative Advisor", 1, 0, 0, 0, 1, "Advisor issues")),
                List.of(new ArchitectureAreaReport("DEPENDENCY_INJECTION", "Dependency injection", "Bean wiring and boundaries", 1, 0, 1, "ATTENTION")),
                List.of(new QualityGate("GATE_DI", "Dependency injection", "WARNING", "Constructor injection preferred.", true, 1)),
                List.of(new RecommendedAction(1, Severity.MAJOR, "SPR002_FIELD_INJECTION", "Use constructor injection", "DemoService:7", "Mandatory dependencies are hidden.", "Refactor to constructor injection.")),
                new ReportExplanation("Score uses deterministic penalties.", "Severity maps to release impact.", "Start from blockers.", List.of("Use JSON for automation.")),
                List.of(finding, advisor)
        );
    }
}
