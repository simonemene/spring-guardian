package com.example.guardian.cli;

import picocli.CommandLine.Command;

/**
 * Root command for the Spring Guardian command line interface.
 *
 * @author p15518 - Simone Meneghetti
 */
@Command(
        name = "spring-guardian",
        mixinStandardHelpOptions = true,
        version = "spring-guardian 1.0.0",
        description = "Local-first Spring Boot architecture validator",
        subcommands = {
                ScanCommand.class
        }
)
public class GuardianCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Use: spring-guardian scan <project-path> --format text|json|html");
    }
}
