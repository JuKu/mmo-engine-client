package com.jukusoft.mmo.engine.applayer.init;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.applayer.utils.ThreadUtils;
import com.jukusoft.mmo.engine.applayer.utils.Utils;

import java.util.concurrent.atomic.AtomicReference;

public class Initializer implements Runnable {

    protected final BaseApp app;

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
        ThreadUtils.executeOnUIThreadAndWait(() -> {
            vA.set(Gdx.graphics.getGLVersion());
        });

        GLVersion glVersion = vA.get();

        Utils.printSection("OpenGL");
        Log.i("OpenGL", "version: " + glVersion.getMajorVersion() + "." + glVersion.getMajorVersion());
        Log.i("OpenGL", "release version: " + glVersion.getReleaseVersion());
        Log.i("OpenGL", "vendor: " + glVersion.getVendorString());
        Log.i("OpenGL", "graphics card: " + glVersion.getRendererString());
        Log.i("OpenGL", "type: " + glVersion.getType().name());

        int requiredMajor = Config.getInt("SystemRequirements", "openGLMajor");
        int requiredMinor = Config.getInt("SystemRequirements", "openGLMinor");

        //check OpenGL version
        if (!glVersion.isVersionEqualToOrHigher(requiredMajor, requiredMinor)) {
            //required OpenGL version not available
            error("Required OpenGL version not available, current version: " + glVersion.getMajorVersion() + "." + glVersion.getMajorVersion() + ", required version: " + requiredMajor + "." + requiredMinor);
        }

        //check OpenGL extensions
        String requiredExtensions = Config.get("SystemRequirements", "openGLExtensions");
        final String[] extensions = requiredExtensions.split(";");

        //check, if required extensions are available
        ThreadUtils.executeOnUIThreadAndWait(() -> {
            for (String extension : extensions) {
                if (extension.equals("none")) {
                    continue;
                }

                if (!Gdx.graphics.supportsExtension(extension)) {
                    error("Required OpenGL extension is not available: " + extension);
                }
            }
        });

        //run GC to cleanup initialization process
        System.gc();
    }

    protected void error (String content) {
        //show error dialog
        JavaFXUtils.startJavaFX();
        JavaFXUtils.showErrorDialog(content);

        Gdx.app.exit();
    }

}
