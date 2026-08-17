package com.example.guardian.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Temporary project workspace that deletes its complete extraction root when closed. */
public final class PreparedWorkspace implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparedWorkspace.class);

    private final Path workspaceRoot;
    private final Path projectRoot;
    private boolean closed;

    PreparedWorkspace(Path workspaceRoot, Path projectRoot) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.projectRoot = projectRoot.toAbsolutePath().normalize();
    }

    public Path projectRoot() {
        return projectRoot;
    }

    Path workspaceRoot() {
        return workspaceRoot;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            deleteRecursively(workspaceRoot);
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Unable to completely delete Spring Guardian temporary workspace {}: {}", workspaceRoot, exception.getMessage());
            scheduleDeleteOnExit(workspaceRoot);
        }
    }

    private void scheduleDeleteOnExit(Path root) {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().deleteOnExit());
        } catch (IOException | RuntimeException ignored) {
            root.toFile().deleteOnExit();
        }
    }

    private void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
