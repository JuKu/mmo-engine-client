package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.shared.version.Version;
import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.cli.CommandLineInterface;

public class VersionCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        Version version = new Version(CommandLineInterface.class);
        return "CLI Version " + version.getFullVersion();
    }

    @Override
    public String getDescription() {
        return "shows version information";
    }

}
