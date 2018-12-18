package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class AllSubSystemsInitializedEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.ALL_SUBSYSTEMS_READY.getID();
    }

}
