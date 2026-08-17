package com.example.guardian.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipWorkspaceServiceTest {

    private final ZipWorkspaceService service = new ZipWorkspaceService();

    @Test
    void extractZipCreatesWorkspaceFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "project.zip", "application/zip", zipBytes("demo/pom.xml", "<project/>").toByteArray());

        Path extractionRoot;
        try (PreparedWorkspace workspace = service.extractZip(file)) {
            extractionRoot = workspace.workspaceRoot();
            assertTrue(Files.exists(workspace.projectRoot().resolve("pom.xml")));
        }
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(extractionRoot));
    }

    @Test
    void extractZipRejectsZipSlipPaths() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "project.zip", "application/zip", zipBytes("../bad.txt", "bad").toByteArray());

        assertThrows(IllegalArgumentException.class, () -> service.extractZip(file));
    }

    @Test
    void copyUploadedFolderPreservesRelativePath() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "demo/src/main/java/App.java", "text/plain", "class App {}".getBytes());

        try (PreparedWorkspace workspace = service.copyUploadedFolder(List.of(file))) {
            assertTrue(Files.exists(workspace.projectRoot().resolve("src/main/java/App.java")));
        }
    }



    @Test
    void extractZipSkipsGeneratedAndDependencyDirectories() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            add(zip, "demo/pom.xml", "<project/>");
            add(zip, "demo/target/classes/App.class", "compiled");
            add(zip, "demo/node_modules/lib/index.js", "dependency");
            add(zip, "demo/.git/config", "git");
        }
        MockMultipartFile file = new MockMultipartFile("file", "project.zip", "application/zip", output.toByteArray());

        Path extractionRoot;
        try (PreparedWorkspace workspace = service.extractZip(file)) {
            extractionRoot = workspace.workspaceRoot();
            Path projectRoot = workspace.projectRoot();
            assertTrue(Files.exists(projectRoot.resolve("pom.xml")));
            org.junit.jupiter.api.Assertions.assertFalse(Files.exists(projectRoot.resolve("target/classes/App.class")));
            org.junit.jupiter.api.Assertions.assertFalse(Files.exists(projectRoot.resolve("node_modules/lib/index.js")));
            org.junit.jupiter.api.Assertions.assertFalse(Files.exists(projectRoot.resolve(".git/config")));
        }
        org.junit.jupiter.api.Assertions.assertFalse(Files.exists(extractionRoot));
    }

    private ByteArrayOutputStream zipBytes(String name, String content) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            add(zip, name, content);
        }
        return output;
    }

    private void add(ZipOutputStream zip, String name, String content) throws Exception {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes());
        zip.closeEntry();
    }
}
