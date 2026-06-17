package com.example.guardian.server.controller;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.model.ArchitectureReviewReport;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.server.dto.LocalScanRequest;
import com.example.guardian.server.service.ZipWorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * REST controller that exposes project scan operations.
 *
 * @author Simone Meneghetti
 */
@RestController
@RequestMapping("/api/v1/scans")
@Tag(name = "Project scans", description = "Operations used to scan Spring projects from ZIP files, browser folders or backend paths.")
public class ScanController {

    private final ProjectScanService projectScanService;
    private final ZipWorkspaceService zipWorkspaceService;

    /**
     * Creates the scan controller.
     *
     * @param projectScanService service that executes architecture rules
     * @param zipWorkspaceService service that prepares uploaded workspaces
     */
    public ScanController(ProjectScanService projectScanService, ZipWorkspaceService zipWorkspaceService) {
        this.projectScanService = projectScanService;
        this.zipWorkspaceService = zipWorkspaceService;
    }

    /**
     * Scans a project uploaded as ZIP archive.
     *
     * @param file ZIP archive containing the project
     * @param language report language
     * @return architecture review report
     */
    @Operation(summary = "Scan an uploaded ZIP project", description = "Extracts a ZIP archive into a temporary workspace and scans it with Spring Guardian rules.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project scanned successfully", content = @Content(schema = @Schema(implementation = ArchitectureReviewReport.class))),
            @ApiResponse(responseCode = "400", description = "Invalid upload or request"),
            @ApiResponse(responseCode = "500", description = "Unexpected scan error")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArchitectureReviewReport uploadZipAndScan(
            @Parameter(description = "ZIP archive containing the project to scan.", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Report language. Accepted values: it, en.", example = "it")
            @RequestParam(defaultValue = "it") String language) {
        Path workspace = zipWorkspaceService.extractZip(file);
        return projectScanService.scan(workspace, ReportLanguage.from(language));
    }

    /**
     * Scans a project folder uploaded by the browser.
     *
     * @param files files selected from the project root folder
     * @param language report language
     * @return architecture review report
     */
    @Operation(summary = "Scan an uploaded browser folder", description = "Copies all files selected from a browser folder upload and scans the resulting workspace.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project scanned successfully", content = @Content(schema = @Schema(implementation = ArchitectureReviewReport.class))),
            @ApiResponse(responseCode = "400", description = "Invalid upload or request"),
            @ApiResponse(responseCode = "500", description = "Unexpected scan error")
    })
    @PostMapping(value = "/upload-folder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArchitectureReviewReport uploadFolderAndScan(
            @Parameter(description = "Files selected from the project root folder.", required = true)
            @RequestPart("files") List<MultipartFile> files,
            @Parameter(description = "Report language. Accepted values: it, en.", example = "it")
            @RequestParam(defaultValue = "it") String language) {
        Path workspace = zipWorkspaceService.copyUploadedFolder(files);
        return projectScanService.scan(workspace, ReportLanguage.from(language));
    }

    /**
     * Scans a project path visible to the backend process.
     *
     * @param request request containing the backend path
     * @param language report language
     * @return architecture review report
     */
    @Operation(summary = "Scan a backend-visible path", description = "Scans a project directory already available on the backend filesystem or mounted Docker volume.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project scanned successfully", content = @Content(schema = @Schema(implementation = ArchitectureReviewReport.class))),
            @ApiResponse(responseCode = "400", description = "Invalid path"),
            @ApiResponse(responseCode = "500", description = "Unexpected scan error")
    })
    @PostMapping("/local")
    public ArchitectureReviewReport scanLocalPath(
            @RequestBody LocalScanRequest request,
            @Parameter(description = "Report language. Accepted values: it, en.", example = "it")
            @RequestParam(defaultValue = "it") String language) {
        String path = request == null ? null : request.path();
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Campo obbligatorio mancante: path.");
        }

        Path root = Path.of(path).normalize();
        if (!Files.exists(root)) {
            throw new IllegalArgumentException("La cartella indicata non esiste: " + root);
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Il path indicato non è una cartella: " + root);
        }

        return projectScanService.scan(root, ReportLanguage.from(language));
    }
}
