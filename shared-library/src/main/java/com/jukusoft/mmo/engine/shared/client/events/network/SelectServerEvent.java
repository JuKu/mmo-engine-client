package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class SelectServerEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.SELECT_SERVER.getID();
    }

}
