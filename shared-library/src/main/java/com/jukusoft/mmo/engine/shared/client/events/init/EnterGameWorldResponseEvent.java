package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.messages.EnterGameWorldResponse;

import java.util.List;

public class EnterGameWorldResponseEvent extends EventData {

    public EnterGameWorldResponse.RESULT_CODE resultCode = null;
    public int cid = 0;
    public String username = "";
    public List<String> groups = null;

    @Override
    public int getEventType() {
        return ClientEvents.ENTERED_GAME_WORLD.getID();
    }

}
