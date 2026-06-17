package com.example.guardian.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Backend configuration properties used to tune Spring Guardian rule thresholds.
 *
 * @author Simone Meneghetti
 */
@ConfigurationProperties(prefix = "spring-guardian")
public class ServerGuardianProperties {

    private String apiPrefix = "/api/v1";
    private int maxControllerLines = 35;
    private int maxControllerBranches = 8;

    /**
     * Returns the expected REST API prefix.
     *
     * @return REST API prefix
     */
    public String getApiPrefix() {
        return apiPrefix;
    }

    /**
     * Updates the expected REST API prefix.
     *
     * @param apiPrefix REST API prefix
     */
    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    /**
     * Returns the maximum accepted controller length before a warning is raised.
     *
     * @return maximum controller lines
     */
    public int getMaxControllerLines() {
        return maxControllerLines;
    }

    /**
     * Updates the maximum accepted controller length.
     *
     * @param maxControllerLines maximum controller lines
     */
    public void setMaxControllerLines(int maxControllerLines) {
        this.maxControllerLines = maxControllerLines;
    }

    /**
     * Returns the maximum accepted number of controller branches before a warning is raised.
     *
     * @return maximum controller branches
     */
    public int getMaxControllerBranches() {
        return maxControllerBranches;
    }

    /**
     * Updates the maximum accepted number of controller branches.
     *
     * @param maxControllerBranches maximum controller branches
     */
    public void setMaxControllerBranches(int maxControllerBranches) {
        this.maxControllerBranches = maxControllerBranches;
    }
}
