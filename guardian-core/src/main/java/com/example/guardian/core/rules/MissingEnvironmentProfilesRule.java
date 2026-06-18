package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Deterministic Spring Guardian rule implementation for MissingEnvironmentProfilesRule.
 *
 * @author Simone Meneghetti
 */
public class MissingEnvironmentProfilesRule implements SpringRule {

    @Override
    public String id() {
        return "SPR036_MISSING_ENVIRONMENT_PROFILES";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        boolean hasMainConfig = exists(context.root(), "application.yml")
                || exists(context.root(), "application.properties");

        if (!hasMainConfig) {
            return List.of();
        }

        boolean hasProfileConfig = exists(context.root(), "application-dev.yml")
                || exists(context.root(), "application-dev.properties")
                || exists(context.root(), "application-test.yml")
                || exists(context.root(), "application-test.properties")
                || exists(context.root(), "application-prod.yml")
                || exists(context.root(), "application-prod.properties");

        if (!hasProfileConfig) {
            return List.of(new Finding(
                    id(),
                    Severity.MINOR,
                    "No environment-specific Spring profile configuration detected",
                    null,
                    null,
                    "application.yml/properties exists, but no application-dev/test/prod configuration was found.",
                    "Spring applications usually need explicit configuration boundaries between local, test and production environments.",
                    "Add profile-specific configuration files if environments differ, or document your profile strategy."
            ));
        }

        return List.of();
    }

    private boolean exists(Path root, String filename) {
        try (var stream = Files.walk(root)) {
            return stream.anyMatch(path -> path.getFileName().toString().equals(filename));
        } catch (Exception e) {
            return false;
        }
    }
}
