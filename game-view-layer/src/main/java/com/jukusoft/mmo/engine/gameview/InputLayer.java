package com.jukusoft.mmo.engine.gameview;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;

public class InputLayer implements SubSystem {

    @Override
    public void onInit() {
        Log.i("Input", "initializing input devices");
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
