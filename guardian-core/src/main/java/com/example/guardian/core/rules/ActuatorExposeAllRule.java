package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ActuatorExposeAllRule implements SpringRule {

    @Override
    public String id() {
        return "SPR039_ACTUATOR_EXPOSE_ALL";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        try (var stream = Files.walk(context.root())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("application"))
                    .forEach(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i).replace(" ", "");
                                if (line.equalsIgnoreCase("management.endpoints.web.exposure.include=*")) {
                                    findings.add(new Finding(
                                            id(),
                                            Severity.MAJOR,
                                            "Actuator exposes all endpoints",
                                            context.root().relativize(path).toString(),
                                            i + 1,
                                            "Detected: " + lines.get(i).trim(),
                                            "Exposing every actuator endpoint can leak sensitive operational data or enable risky actions.",
                                            "Expose only required endpoints, protect them with security and avoid '*' outside local development."
                                    ));
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    });
        } catch (Exception ignored) {
        }

        return findings;
    }
}
