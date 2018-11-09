package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired if proxy server connection is ready (this means connection is established and RSA public key was received from client)
*/
public class ConnectionReadyEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.CONNECTION_READY.getID();
    }

}
