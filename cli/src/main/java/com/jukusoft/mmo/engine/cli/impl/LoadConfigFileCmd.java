package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

import java.io.File;
import java.io.IOException;

public class LoadConfigFileCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        if (args.length != 1) {
            return "Invalide use of command! Usage: loadConfigFile path/to/config/file.cfg";
        }

        try {
            Config.load(new File(FilePath.parse(args[1])));
        } catch (IOException e) {
            Log.w("CLI", "IOException while trying to load config file: ", e);
            return "loading of config file failed! IOException: " + e.getLocalizedMessage();
        }

        return "Config file '" + args[1] + "' loaded successfully!";
    }

    @Override
    public String getDescription() {
        return "load a single config file. Usage: loadConfigFile path/to/config/file.cfg";
    }

}
