package com.example.guardian.server.controller;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.server.service.ZipWorkspaceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scans")
public class ScanController {

    private final ProjectScanService projectScanService;
    private final ZipWorkspaceService zipWorkspaceService;

    public ScanController(ProjectScanService projectScanService, ZipWorkspaceService zipWorkspaceService) {
        this.projectScanService = projectScanService;
        this.zipWorkspaceService = zipWorkspaceService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArchitectureReviewReport uploadAndScan(@RequestPart("file") MultipartFile file) {
        Path workspace = zipWorkspaceService.extractZip(file);
        return projectScanService.scan(workspace);
    }

    @PostMapping("/local")
    public ArchitectureReviewReport scanLocalPath(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Missing field: path");
        }
        return projectScanService.scan(Path.of(path));
    }
}
