package com.example.guardian.core;

import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that Architect Mode maps enterprise package names to useful logical modules.
 *
 * @author p15518 - Simone Meneghetti
 */
class ArchitectModeArchitectureMapPrecisionTest {

    @TempDir
    Path tempDir;

    @Test
    void mapsEnterpriseWebPackagesToBusinessModulesAndDetectsCycles() throws Exception {
        Files.createDirectories(tempDir.resolve("src/main/java/com/gruppoveronesi/web/dedalo/service"));
        Files.createDirectories(tempDir.resolve("src/main/java/com/gruppoveronesi/web/billing/service"));

        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <dependencies>
                        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
                    </dependencies>
                </project>
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/gruppoveronesi/web/dedalo/service/DedaloService.java"), """
                package com.gruppoveronesi.web.dedalo.service;

                import com.gruppoveronesi.web.billing.service.BillingService;
                import org.springframework.stereotype.Service;

                @Service
                class DedaloService {
                    private BillingService billingService;
                }
                """);

        Files.writeString(tempDir.resolve("src/main/java/com/gruppoveronesi/web/billing/service/BillingService.java"), """
                package com.gruppoveronesi.web.billing.service;

                import com.gruppoveronesi.web.dedalo.service.DedaloService;
                import org.springframework.stereotype.Service;

                @Service
                class BillingService {
                    private DedaloService dedaloService;
                }
                """);

        var report = new ProjectScanService(GuardianSettings.defaults()).scan(tempDir);
        var map = report.architectMode().architectureMap();

        Set<String> modules = map.modules().stream()
                .map(module -> module.name())
                .collect(Collectors.toSet());

        assertTrue(modules.contains("dedalo"), "Expected business module dedalo but got " + modules);
        assertTrue(modules.contains("billing"), "Expected business module billing but got " + modules);
        assertFalse(modules.contains("gruppoveronesi"), "Company package must not be used as the logical module name");
        assertTrue(map.dependencies().stream().anyMatch(dependency -> dependency.fromModule().equals("dedalo") && dependency.toModule().equals("billing")));
        assertTrue(map.dependencies().stream().anyMatch(dependency -> dependency.fromModule().equals("billing") && dependency.toModule().equals("dedalo")));
        assertFalse(map.cycles().isEmpty(), "Expected a package/module cycle to be detected");
        assertTrue(map.mermaidDiagram().contains("dedalo"));
        assertTrue(map.mermaidDiagram().contains("billing"));
    }
}
