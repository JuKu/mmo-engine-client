package com.jukusoft.mmo.engine.applayer.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.init.Initializer;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.splashscreen.SplashScreen;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystemManager;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
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

public abstract class BaseApp implements ApplicationListener, SubSystemManager {

    protected static final String VERSION_TAG = "Version";
    protected static final String CONFIG_TAG = "Config";
    protected static final String SECTION_PATHS = "Paths";
    protected static final String THREADS_TAG = "Threads";

    protected Boolean initialized = false;
    protected SplashScreen splashScreen = null;

    protected float elapsed = 0;
    protected float minInitTime = 1500;

    //list with subsystems
    protected ObjectArrayList<SubSystem> subSystems = new ObjectArrayList<>();
    protected ObjectArrayList<SubSystem> extraThreadSubSystems = new ObjectArrayList<>();
    protected boolean multiThreadingMode = false;

    protected Thread gameLogicThread = null;
    protected long timePerGameLogicGameloopTick = 16;

    protected final GameTime gameTime = GameTime.getInstance();

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

            if (Config.getBool("MultiThreading", "useMultipleThreads")) {
                //use different threads for game logic & rendering
                this.multiThreadingMode = true;

                Log.i(THREADS_TAG, "use different threads for game logic & rendering");
            } else {
                Log.i(THREADS_TAG, "use single thread for game logic & rendering");
            }

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

        //add subsystems
        this.addSubSystems(this);

        Platform.runOnUIThread(() -> {
            //initialize subsystems in main thread
            this.subSystems.iterator().forEachRemaining(system -> system.value.onInit());

            this.initialized = true;

            if (this.multiThreadingMode) {
                Log.i(THREADS_TAG, "Create new game-logic-layer thread now.");

                //start extra thread for game logic layer
                this.gameLogicThread = new Thread(() -> {
                    Log.i(THREADS_TAG, "Initialize new game-logic-layer subsystems...");

                    //initialize game logic layer subsystems
                    this.extraThreadSubSystems.forEach((ObjectProcedure<? super SubSystem>) system -> system.onInit());

                    Log.i(THREADS_TAG, "game-logic-layer subsystems initialized successfully!");

                    //call subsystems which has to be executed in main thread
                    long startTime = 0;
                    long endTime = 0;
                    long diffTime = 0;

                    while (!Thread.interrupted()) {
                        startTime = System.currentTimeMillis();

                        extraThreadSubSystems.iterator().forEachRemaining(system -> system.value.onGameloop());

                        endTime = System.currentTimeMillis();
                        diffTime = endTime - startTime;

                        if (diffTime > timePerGameLogicGameloopTick - 1) {
                            Log.w(THREADS_TAG, "game logic layer thread required " + diffTime + "ms to execute the gameloop.");
                        } else {
                            try {
                                Thread.sleep(timePerGameLogicGameloopTick - diffTime);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }

                    Log.i(THREADS_TAG, "shutdown game-logic-layer subsystems now...");

                    //initialize game logic layer subsystems
                    this.extraThreadSubSystems.iterator().forEachRemaining(system -> system.value.onShutdown());

                    Log.i(THREADS_TAG, "closing game-logic-layer thread now.");
                });
                this.gameLogicThread.setName("game-logic-thread");
                this.gameLogicThread.start();
            }
        });
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
            //update timestamp and delta time for movements & animations
            gameTime.setTime(System.currentTimeMillis());
            gameTime.setDelta(Gdx.graphics.getDeltaTime());

            //call subsystems which has to be executed in main thread
            long startTime = System.currentTimeMillis();
            subSystems.iterator().forEachRemaining(system -> system.value.onGameloop());

            long endTime = System.currentTimeMillis();
            long diffTime = endTime - startTime;

            if (diffTime > 15) {
                Log.w("Gameloop", "Rendering gameloop required more than 16ms to render!");
            }
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
        //interrupt game logic layer thread
        if (this.multiThreadingMode && this.gameLogicThread != null) {
            this.gameLogicThread.interrupt();
        }

        Log.i(THREADS_TAG, "Shutdown subsystems now.");

        //shutdown subsystems now
        this.subSystems.iterator().forEachRemaining(system -> system.value.onShutdown());
    }

    @Override
    public void addSubSystem (SubSystem system, boolean useExtraThread) {
        if (useExtraThread && this.multiThreadingMode) {
            this.extraThreadSubSystems.add(system);
        } else {
            this.subSystems.add(system);
        }

        Log.i("SubSystem", "added subsystem " + system.getClass().getCanonicalName());
    }

    @Override
    public void removeSubSystem (SubSystem system) {
        this.subSystems.remove(this.subSystems.lastIndexOf(system));
    }

    protected abstract void addSubSystems (SubSystemManager manager);

}
