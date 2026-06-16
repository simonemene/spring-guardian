package com.example.guardian.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring-guardian")
public class ServerGuardianProperties {

    private String apiPrefix = "/api/v1";
    private int maxControllerLines = 35;
    private int maxControllerBranches = 8;

    public String getApiPrefix() {
        return apiPrefix;
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    public int getMaxControllerLines() {
        return maxControllerLines;
    }

    public void setMaxControllerLines(int maxControllerLines) {
        this.maxControllerLines = maxControllerLines;
    }

    public int getMaxControllerBranches() {
        return maxControllerBranches;
    }

    public void setMaxControllerBranches(int maxControllerBranches) {
        this.maxControllerBranches = maxControllerBranches;
    }
}
