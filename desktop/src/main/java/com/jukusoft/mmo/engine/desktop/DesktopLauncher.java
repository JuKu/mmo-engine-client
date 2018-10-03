package com.jukusoft.mmo.engine.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.desktop.config.WindowConfig;

import java.io.File;

public class DesktopLauncher {

    public static void main (String[] args) {
        //start game
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected static void start () throws Exception {
        //check working directory
        if (!(new File("./config/").exists())) {
            throw new IllegalStateException("Working directory isn't correct, couldn't found config directory! Current working dir: " + (new File(".").getAbsolutePath()));
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        //load window config
        WindowConfig windowConfig = new WindowConfig("./config/window.cfg");
        windowConfig.fillConfig(config);

        // start game
        new Lwjgl3Application(new BaseApp(), config);

        //shutdown logger and write all remaining logs to file
        Log.shutdown();

        System.err.println("shutdown JVM now.");
        System.exit(0);
    }

}
