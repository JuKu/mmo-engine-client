package com.jukusoft.mmo.engine.gameview;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;

public class InputLayer implements SubSystem {

    @Override
    public void onInit() {
        Log.i("Input", "initializing input devices");

        //check for connected controllers
        int connectedControllers = 0;

        for (Controller controller : Controllers.getControllers()) {
            Log.i("Controller", "controller detected: " + controller.getName());
            connectedControllers++;
        }

        Log.i("Controller", connectedControllers + " controller(s) detected.");
    }

    @Override
    public void onGameloop() {
        //
    }

    @Override
    public void onShutdown() {
        //
    }

}
