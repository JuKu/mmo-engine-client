package com.jukusoft.mmo.engine.applayer.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.init.Initializer;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.splashscreen.SplashScreen;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.applayer.utils.Platform;
import com.jukusoft.mmo.engine.applayer.utils.Utils;
import com.jukusoft.mmo.engine.applayer.version.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseApp implements ApplicationListener {

    protected static final String VERSION_TAG = "Version";
    protected static final String CONFIG_TAG = "Config";
    protected static final String SECTION_PATHS = "Paths";

    protected Boolean initialized = false;
    protected SplashScreen splashScreen = null;

    protected float elapsed = 0;
    protected float minInitTime = 1500;

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
            Log.i(CONFIG_TAG, "load ./config/game.cfg");

            try {
                Config.load(new File("./config/game.cfg"));
            } catch (IOException e) {
                Log.e(CONFIG_TAG, "IOException while loading config file ./config/game.cfg!", e);
                throw e;
            }

            String dataDir = FilePath.parse(Config.get(SECTION_PATHS, "dataDir"));

            //check, if data directory exists
            if (!new File(dataDir).exists()) {
                Log.e(CONFIG_TAG, "data directory '" + dataDir + "' doesn't exists!");
                throw new FileNotFoundException("data directory '" + dataDir + "' doesn't exists!");
            }

            FilePath.setDataDir(dataDir);

            String configDir = FilePath.parse(Config.get(SECTION_PATHS, "configDir"));
            String tempDir = FilePath.parse(Config.get(SECTION_PATHS, "tempDir"));

            //check, if config directories exists
            String[] dirs = configDir.split(";");

            for (String dir : dirs) {
                dir = FilePath.parse(dir);

                if (!new File(dir).exists()) {
                    Log.e(CONFIG_TAG, "config directory '" + dir + "' doesn't exists!");
                    //throw new FileNotFoundException("config directory '" + dir + "' doesn't exists!");

                    Log.i(CONFIG_TAG, "Create config directory now: " + dir);
                    new File(dir).mkdirs();
                }
            }

            //check, if temp directory exists, else create temp directory
            if (!new File(tempDir).exists()) {
                Log.i(CONFIG_TAG, "create new temp directory: " + tempDir);
                new File(tempDir).mkdirs();
            }

            Log.d(CONFIG_TAG, "config directory: " + configDir);
            Log.d(CONFIG_TAG, "temp directory: " + tempDir);

            FilePath.setConfigDirs(configDir);
            FilePath.setTempDir(tempDir);

            //load config directories
            Log.i(CONFIG_TAG, "load config directories with static data");

            for (String dir : dirs) {
                dir = FilePath.parse(dir);
                Log.d(CONFIG_TAG, "load config directory '" + dir + "'");
                Config.loadDir(new File(dir));
            }

            //load internationalization system
            Log.i("i18n", "Load i18n GNU gettext...");
            File langFolder = new File(FilePath.parse(Config.get("i18n", "langFolder")));
            I.init(langFolder, Locale.forLanguageTag(Config.getOrDefault("i18n", "lang", "en")), "messages");
            Log.i("i18n", "langFolder: " + langFolder.getAbsolutePath());

            Log.d("Splashscreen", "load splash screen");

            //load splash screen
            this.splashScreen = new SplashScreen();
            this.splashScreen.load();
        } catch (Exception e) {
            Log.e("Exception", "Exception while starting up game engine: ", e);
            Log.shutdown();

            try {
                JavaFXUtils.startJavaFX();

                //show exception in exception window
                JavaFXUtils.showExceptionDialog(Config.get("Error", "windowTitle"), Config.get("Error", "headerText"), "Exception while starting up game engine: ", e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.exit(1);
        }

        //start new initialize thread
        Thread initThread = new Thread(() -> {
            Initializer init = new Initializer(BaseApp.this);
            init.run();

            BaseApp.this.initFinished();
        });
        initThread.setName("game-init");
        initThread.start();
    }

    public void initFinished () {
        while (this.elapsed < this.minInitTime) {
            //wait
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                //dont do anything
            }
        }

        Log.v("BaseApp", "initFinished() called.");

        Platform.runOnUIThread(() -> this.initialized = true);
    }

    @Override
    public void resize(int width, int height) {
        //
    }

    @Override
    public void render() {
        //run tasks which have to be executed in ui thread
        Platform.executeQueue();

        if (!this.initialized) {
            splashScreen.render();
            this.elapsed += Gdx.graphics.getDeltaTime() * 1000;
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
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
