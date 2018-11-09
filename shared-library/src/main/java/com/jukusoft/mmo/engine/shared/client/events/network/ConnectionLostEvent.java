package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired, if connection to proxy server was lost
*/
public class ConnectionLostEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.CONNECTION_LOST.getID();
    }

}
