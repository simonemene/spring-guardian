package com.example.guardian.server.config;

import com.example.guardian.core.ProjectScanService;
import com.example.guardian.core.config.GuardianSettings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the backend with the Spring Guardian core module.
 *
 * @author p15518 - Simone Meneghetti
 */
@Configuration
@EnableConfigurationProperties(ServerGuardianProperties.class)
public class GuardianCoreConfig {

    /**
     * Creates the project scan service using backend configuration properties.
     *
     * @param properties backend scanner properties
     * @return project scan service
     */
    @Bean
    public ProjectScanService projectScanService(ServerGuardianProperties properties) {
        GuardianSettings settings = GuardianSettings.of(
                properties.getApiPrefix(),
                properties.getMaxControllerLines(),
                properties.getMaxControllerBranches()
        );
        return new ProjectScanService(settings);
    }
}
