package com.jukusoft.mmo.engine.logic;

import com.jukusoft.mmo.engine.logic.region.RegionLoaderImpl;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.region.RegionLoader;

public class GameLogicLayer implements SubSystem {

    protected boolean paused = false;

    protected RegionLoader regionLoader = null;

    @Override
    public void onInit() {
        Log.i("Game Logic", "initialize game-logic-layer.");

        this.regionLoader = new RegionLoaderImpl();

        //add listener to receive events for loading regions
        Events.addListener(Events.LOGIC_THREAD, ClientEvents.ALL_MAP_SPECIFIC_DATA_RECEIVED, this.regionLoader);
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
