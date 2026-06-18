package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects packaged activation of Spring profiles inside application configuration.
 *
 * @author Simone Meneghetti
 */
public class HardcodedActiveSpringProfileRule implements SpringRule {

    @Override
    public String id() {
        return "SPR092_HARDCODED_ACTIVE_SPRING_PROFILE";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (Path file : applicationConfigFiles(context.root())) {
            inspectFile(context, file, findings);
        }
        return findings;
    }

    private void inspectFile(ProjectScanContext context, Path file, List<Finding> findings) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                String normalized = line.toLowerCase(Locale.ROOT).replace(" ", "");
                if (line.startsWith("#") || line.isBlank()) {
                    continue;
                }
                if ((normalized.startsWith("spring.profiles.active=") || normalized.startsWith("spring.profiles.active:")) && !line.contains("${")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Active Spring profile is hardcoded",
                            context.root().relativize(file).toString(),
                            i + 1,
                            "spring.profiles.active is assigned inside packaged configuration.",
                            "Hardcoding the active profile in application configuration makes the same artifact behave differently from what the deployment environment declares.",
                            "Remove spring.profiles.active from packaged configuration and set the active profile through environment variables, command-line arguments or the deployment platform."
                    ));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private List<Path> applicationConfigFiles(Path root) {
        try (var stream = Files.walk(root)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                        String normalized = path.toString().replace("\\", "/");
                        return !normalized.contains("/target/")
                                && !normalized.contains("/build/")
                                && name.startsWith("application")
                                && (name.endsWith(".properties") || name.endsWith(".yml") || name.endsWith(".yaml"));
                    })
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }
}
