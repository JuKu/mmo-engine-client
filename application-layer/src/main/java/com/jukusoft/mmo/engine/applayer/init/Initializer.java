package com.jukusoft.mmo.engine.applayer.init;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.config.Cache;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.network.ServerManager;
import com.jukusoft.mmo.engine.applayer.script.ScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.utils.*;
import com.jukusoft.mmo.engine.applayer.version.Version;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Initializer implements Runnable {

    protected final BaseApp app;

    protected final String OPENGL_TAG = "OpenGL";
    protected final String UPDATE_TAG = "Update";
    protected final String SERVERS_TAG = "Servers";
    protected final String SCRIPTS_TAG = "Scripts";
    protected final String SR_SECTION = "SystemRequirements";

    public Initializer (BaseApp app) {
        this.app = app;
    }

    @Override
    public void run() {
        //check hardware resources
        ResourceChecker.check();

        //get OpenGL version
        AtomicReference<GLVersion> vA = new AtomicReference<>();

        //this operation has to be executed in ui thread
        ThreadUtils.executeOnUIThreadAndWait(() -> vA.set(Gdx.graphics.getGLVersion()));

        GLVersion glVersion = vA.get();

        Utils.printSection(OPENGL_TAG);
        Log.i(OPENGL_TAG, "version: " + glVersion.getMajorVersion() + "." + glVersion.getMajorVersion());
        Log.i(OPENGL_TAG, "release version: " + glVersion.getReleaseVersion());
        Log.i(OPENGL_TAG, "vendor: " + glVersion.getVendorString());
        Log.i(OPENGL_TAG, "graphics card: " + glVersion.getRendererString());
        Log.i(OPENGL_TAG, "type: " + glVersion.getType().name());

        int requiredMajor = Config.getInt(SR_SECTION, "openGLMajor");
        int requiredMinor = Config.getInt(SR_SECTION, "openGLMinor");

        //check OpenGL version
        if (!glVersion.isVersionEqualToOrHigher(requiredMajor, requiredMinor)) {
            //required OpenGL version not available
            error("Required OpenGL version not available, current version: " + glVersion.getMajorVersion() + "." + glVersion.getMajorVersion() + ", required version: " + requiredMajor + "." + requiredMinor);
        }

        //check OpenGL extensions
        String requiredExtensions = Config.get(SR_SECTION, "openGLExtensions");
        final String[] extensions = requiredExtensions.split(";");

        //check, if required extensions are available
        ThreadUtils.executeOnUIThreadAndWait(() -> {
            for (String extension : extensions) {
                if (extension.equals("none")) {
                    continue;
                }

                Log.d(OPENGL_TAG, "check OpenGL extension: " + extension);

                if (!Gdx.graphics.supportsExtension(extension)) {
                    error("Required OpenGL extension is not available: " + extension);
                }
            }
        });

        //check for updates
        checkForUpdates("Engine", Initializer.class);
        checkForUpdates("Game", Initializer.class);

        //check, which servers are available
        Log.i(SERVERS_TAG, "load server config");
        try {
            ServerManager.getInstance().loadFromConfig(new File(FilePath.parse("{data.dir}config/servers.json")));
        } catch (IOException e) {
            Log.e(SERVERS_TAG, "error while checking for online servers: ", e);
            JavaFXUtils.showExceptionDialog(I.tr("Server Error!"), "IOException: ", e);
            Gdx.app.exit();
        }

        Log.i(SERVERS_TAG, "" + ServerManager.getInstance().listServers().size() + " servers in configuration file found.");

        int onlineServerCount = 0;

        for (ServerManager.Server server : ServerManager.getInstance().listServers()) {
            if (server.online) {
                onlineServerCount++;
            }
        }

        Log.i(SERVERS_TAG, "" + onlineServerCount + " servers are currently online.");

        //initialize Cache
        String cacheDir = FilePath.parse(Config.get("Paths", "tempDir"));
        Log.i("Cache", "initialize cache: " + new File(cacheDir).getAbsolutePath());
        Cache.init(cacheDir);

        //initialize scripting engine
        Log.i(SCRIPTS_TAG, "initialize scripting engine...");
        ScriptEngine.init();

        Log.i(SCRIPTS_TAG, "load & execute init.lua script...");
        long startTime = System.currentTimeMillis();
        try {
            ScriptEngine.getInstance().loadFile(new File(FilePath.parse("{data.dir}init/scripts/init.lua")));
        } catch (ScriptLoadException e) {
            Log.e(SCRIPTS_TAG, "Exception while loading init.lua script file: ", e);
            JavaFXUtils.showExceptionDialog(I.tr("Error!"), "Exception: ", e);
            Gdx.app.exit();
        }

        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;
        Log.d(SCRIPTS_TAG, "init script execution took " + timeDiff + "ms.");
    }

    protected void error (String content) {
        //show error dialog
        JavaFXUtils.startJavaFX();
        JavaFXUtils.showErrorDialog(content);

        Gdx.app.exit();
    }

    protected void checkForUpdates (String name, Class<?> cls) {
        if (Config.getBool(UPDATE_TAG, "checkFor" + name + "Updates")) {
            Log.i(UPDATE_TAG, "Check for " + name.toLowerCase() + " updates now...");
            String updateUrl = Config.get(UPDATE_TAG, name.toLowerCase() + "UpdateUrl");

            try {
                String content = WebUtils.readContentFromWebsite(Config.get(UPDATE_TAG, name.toLowerCase() + "UpdateUrl"));
                JSONObject json = new JSONObject(content);

                Version version = new Version(cls);
                String onlineVersion = json.getString("full-version");

                //compare versions
                if (version.getFullVersion().equals(onlineVersion)) {
                    Log.i(UPDATE_TAG,  name + " is up to date!");
                } else {
                    Log.w(UPDATE_TAG, name + " isn't up to date! Current version: " + version.getFullVersion() + ", available version: " + onlineVersion);

                    if (Config.getBool(UPDATE_TAG, "warnOnOutdated" + name + "Version")) {
                        JavaFXUtils.showErrorDialog(I.tr("Updater"), name  + " version isn't up to date! Current version: " + version.getFullVersion() + ", available version: " + onlineVersion);
                    }
                }
            } catch (IOException e) {
                Log.w("Update", "Couldn't get update data from url: " + updateUrl, e);
            }
        }
    }

}
