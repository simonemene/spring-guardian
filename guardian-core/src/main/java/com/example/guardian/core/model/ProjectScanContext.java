package com.example.guardian.core.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Immutable input context shared by all scan rules.
 *
 * @param root project root
 * @param javaFiles parsed Java files
 * @param pomFiles Maven POM files
 * @param entityClassNames detected entity class names
 * @param hasTests true when test sources are present
 * @param hasRestControllerAdvice true when a REST advice is present
 * @param capabilities detected frameworks and architecture signals
 * @param profile stateless scan profile resolved for this analysis
 * @author Simone Meneghetti
 */
public record ProjectScanContext(
        Path root,
        List<JavaSourceFile> javaFiles,
        List<Path> pomFiles,
        Set<String> entityClassNames,
        boolean hasTests,
        boolean hasRestControllerAdvice,
        ProjectCapabilities capabilities,
        ProjectProfile profile
) {
}
