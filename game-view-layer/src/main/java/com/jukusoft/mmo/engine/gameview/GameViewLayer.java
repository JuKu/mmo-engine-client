package com.jukusoft.mmo.engine.gameview;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.impl.DefaultScreenManager;

public class GameViewLayer implements SubSystem {

    protected ScreenManager<IScreen> screenManager = null;

    @Override
    public void onInit() {
        Log.i("GameView", "initialize game-view-layer.");

        //initialize screen manager
        screenManager = new DefaultScreenManager();
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
