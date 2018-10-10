package com.jukusoft.mmo.engine.gameview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.badlogic.gdx.math.Vector3;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.applayer.utils.PlatformUtils;
import com.jukusoft.mmo.engine.gameview.mapping.MappingGenerator;

import java.io.File;
import java.io.IOException;

public class InputLayer implements SubSystem {

    protected static final String CONTROLLER_TAG = "Controller";

    @Override
    public void onInit() {
        Log.i("Input", "initializing input devices");

        if (Config.getBool(CONTROLLER_TAG, "enabled")) {
            //check for connected controllers
            int connectedControllers = 0;

            for (Controller controller : Controllers.getControllers()) {
                Log.i(CONTROLLER_TAG, "controller detected: " + controller.getName());
                connectedControllers++;

                //initialize controller
                this.initController(controller);
            }

            Log.i(CONTROLLER_TAG, connectedControllers + " controller(s) detected.");

            Controllers.addListener(new ControllerAdapter() {
                @Override
                public void connected(Controller controller) {
                    Log.i(CONTROLLER_TAG, "new controller detected: " + controller.getName());
                    initController(controller);
                }
            });
        } else {
            Log.i(CONTROLLER_TAG, "controller support is disabled.");
        }
    }

    @Override
    public void onGameloop() {
        //
    }

    @Override
    public void onShutdown() {
        //
    }

    protected void initController (Controller controller) {
        controller.addListener(new ControllerAdapter() {

            @Override
            public void disconnected(Controller controller) {
                Log.i(CONTROLLER_TAG, "controller disconnected: " + controller.getName());
            }

        });

        //search for mapping
        String osName = PlatformUtils.getType().name();

        //to lower case, except first character
        osName = osName.substring(0, 1).toUpperCase() + osName.substring(1).toLowerCase();

        String name = controller.getName().replace(" ", "_");
        String mappingFile = FilePath.parse("{data.dir}input/mappings/" + name.toLowerCase() + ".ini");

        if (!new File(mappingFile).exists()) {
            Log.w(CONTROLLER_TAG, "mapping file for controller doesn't exists! search path: " + mappingFile);

            if (Config.getBool(CONTROLLER_TAG, "autoGenerateMapping")) {
                Log.i(CONTROLLER_TAG, "auto generate mapping file now: " + mappingFile);

                try {
                    MappingGenerator.generateDefaultMapping(new File(mappingFile), name);
                } catch (IOException e) {
                    Log.w(CONTROLLER_TAG, "Couldn't generate auto mapping for controller!", e);
                    JavaFXUtils.showExceptionDialog(I.tr("Error!"), I.tr("Couldn't generate auto mapping for controller!"), e);
                }
            } else {
                Log.w(CONTROLLER_TAG, "auto generating of controller mappings is disabled.");
                JavaFXUtils.showErrorDialog(I.tr("Error!"), I.tr("Controller unbekannt! Für diesen Controller existieren derzeit keine Mappings!"));
            }
        }
    }

}
