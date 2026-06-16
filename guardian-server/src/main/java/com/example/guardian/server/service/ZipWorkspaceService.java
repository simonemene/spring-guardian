package com.example.guardian.server.service;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ZipWorkspaceService {

    public Path extractZip(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("Only .zip files are supported");
        }

        Path workspace = Path.of(System.getProperty("java.io.tmpdir"), "spring-guardian", UUID.randomUUID().toString());

        try {
            Files.createDirectories(workspace);
            try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(file.getInputStream()))) {
                ZipArchiveEntry entry;
                while ((entry = zip.getNextZipEntry()) != null) {
                    Path target = workspace.resolve(entry.getName()).normalize();

                    if (!target.startsWith(workspace)) {
                        throw new IllegalArgumentException("Invalid ZIP entry path: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(zip, target);
                    }
                }
            }
            return workspace;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to extract ZIP file", e);
        }
    }
}
