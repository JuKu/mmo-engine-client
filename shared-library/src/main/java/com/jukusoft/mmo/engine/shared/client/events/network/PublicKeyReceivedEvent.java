package com.jukusoft.mmo.engine.shared.client.events.network;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired if public key was received from proxy server
*/
public class PublicKeyReceivedEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.PUBLIC_KEY_RECEIVED.getID();
    }

}
