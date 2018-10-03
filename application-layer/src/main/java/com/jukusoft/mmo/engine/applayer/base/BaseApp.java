package com.jukusoft.mmo.engine.applayer.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.applayer.utils.Utils;
import com.jukusoft.mmo.engine.applayer.version.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseApp implements ApplicationListener {

    protected static final String VERSION_TAG = "Version";

    @Override
    public void create() {
        //load logger config
        try {
            Config.load(Gdx.files.absolute("./config/logger.cfg"));

            //initialize logger
            Log.init();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Couldn't initialize config and logger!", e);
            System.exit(0);
        }

        try {
            Log.i("Startup", "Started Game Engine.");

            //print game engine version information
            Utils.printSection("Game Engine");
            Version version = new Version(BaseApp.class);
            Log.i(VERSION_TAG, "Version: " + version.getVersion());
            Log.i(VERSION_TAG, "Build: " + version.getRevision());
            Log.i(VERSION_TAG, "Build JDK: " + version.getBuildJdk());
            Log.i(VERSION_TAG, "Build Time: " + version.getBuildTime());
            Log.i(VERSION_TAG, "Vendor ID: " + (!version.getVendor().equals("n/a") ? version.getVendor() : version.getVendorID()));

            //print libGDX version
            Utils.printSection("libGDX");
            Log.i("libGDX", "libGDX Version: " + com.badlogic.gdx.Version.VERSION);

            //print java version
            Utils.printSection("Java Version");
            Log.i("Java", "Java Vendor: " + System.getProperty("java.vendor"));
            Log.i("Java", "Java Vendor URL: " + System.getProperty("java.vendor.url"));
            Log.i("Java", "Java Version: " + System.getProperty("java.version"));

            //load all config files
            Utils.printSection("Configuration & Init");
            Log.i("Config", "load ./config/game.cfg");

            try {
                Config.load(new File("./config/game.cfg"));
            } catch (IOException e) {
                Log.e("Config", "IOException while loading config file ./config/game.cfg!", e);
                throw e;
            }

            String dataDir = FilePath.parse(Config.get("Paths", "dataDir"));
            String configDir = FilePath.parse(Config.get("Paths", "configDir"));
            String tempDir = FilePath.parse(Config.get("Paths", "tempDir"));

            //check, if data directory exists
            if (!new File(dataDir).exists()) {
                Log.e("Config", "data directory '" + dataDir + "' doesn't exists!");
                throw new FileNotFoundException("data directory '" + dataDir + "' doesn't exists!");
            }

            //check, if config directory exists
            if (!new File(configDir).exists()) {
                Log.e("Config", "config directory '" + configDir + "' doesn't exists!");
                throw new FileNotFoundException("config directory '" + configDir + "' doesn't exists!");
            }

            //check, if temp directory exists, else create temp directory
            if (!new File(tempDir).exists()) {
                new File(tempDir).mkdirs();
            }

            Log.d("Config", "data directory: " + dataDir);
            Log.d("Config", "config directory: " + configDir);
            Log.d("Config", "temp directory: " + tempDir);

            FilePath.setDataDir(dataDir);
        } catch (Exception e) {
            Log.e("Exception", "Exception while starting up game engine: ", e);
            Log.shutdown();

            try {
                JavaFXUtils.startJavaFX();

                //show exception in exception window
                JavaFXUtils.showExceptionDialog(Config.get("Error", "windowTitle"), "Exception while starting up game engine: ", e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.exit(1);
        }
    }

    @Override
    public void resize(int width, int height) {
        //
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
