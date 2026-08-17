package com.example.guardian.core;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.security.SensitiveDataRedactor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/** Extracts a small, redacted source context around a finding. */
final class SourceEvidenceExtractor {

    String safeEvidence(Finding finding) {
        return SensitiveDataRedactor.redact(finding.evidence());
    }

    String codeSnippetOf(Finding finding, ProjectScanContext context) {
        if (finding.filePath() == null || finding.filePath().isBlank() || finding.line() == null || finding.line() <= 0) {
            return safeEvidence(finding);
        }
        String content = sourceContentFor(finding, context);
        if (content == null || content.isBlank()) {
            return safeEvidence(finding);
        }
        String[] lines = content.split("\\R", -1);
        int index = finding.line() - 1;
        if (index < 0 || index >= lines.length) {
            return safeEvidence(finding);
        }
        int from = Math.max(0, index - 1);
        int to = Math.min(lines.length - 1, index + 1);
        StringBuilder snippet = new StringBuilder();
        for (int i = from; i <= to; i++) {
            if (snippet.length() > 0) {
                snippet.append(System.lineSeparator());
            }
            snippet.append(String.format("%4d | %s", i + 1, lines[i]));
        }
        return SensitiveDataRedactor.redact(snippet.toString());
    }

    private String sourceContentFor(Finding finding, ProjectScanContext context) {
        String normalizedFindingPath = finding.filePath().replace("\\", "/");
        for (var javaFile : context.javaFiles()) {
            if (javaFile.relativePath().replace("\\", "/").equals(normalizedFindingPath)) {
                return javaFile.content();
            }
        }
        try {
            Path root = context.root().toAbsolutePath().normalize();
            Path path = root.resolve(finding.filePath()).normalize();
            if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) && path.startsWith(root)) {
                return Files.readString(path, StandardCharsets.UTF_8);
            }
        } catch (IOException | RuntimeException ignored) {
            return null;
        }
        return null;
    }
}
