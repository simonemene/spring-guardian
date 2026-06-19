package com.example.guardian.server.service;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Prepares temporary workspaces from uploaded ZIP archives or browser folder uploads.
 *
 * @author p15518 - Simone Meneghetti
 */
@Service
public class ZipWorkspaceService {

    private static final int MAX_ARCHIVE_ENTRIES = 25_000;
    private static final long MAX_UNCOMPRESSED_BYTES = 250L * 1024L * 1024L;
    private static final long MAX_SINGLE_FILE_BYTES = 50L * 1024L * 1024L;
    private static final int BUFFER_SIZE = 16 * 1024;

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
        if (originalName == null || !originalName.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new IllegalArgumentException("Sono supportati solo file .zip.");
        }

        Path workspace = newWorkspace();

        try {
            Files.createDirectories(workspace);
            long totalBytes = 0;
            int entries = 0;
            try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(file.getInputStream()))) {
                ZipArchiveEntry entry;
                while ((entry = zip.getNextZipEntry()) != null) {
                    entries++;
                    if (entries > MAX_ARCHIVE_ENTRIES) {
                        throw new IllegalArgumentException("Archivio ZIP troppo grande: numero massimo di file superato.");
                    }

                    String entryName = entry.getName();
                    if (isIgnoredRelativePath(entryName)) {
                        continue;
                    }

                    Path target = safeResolve(workspace, entryName);

                    if (entry.isDirectory()) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        long copied = copyEntry(zip, target);
                        totalBytes += copied;
                        if (copied > MAX_SINGLE_FILE_BYTES || totalBytes > MAX_UNCOMPRESSED_BYTES) {
                            throw new IllegalArgumentException("Archivio ZIP troppo grande per una scansione interattiva.");
                        }
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
            long totalBytes = 0;
            int copiedFiles = 0;
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                String originalName = file.getOriginalFilename();
                if (originalName == null || originalName.isBlank() || isIgnoredRelativePath(originalName)) {
                    continue;
                }

                if (file.getSize() > MAX_SINGLE_FILE_BYTES) {
                    throw new IllegalArgumentException("File troppo grande nella cartella caricata: " + originalName);
                }

                totalBytes += file.getSize();
                copiedFiles++;
                if (copiedFiles > MAX_ARCHIVE_ENTRIES || totalBytes > MAX_UNCOMPRESSED_BYTES) {
                    throw new IllegalArgumentException("Cartella troppo grande per una scansione interattiva.");
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

    private long copyEntry(InputStream input, Path target) throws IOException {
        long copied = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        try (var output = Files.newOutputStream(target)) {
            int read;
            while ((read = input.read(buffer)) >= 0) {
                copied += read;
                if (copied > MAX_SINGLE_FILE_BYTES) {
                    throw new IllegalArgumentException("File troppo grande nell'archivio ZIP: " + target.getFileName());
                }
                output.write(buffer, 0, read);
            }
        }
        return copied;
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

    private boolean isIgnoredRelativePath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return true;
        }
        String normalized = "/" + relativePath.replace('\\', '/').toLowerCase(Locale.ROOT);
        return normalized.contains("/.git/")
                || normalized.contains("/.idea/")
                || normalized.contains("/.gradle/")
                || normalized.contains("/target/")
                || normalized.contains("/build/")
                || normalized.contains("/node_modules/")
                || normalized.contains("/dist/")
                || normalized.contains("/out-tsc/")
                || normalized.contains("/.angular/")
                || normalized.contains("/coverage/");
    }
}
