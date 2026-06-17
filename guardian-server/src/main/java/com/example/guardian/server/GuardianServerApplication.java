package com.example.guardian.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Spring Guardian backend.
 *
 * @author p15518 - Simone Meneghetti
 */
@SpringBootApplication
public class GuardianServerApplication {

    /**
     * Starts the backend application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GuardianServerApplication.class, args);
    }
}
