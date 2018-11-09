package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired, if network connection to proxy server has failed
*/
public class ConnectionFailedEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.CONNECTION_FAILED.getID();
    }

}
