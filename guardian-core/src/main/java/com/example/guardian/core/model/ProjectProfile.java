package com.example.guardian.core.model;

/**
 * Stateless scan profile selected by the user for the current analysis.
 *
 * @param projectType functional project type
 * @param architectureStyle expected architecture style
 * @param releaseTarget release target used by quality gates
 * @param knownIssuesAccepted true when the scan is a legacy baseline with known issues
 * @author Simone Meneghetti
 */
public record ProjectProfile(
        ProjectType projectType,
        ArchitectureStyle architectureStyle,
        ReleaseTarget releaseTarget,
        boolean knownIssuesAccepted
) {
    /**
     * Returns the default strict profile.
     *
     * @return default project profile
     */
    public static ProjectProfile defaults() {
        return new ProjectProfile(ProjectType.WEB_API, ArchitectureStyle.AUTO_DETECTED, ReleaseTarget.PRODUCTION, false);
    }

    /**
     * Creates a scan profile from user-facing string values.
     *
     * @param projectType project type code or label
     * @param architectureStyle architecture style code or label
     * @param releaseTarget release target code or label
     * @param knownIssuesAccepted true when known issues should calibrate release gates
     * @return resolved project profile
     */
    public static ProjectProfile from(String projectType, String architectureStyle, String releaseTarget, boolean knownIssuesAccepted) {
        return new ProjectProfile(
                ProjectType.from(projectType),
                ArchitectureStyle.from(architectureStyle),
                ReleaseTarget.from(releaseTarget),
                knownIssuesAccepted
        );
    }
}
