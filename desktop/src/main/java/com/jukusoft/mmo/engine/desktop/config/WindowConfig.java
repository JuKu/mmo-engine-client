package com.jukusoft.mmo.engine.desktop.config;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WindowConfig {

    //ini4j instance
    protected Ini ini = null;

    //window section in config file
    protected Profile.Section section = null;

    /**
    * default constructor
     *
     * @param filePath path to config file
    */
    public WindowConfig(String filePath) throws IOException {
        if (filePath == null) {
            throw new NullPointerException("filePath cannot be null.");
        }

        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath is empty.");
        }

        //check, if file exists
        if (!new File(filePath).exists()) {
            throw new FileNotFoundException("config file doesnt exists: " + new File(filePath).getAbsolutePath());
        }

        //create ini instance
        this.ini = new Ini(new File(filePath));

        //get section
        this.section = this.ini.get("Window");
    }

    public void fillConfig (Lwjgl3ApplicationConfiguration config) {
        config.setTitle(section.get("title"));
        config.setWindowedMode(getInt("width"), getInt("height"));
        config.setWindowIcon(section.get("iconPath"));
        config.setResizable(getBoolean("resizeable"));
        config.useVsync(getBoolean("vsync"));

        if (getBoolean("fullscreen")) {
            throw new UnsupportedOperationException("fullscreen isnt supported yet.");
        }
    }

    protected int getInt (String key) {
        return Integer.parseInt(this.section.get(key));
    }

    protected int getIntOrDefault (String key, int defaultValue) {
        return Integer.parseInt(this.section.getOrDefault(key, "" + defaultValue));
    }

    protected boolean getBoolean (String key) {
        return Boolean.parseBoolean(this.section.get(key));
    }

}
