package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Detects explicit Open EntityManager in View activation in Spring Boot configuration.
 *
 * @author p15518 - Simone Meneghetti
 */
public class OpenEntityManagerInViewEnabledRule implements SpringRule {

    private static final Pattern OPEN_IN_VIEW_ENABLED = Pattern.compile("^\\s*spring\\.jpa\\.open-in-view\\s*[:=]\\s*true\\b.*$", Pattern.CASE_INSENSITIVE);

    @Override
    public String id() {
        return "SPR096_JPA_OPEN_IN_VIEW_ENABLED";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        try (var stream = Files.walk(context.root())) {
            stream.filter(Files::isRegularFile)
                    .filter(this::isApplicationConfiguration)
                    .filter(path -> !isIgnored(context.root(), path))
                    .forEach(path -> inspect(context, path, findings));
        } catch (IOException ignored) {
            return List.of();
        }
        return findings;
    }

    private void inspect(ProjectScanContext context, Path path, List<Finding> findings) {
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            String[] lines = content.split("\\R", -1);
            for (int index = 0; index < lines.length; index++) {
                String line = stripInlineComment(lines[index]);
                if (OPEN_IN_VIEW_ENABLED.matcher(line).matches()) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Open EntityManager in View enabled",
                            context.root().relativize(path).toString().replace('\\', '/'),
                            index + 1,
                            lines[index].strip(),
                            "Keeping the persistence context open during web rendering can hide lazy loading and move database access outside the service transaction boundary.",
                            "Disable spring.jpa.open-in-view and load the required data inside service-level transactions using DTOs, projections, fetch joins or entity graphs."
                    ));
                }
            }
        } catch (IOException ignored) {
        }
    }

    private String stripInlineComment(String line) {
        int comment = line.indexOf('#');
        if (comment < 0) {
            return line;
        }
        return line.substring(0, comment);
    }

    private boolean isApplicationConfiguration(Path path) {
        String name = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".properties") || name.endsWith(".yml") || name.endsWith(".yaml");
    }

    private boolean isIgnored(Path root, Path path) {
        String relative = "/" + root.relativize(path).toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        return relative.contains("/target/")
                || relative.contains("/build/")
                || relative.contains("/.git/")
                || relative.contains("/.idea/")
                || relative.contains("/node_modules/")
                || relative.contains("/dist/")
                || relative.contains("/.angular/")
                || relative.contains("/out-tsc/")
                || relative.contains("/src/test/");
    }
}
