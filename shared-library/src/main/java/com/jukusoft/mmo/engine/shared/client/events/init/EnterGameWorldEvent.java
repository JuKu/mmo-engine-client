package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired if player has selected a character and tries to enter the game world (--&gt; show loading screen after server acknowlegded)
*/
public class EnterGameWorldEvent extends EventData {

    public int cid = 0;

    @Override
    public int getEventType() {
        return ClientEvents.ENTER_GAME_WORLD.getID();
    }

}
