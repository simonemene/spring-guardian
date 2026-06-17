package com.example.guardian.server.controller;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.ProjectCapabilities;
import com.example.guardian.core.model.ProjectProfile;
import com.example.guardian.core.model.QualityGate;
import com.example.guardian.core.model.ReleaseReadiness;
import com.example.guardian.core.model.ReportExplanation;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.ReportSummary;
import com.example.guardian.core.model.Severity;
import com.example.guardian.server.exception.ApiExceptionHandler;
import com.example.guardian.server.service.ZipWorkspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScanController.class)
@Import(ApiExceptionHandler.class)
class ScanControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProjectScanService projectScanService;

    @MockBean
    ZipWorkspaceService zipWorkspaceService;

    @TempDir
    Path tempDir;

    @Test
    void scanLocalPathPassesSelectedLanguageAndProfileToCore() throws Exception {
        when(projectScanService.scan(any(Path.class), eq(ReportLanguage.ENGLISH), any(ProjectProfile.class))).thenReturn(emptyReport());

        mockMvc.perform(post("/api/v1/scans/local?language=en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"" + tempDir.toString().replace("\\", "\\\\") + "\",\"architectureStyle\":\"DOMAIN_DRIVEN_DESIGN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("demo"));

        verify(projectScanService).scan(any(Path.class), eq(ReportLanguage.ENGLISH), any(ProjectProfile.class));
    }

    @Test
    void scanLocalPathReturnsBadRequestWhenPathIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/scans/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Richiesta non valida"));
    }

    private ArchitectureReviewReport emptyReport() {
        Map<Severity, Long> severity = new LinkedHashMap<>();
        severity.put(Severity.CRITICAL, 0L);
        severity.put(Severity.MAJOR, 0L);
        severity.put(Severity.MINOR, 0L);
        severity.put(Severity.INFO, 0L);
        return new ArchitectureReviewReport(
                "demo",
                Instant.parse("2026-01-01T00:00:00Z"),
                ProjectProfile.defaults(),
                new ProjectCapabilities(false, false, false, false, false, false, false, false, false, false, false, false, false, false, List.of("UNCLASSIFIED")),
                new ReportSummary(0, 0, 0, 0, 0, "HEALTHY", "NO_FINDINGS", "ok"),
                new ReleaseReadiness("READY", "Ready", "ok", true, List.of(), List.of()),
                100,
                "HEALTHY",
                0,
                0,
                0,
                severity,
                List.of(),
                List.of(),
                List.of(),
                List.of(new QualityGate("GATE_TEST", "Test", "PASS", "ok", true, 0)),
                List.of(),
                new ReportExplanation("score", "severity", "usage", List.of()),
                List.of()
        );
    }
}
