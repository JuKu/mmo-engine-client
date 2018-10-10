package com.jukusoft.mmo.engine.logic;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;

public class GameLogicLayer implements SubSystem {

    protected boolean paused = false;

    @Override
    public void onInit() {
        Log.i("Game Logic", "initialize game-logic-layer.");
    }

    @Override
    public void onGameloop() {
        if (this.paused) {
            //we dont update game, if game was paused
            return;
        }

        //TODO: add code here
    }

    @Override
    public void onShutdown() {
        //
    }

}
