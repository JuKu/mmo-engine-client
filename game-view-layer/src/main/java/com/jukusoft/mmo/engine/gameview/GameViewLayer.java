package com.jukusoft.mmo.engine.gameview;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;

public class GameViewLayer implements SubSystem {

    @Override
    public void onInit() {
        Log.i("GameView", "initialize game-view-layer.");
    }

    @Override
    public void onGameloop() {

    }

    @Override
    public void onShutdown() {

    }

}
