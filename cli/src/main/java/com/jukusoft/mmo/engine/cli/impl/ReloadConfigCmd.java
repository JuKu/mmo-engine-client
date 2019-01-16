package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;

import java.io.IOException;

public class ReloadConfigCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        try {
            Config.reload();
        } catch (IOException e) {
            Log.w("ReloadConfig", "IOException while call Config.reload(): ", e);
            return "reload failed!\n" + e.getLocalizedMessage();
        }

        return "reloaded config successfully!";
    }

    @Override
    public String getDescription() {
        return "reload config at runtime (so you can change values in config files and reload them directly)";
    }

}
