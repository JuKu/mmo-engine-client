package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;

public class VersionCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        return "not implemented yet!";
    }

    @Override
    public String getDescription() {
        return "shows version information";
    }

}
