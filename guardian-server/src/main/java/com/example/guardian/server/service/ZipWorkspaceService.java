package com.example.guardian.server.service;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Prepares temporary workspaces from uploaded ZIP archives or browser folder uploads.
 *
 * @author Simone Meneghetti
 */
@Service
public class ZipWorkspaceService {

    /**
     * Extracts a ZIP archive into a safe temporary workspace.
     *
     * @param file uploaded ZIP file
     * @return workspace root
     */
    public Path extractZip(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file ZIP caricato è vuoto.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("Sono supportati solo file .zip.");
        }

        Path workspace = newWorkspace();

        try {
            Files.createDirectories(workspace);
            try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(file.getInputStream()))) {
                ZipArchiveEntry entry;
                while ((entry = zip.getNextZipEntry()) != null) {
                    Path target = safeResolve(workspace, entry.getName());

                    if (entry.isDirectory()) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(zip, target);
                    }
                }
            }
            return resolveProjectRoot(workspace);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile estrarre il file ZIP.", e);
        }
    }

    /**
     * Copies files selected from a browser folder upload into a safe temporary workspace.
     *
     * @param files uploaded project files
     * @return workspace root
     */
    public Path copyUploadedFolder(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("La cartella caricata non contiene file.");
        }

        Path workspace = newWorkspace();

        try {
            Files.createDirectories(workspace);
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                String originalName = file.getOriginalFilename();
                if (originalName == null || originalName.isBlank()) {
                    continue;
                }

                Path target = safeResolve(workspace, originalName);
                Files.createDirectories(target.getParent());
                file.transferTo(target);
            }
            return resolveProjectRoot(workspace);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile copiare la cartella caricata.", e);
        }
    }

    private Path newWorkspace() {
        return Path.of(System.getProperty("java.io.tmpdir"), "spring-guardian", UUID.randomUUID().toString());
    }

    private Path resolveProjectRoot(Path workspace) throws IOException {
        if (Files.exists(workspace.resolve("pom.xml")) || Files.isDirectory(workspace.resolve("src"))) {
            return workspace;
        }

        try (var children = Files.list(workspace)) {
            List<Path> visibleChildren = children
                    .filter(path -> path.getFileName() != null)
                    .filter(path -> !path.getFileName().toString().startsWith("."))
                    .toList();
            if (visibleChildren.size() == 1 && Files.isDirectory(visibleChildren.get(0))) {
                Path candidate = visibleChildren.get(0);
                if (Files.exists(candidate.resolve("pom.xml")) || Files.isDirectory(candidate.resolve("src"))) {
                    return candidate;
                }
            }
        }

        return workspace;
    }

    private Path safeResolve(Path workspace, String relativePath) {
        String cleaned = relativePath.replace('\\', '/');
        while (cleaned.startsWith("/")) {
            cleaned = cleaned.substring(1);
        }

        Path target = workspace.resolve(cleaned).normalize();
        if (!target.startsWith(workspace)) {
            throw new IllegalArgumentException("Percorso non valido nel progetto caricato: " + relativePath);
        }
        return target;
    }
}
