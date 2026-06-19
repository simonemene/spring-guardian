package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the first deterministic Architect Mode baseline.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectModePlannerTest {

    @TempDir
    Path tempDir;

    @Test
    void buildsMaturityArchitectureMapChecklistAndExports() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/order/controller"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/order/repository"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/payment/service"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/acme/invoice/service"));
        Files.createDirectories(tempDir.resolve("src/main/resources"));

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <properties>
                        <java.version>11</java.version>
                        <spring-boot.version>2.7.18</spring-boot.version>
                    </properties>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("src/main/resources/application.properties"), "spring.jpa.open-in-view=true\n");

        Files.writeString(tempDir.resolve("src/main/java/com/acme/order/controller/OrderController.java"), """
                package com.acme.order.controller;

                import com.acme.order.repository.OrderRepository;
                import org.springframework.web.bind.annotation.RestController;

                @RestController
                class OrderController {
                    private final OrderRepository repository;

                    OrderController(OrderRepository repository) {
                        this.repository = repository;
                    }
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/order/repository/OrderRepository.java"), """
                package com.acme.order.repository;

                interface OrderRepository {
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/payment/service/PaymentService.java"), """
                package com.acme.payment.service;

                import com.acme.invoice.service.InvoiceService;

                class PaymentService {
                    private InvoiceService invoiceService;
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/acme/invoice/service/InvoiceService.java"), """
                package com.acme.invoice.service;

                import com.acme.payment.service.PaymentService;

                class InvoiceService {
                    private PaymentService paymentService;
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        var architect = report.architectMode();

        assertNotNull(architect);
        assertTrue(architect.maturityScore().overallScore() >= 0);
        assertFalse(architect.architectureMap().modules().isEmpty());
        assertFalse(architect.modernizationPlan().checklist().isEmpty());
        assertTrue(architect.architectureMap().mermaidDiagram().startsWith("flowchart LR"));
        assertTrue(architect.openRewritePlan().yaml().contains("recipeList"));
        assertFalse(architect.upgradePath().steps().isEmpty());
        assertTrue(architect.architectureMap().cycles().stream()
                .anyMatch(cycle -> String.join("->", cycle.modules()).contains("payment")
                        && String.join("->", cycle.modules()).contains("invoice")));
    }
}
