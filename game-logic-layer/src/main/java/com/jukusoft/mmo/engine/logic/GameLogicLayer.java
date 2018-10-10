package com.jukusoft.mmo.engine.logic;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;

public class GameLogicLayer implements SubSystem {

    @Override
    public void onInit() {
        Log.i("Game Logic", "initialize game-logic-layer.");
    }

    @Override
    public void onGameloop() {

    }

    @Override
    public void onShutdown() {

    }

}
