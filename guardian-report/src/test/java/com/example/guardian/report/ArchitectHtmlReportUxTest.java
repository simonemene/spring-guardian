package com.example.guardian.report;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that Architect Mode HTML uses readable UX components instead of raw browser widgets.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectHtmlReportUxTest {

    @TempDir
    Path tempDir;

    @Test
    void architectSectionsRenderStyledGraphAndCodePanels() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/order/service"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/payment/service"));

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/order/service/OrderService.java"), """
                package com.acme.order.service;

                import com.acme.payment.service.PaymentService;
                import org.springframework.stereotype.Service;

                @Service
                class OrderService {
                    private PaymentService paymentService;
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/payment/service/PaymentService.java"), """
                package com.acme.payment.service;

                import org.springframework.stereotype.Service;

                @Service
                class PaymentService {
                }
                """);

        String html = new HtmlReportRenderer().render(new ProjectScanService(GuardianSettings.defaults()).scan(tempDir));

        assertTrue(html.contains("graph-board"));
        assertTrue(html.contains("code-panel"));
        assertTrue(html.contains("OpenRewrite suggestions YAML"));
        assertTrue(html.contains("<header><strong>"));
        assertFalse(html.contains("<details><summary>Mermaid module graph"));
        assertFalse(html.contains("<details><summary>OpenRewrite suggestions YAML"));
    }
}
