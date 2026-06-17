package com.example.guardian.core.config;

/**
 * Immutable Spring Guardian data model for GuardianSettings.
 *
 * @author p15518 - Simone Meneghetti
 */
public record GuardianSettings(
        String requiredApiPrefix,
        int maxControllerMethodLines,
        int maxControllerBranchingStatements,
        int maxInjectedDependencies,
        int maxClassLines,
        int maxMethodsPerClass,
        int maxServiceMethodLines,
        int maxServiceBranchingStatements
) {
    public static GuardianSettings defaults() {
        return new GuardianSettings("/api/v1", 35, 8, 7, 400, 20, 80, 12);
    }

    public static GuardianSettings of(String requiredApiPrefix,
                                      int maxControllerMethodLines,
                                      int maxControllerBranchingStatements) {
        GuardianSettings defaults = defaults();
        return new GuardianSettings(
                requiredApiPrefix,
                maxControllerMethodLines,
                maxControllerBranchingStatements,
                defaults.maxInjectedDependencies,
                defaults.maxClassLines,
                defaults.maxMethodsPerClass,
                defaults.maxServiceMethodLines,
                defaults.maxServiceBranchingStatements
        );
    }
}
