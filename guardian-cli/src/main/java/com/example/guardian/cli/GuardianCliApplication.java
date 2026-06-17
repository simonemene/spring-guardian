package com.example.guardian.cli;

import picocli.CommandLine;

/**
 * Spring Guardian component for GuardianCliApplication.
 *
 * @author p15518 - Simone Meneghetti
 */
public class GuardianCliApplication {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GuardianCommand()).execute(args);
        System.exit(exitCode);
    }
}
