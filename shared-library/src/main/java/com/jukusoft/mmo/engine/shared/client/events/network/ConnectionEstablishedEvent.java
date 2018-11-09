package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired, if network connection to proxy server was successfully
*/
public class ConnectionEstablishedEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.CONNECTION_ESTABLISHED.getID();
    }

}
