package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Catalog-driven rule used for deterministic source, configuration and POM checks.
 *
 * @author p15518 - Simone Meneghetti
 */
public class CatalogPatternRule implements SpringRule {

    private static final int MAX_OCCURRENCES_PER_RULE = 80;

    private final Definition definition;

    /**
     * Creates a catalog-driven rule from one immutable definition.
     *
     * @param definition rule definition
     */
    public CatalogPatternRule(Definition definition) {
        this.definition = definition;
    }

    @Override
    public String id() {
        return definition.id();
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        if (!definition.contextPredicate().test(context)) {
            return List.of();
        }
        return switch (definition.target()) {
            case JAVA_MAIN, JAVA_ALL, JAVA_TEST -> evaluateJava(context);
            case POM -> evaluatePom(context);
            case CONFIG -> evaluateConfig(context);
            case ANY_FILE -> evaluateAnyFile(context);
        };
    }

    private List<Finding> evaluateJava(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            boolean testSource = SourceFilePredicates.isTestSource(file);
            if (definition.target() == SourceTarget.JAVA_MAIN && testSource) {
                continue;
            }
            if (definition.target() == SourceTarget.JAVA_TEST && !testSource) {
                continue;
            }
            inspectContent(file.relativePath(), file.content(), findings);
        }
        return findings;
    }

    private List<Finding> evaluatePom(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (Path pom : context.pomFiles()) {
            inspectPath(context, pom, findings);
        }
        return findings;
    }

    private List<Finding> evaluateConfig(ProjectScanContext context) {
        return evaluateFiles(context, path -> {
            String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
            return name.endsWith(".properties") || name.endsWith(".yml") || name.endsWith(".yaml");
        });
    }

    private List<Finding> evaluateAnyFile(ProjectScanContext context) {
        return evaluateFiles(context, path -> true);
    }

    private List<Finding> evaluateFiles(ProjectScanContext context, Predicate<Path> filePredicate) {
        List<Finding> findings = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(context.root())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> !ignored(path))
                    .filter(filePredicate)
                    .forEach(path -> inspectPath(context, path, findings));
        } catch (IOException ignored) {
            return List.of();
        }
        return findings;
    }

    private void inspectPath(ProjectScanContext context, Path path, List<Finding> findings) {
        try {
            String relative = context.root().relativize(path).toString();
            String content = Files.readString(path, StandardCharsets.UTF_8);
            inspectContent(relative, content, findings);
        } catch (Exception ignored) {
        }
    }

    private void inspectContent(String relativePath, String content, List<Finding> findings) {
        if (relativePath == null || content == null || content.isBlank()) {
            return;
        }
        String normalizedPath = relativePath.replace("\\", "/");
        String lowerPath = normalizedPath.toLowerCase(Locale.ROOT);
        if (!matchesPath(lowerPath)) {
            return;
        }
        String lowerContent = content.toLowerCase(Locale.ROOT);
        if (!containsAll(lowerContent, definition.requiredInFile())) {
            return;
        }
        String[] lines = content.split("\\R", -1);
        int emitted = 0;
        for (int i = 0; i < lines.length; i++) {
            if (ignoredLine(normalizedPath, lines[i])) {
                continue;
            }
            String lowerLine = lines[i].toLowerCase(Locale.ROOT);
            if (!containsAll(lowerLine, definition.requiredInLine())) {
                continue;
            }
            if (!containsAny(lowerLine, definition.anyInLine())) {
                continue;
            }
            findings.add(new Finding(
                    definition.id(),
                    definition.severity(),
                    definition.title(),
                    normalizedPath,
                    i + 1,
                    evidence(lines[i]),
                    definition.whyItMatters(),
                    definition.suggestedFix()
            ));
            emitted++;
            if (emitted >= definition.maxOccurrencesPerFile() || findings.size() >= MAX_OCCURRENCES_PER_RULE) {
                break;
            }
        }
    }

    private boolean matchesPath(String lowerPath) {
        if (!definition.pathIncludes().isEmpty() && !containsAny(lowerPath, definition.pathIncludes())) {
            return false;
        }
        if (!definition.pathExcludes().isEmpty() && containsAny(lowerPath, definition.pathExcludes())) {
            return false;
        }
        return true;
    }

    private boolean containsAll(String text, Set<String> patterns) {
        for (String pattern : patterns) {
            if (!text.contains(pattern.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private boolean containsAny(String text, Set<String> patterns) {
        if (patterns.isEmpty()) {
            return true;
        }
        for (String pattern : patterns) {
            if (text.contains(pattern.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String evidence(String line) {
        String compact = line == null ? "" : line.strip();
        if (compact.length() <= 220) {
            return compact;
        }
        return compact.substring(0, 217) + "...";
    }

    private boolean ignored(Path path) {
        String normalized = path.toString().replace("\\", "/").toLowerCase(Locale.ROOT);
        return normalized.contains("/target/")
                || normalized.contains("/build/")
                || normalized.contains("/.git/")
                || normalized.contains("/.idea/")
                || normalized.contains("/node_modules/")
                || normalized.contains("/dist/");
    }

    private boolean ignoredLine(String normalizedPath, String line) {
        if (line == null) {
            return true;
        }
        String trimmed = line.strip();
        if (trimmed.isEmpty()) {
            return true;
        }
        String lowerPath = normalizedPath.toLowerCase(Locale.ROOT);
        if (lowerPath.endsWith(".java")) {
            return trimmed.startsWith("//")
                    || trimmed.startsWith("/*")
                    || trimmed.startsWith("*")
                    || trimmed.startsWith("*/");
        }
        if (lowerPath.endsWith(".properties") || lowerPath.endsWith(".yml") || lowerPath.endsWith(".yaml")) {
            return trimmed.startsWith("#") || trimmed.startsWith("!");
        }
        if (lowerPath.endsWith(".xml")) {
            return trimmed.startsWith("<!--") || trimmed.endsWith("-->");
        }
        return false;
    }

    /**
     * Source target inspected by a catalog-driven rule.
     *
     * @author p15518 - Simone Meneghetti
     */
    public enum SourceTarget {
        JAVA_MAIN,
        JAVA_ALL,
        JAVA_TEST,
        POM,
        CONFIG,
        ANY_FILE
    }

    /**
     * Immutable definition for one deterministic catalog rule.
     *
     * @param id rule identifier
     * @param severity severity
     * @param target target file family
     * @param pathIncludes optional path include fragments
     * @param pathExcludes optional path exclude fragments
     * @param requiredInFile patterns that must exist in the same file
     * @param requiredInLine patterns that must exist in the same line
     * @param anyInLine at least one pattern that must exist in the same line
     * @param maxOccurrencesPerFile maximum findings per file
     * @param title finding title
     * @param whyItMatters risk explanation
     * @param suggestedFix recommended fix
     * @param contextPredicate optional context predicate
     * @author p15518 - Simone Meneghetti
     */
    public record Definition(
            String id,
            Severity severity,
            SourceTarget target,
            Set<String> pathIncludes,
            Set<String> pathExcludes,
            Set<String> requiredInFile,
            Set<String> requiredInLine,
            Set<String> anyInLine,
            int maxOccurrencesPerFile,
            String title,
            String whyItMatters,
            String suggestedFix,
            Predicate<ProjectScanContext> contextPredicate
    ) {
        /**
         * Creates a minimal source line definition.
         *
         * @param id rule identifier
         * @param severity severity
         * @param target source target
         * @param anyInLine triggering fragments
         * @param title title
         * @param whyItMatters risk explanation
         * @param suggestedFix suggested fix
         * @return definition
         */
        public static Definition line(String id, Severity severity, SourceTarget target, Set<String> anyInLine, String title, String whyItMatters, String suggestedFix) {
            return new Definition(
                    id,
                    severity,
                    target,
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Set.copyOf(anyInLine),
                    3,
                    title,
                    whyItMatters,
                    suggestedFix,
                    context -> true
            );
        }

        /**
         * Returns a copy with a context predicate.
         *
         * @param predicate context predicate
         * @return updated definition
         */
        public Definition onlyWhen(Predicate<ProjectScanContext> predicate) {
            return new Definition(id, severity, target, pathIncludes, pathExcludes, requiredInFile, requiredInLine, anyInLine, maxOccurrencesPerFile, title, whyItMatters, suggestedFix, predicate);
        }

        /**
         * Returns a copy with additional path include fragments.
         *
         * @param includes path include fragments
         * @return updated definition
         */
        public Definition inPaths(String... includes) {
            return new Definition(id, severity, target, Set.of(includes), pathExcludes, requiredInFile, requiredInLine, anyInLine, maxOccurrencesPerFile, title, whyItMatters, suggestedFix, contextPredicate);
        }

        /**
         * Returns a copy with path exclude fragments.
         *
         * @param excludes path exclude fragments
         * @return updated definition
         */
        public Definition excludingPaths(String... excludes) {
            return new Definition(id, severity, target, pathIncludes, Set.of(excludes), requiredInFile, requiredInLine, anyInLine, maxOccurrencesPerFile, title, whyItMatters, suggestedFix, contextPredicate);
        }

        /**
         * Returns a copy requiring file-level fragments.
         *
         * @param required required fragments
         * @return updated definition
         */
        public Definition requiringFile(String... required) {
            return new Definition(id, severity, target, pathIncludes, pathExcludes, Set.of(required), requiredInLine, anyInLine, maxOccurrencesPerFile, title, whyItMatters, suggestedFix, contextPredicate);
        }

        /**
         * Returns a copy requiring line-level fragments.
         *
         * @param required required fragments
         * @return updated definition
         */
        public Definition requiringLine(String... required) {
            return new Definition(id, severity, target, pathIncludes, pathExcludes, requiredInFile, Set.of(required), anyInLine, maxOccurrencesPerFile, title, whyItMatters, suggestedFix, contextPredicate);
        }

        /**
         * Returns a copy limiting occurrences per file.
         *
         * @param max max occurrences
         * @return updated definition
         */
        public Definition max(int max) {
            return new Definition(id, severity, target, pathIncludes, pathExcludes, requiredInFile, requiredInLine, anyInLine, max, title, whyItMatters, suggestedFix, contextPredicate);
        }
    }
}
