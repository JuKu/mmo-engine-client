package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class PingChangedEvent extends EventData {

    public int ping = 0;

    @Override
    public int getEventType() {
        return ClientEvents.PING_CHANGED.getID();
    }

}
