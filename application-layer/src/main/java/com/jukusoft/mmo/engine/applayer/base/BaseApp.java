package com.jukusoft.mmo.engine.applayer.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.Utils;
import com.jukusoft.mmo.engine.applayer.version.Version;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseApp implements ApplicationListener {

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

        Log.i("Startup", "Started Game Engine.");

        //print game engine version information
        Utils.printSection("Game Engine");
        Version version = new Version(BaseApp.class);
        Log.i("Version", "Version: " + version.getVersion());
        Log.i("Version", "Build: " + version.getRevision());
        Log.i("Version", "Build JDK: " + version.getBuildJdk());
        Log.i("Version", "Build Time: " + version.getBuildTime());
        Log.i("Version", "Vendor ID: " + (!version.getVendor().equals("n/a") ? version.getVendor() : version.getVendorID()));

        //print libGDX version
        Utils.printSection("libGDX");
        Log.i("libGDX", "libGDX Version: " + com.badlogic.gdx.Version.VERSION);

        //print java version
        Utils.printSection("Java Version");
        Log.i("Java", "Java Vendor: " + System.getProperty("java.vendor"));
        Log.i("Java", "Java Vendor URL: " + System.getProperty("java.vendor.url"));
        Log.i("Java", "Java Version: " + System.getProperty("java.version"));
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
