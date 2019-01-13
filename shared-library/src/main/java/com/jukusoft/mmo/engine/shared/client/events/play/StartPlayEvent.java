package com.jukusoft.mmo.engine.shared.client.events.play;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class StartPlayEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.START_PLAYING.getID();
    }
}
