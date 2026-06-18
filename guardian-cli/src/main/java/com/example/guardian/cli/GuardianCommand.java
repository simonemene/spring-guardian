package com.example.guardian.cli;

import picocli.CommandLine.Command;

@Command(
        name = "spring-guardian",
        mixinStandardHelpOptions = true,
        version = "spring-guardian 0.4.0",
        description = "Local-first Spring Boot architecture validator",
        subcommands = {
                ScanCommand.class
        }
)
/**
 * Spring Guardian component for GuardianCommand.
 *
 * @author Simone Meneghetti
 */
public class GuardianCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Use: spring-guardian scan <project-path>");
    }
}
