package com.jukusoft.mmo.engine.shared.client.events.load.ready;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired when game logic layer has loaded game world (region) and is ready to play
*/
public class GameViewLayerReadyEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.GAME_VIEW_LAYER_READY.getID();
    }

}
