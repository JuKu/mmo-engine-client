package com.jukusoft.mmo.engine.gameview;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.utils.FilePath;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.gameview.input.InputManager;
import com.jukusoft.mmo.engine.gameview.input.KeyboardInputProcessor;
import com.jukusoft.mmo.engine.gameview.input.controller.ControllerMapper;
import com.jukusoft.mmo.engine.gameview.input.controller.MappingGenerator;
import com.jukusoft.mmo.engine.shared.client.events.input.PlayerMoveEvent;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

import java.io.File;
import java.io.IOException;

public class InputLayer implements SubSystem {

    protected static final String CONTROLLER_TAG = "Controller";
    protected InputManager manager = null;
    protected KeyboardInputProcessor keyboardInputProcessor = null;

    //player input
    protected final Vector3 playerMoveDirection = new Vector3(0, 0, 1);//z coordinate is movement speed in percent (0 - 1)

    //vector to calculate speed
    protected final Vector2 tmpVec = new Vector2(0, 0);

    //initialization flag
    protected boolean initialized = false;

    @Override
    public void onInit() {
        Log.i("Input", "initializing input devices");

        //initialize controllers
        this.initControllers();

        //get singleton instance of input manager and initialize it
        this.manager = InputManager.getInstance();
        this.manager.setGdxInputProcessor();

        //create and set game input processor
        this.keyboardInputProcessor = new KeyboardInputProcessor(this.playerMoveDirection);
        this.keyboardInputProcessor.enable();

        //add input processor for game logic
        this.manager.add(this.keyboardInputProcessor);

        this.initialized = true;
    }

    @Override
    public void onGameloop() {
        if (!this.initialized) {
            return;
        }

        //calculate speed
        this.tmpVec.set(playerMoveDirection.x, playerMoveDirection.y);
        float lengthVec = this.tmpVec.len();

        //max length in circle is 1
        lengthVec = lengthVec > 1f ? 1f : lengthVec;

        //set speed
        playerMoveDirection.z = lengthVec;

        //create new event from memory pool
        PlayerMoveEvent event = Pools.get(PlayerMoveEvent.class);
        event.x = this.playerMoveDirection.x;
        event.y = this.playerMoveDirection.y;
        event.speed = (event.x != 0 || event.y != 0) ? this.playerMoveDirection.z : 0f;

        //fire event
        Events.queueEvent(event);
    }

    @Override
    public void onShutdown() {
        Log.i("Input", "shutdown input layer");

        //disable input processor, so input doesn't have any effect anymore
        this.keyboardInputProcessor.disable();

        //clear all input processors
        this.manager.clear();
    }

    protected void initControllers () {
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

    protected void initController (Controller controller) {
        //search for keyDownMapping
        String name = controller.getName().replace(" ", "_");
        String mappingFile = FilePath.parse("{data.dir}input/mappings/" + name.toLowerCase() + ".ini");

        if (!new File(mappingFile).exists()) {
            Log.w(CONTROLLER_TAG, "keyDownMapping file for controller doesn't exists! search path: " + mappingFile);

            if (Config.getBool(CONTROLLER_TAG, "autoGenerateMapping")) {
                Log.i(CONTROLLER_TAG, "auto generate keyDownMapping file now: " + mappingFile);

                try {
                    MappingGenerator.generateDefaultMapping(new File(mappingFile), name);
                } catch (IOException e) {
                    Log.w(CONTROLLER_TAG, "Couldn't generate auto keyDownMapping for controller!", e);
                    JavaFXUtils.showExceptionDialog(I.tr("Error!"), I.tr("Couldn't generate auto keyDownMapping for controller!"), e);
                }
            } else {
                Log.w(CONTROLLER_TAG, "auto generating of controller mappings is disabled.");
                JavaFXUtils.showErrorDialog(I.tr("Error!"), I.tr("Unknown controller! There doesn't exists any mappings for this controller, yet!"));

                return;
            }
        }

        //load keyDownMapping
        try {
            Config.load(new File(mappingFile));
        } catch (IOException e) {
            Log.e(CONTROLLER_TAG, "Coulnd't load keyDownMapping for controller '" + controller.getName() + "'!");
            JavaFXUtils.showExceptionDialog(I.tr("Error!"), I.tr("Coulnd't load keyDownMapping for controller!"), e);
        }

        //create new controller mapper
        ControllerMapper controllerMapper = null;

        //load controller
        try {
            controllerMapper = new ControllerMapper(this.playerMoveDirection, new File(mappingFile));
        } catch (IOException e) {
            Log.w(CONTROLLER_TAG, "IOException while loading controller mapping: ", e);
        }

        final ControllerMapper controllerMapper1 = controllerMapper;

        //https://github.com/MrStahlfelge/gdx-controllerutils/tree/master/core-mapping

        controller.addListener(new ControllerAdapter() {
            @Override
            public void disconnected(Controller controller) {
                Log.i(CONTROLLER_TAG, "controller disconnected: " + controller.getName());

                //dispose controller mapper
                controllerMapper1.dispose();
            }
        });

        //add controller mapper
        controller.addListener(controllerMapper);
    }

}
