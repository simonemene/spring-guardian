package com.example.guardian.cli;

import picocli.CommandLine;

public class GuardianCliApplication {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GuardianCommand()).execute(args);
        System.exit(exitCode);
    }
}
