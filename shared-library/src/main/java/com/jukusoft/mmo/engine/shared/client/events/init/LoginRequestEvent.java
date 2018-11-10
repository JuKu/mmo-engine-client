package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class LoginRequestEvent extends EventData {

    //events are like value objects or structs in C / C++
    public String username = "";
    public String password = "";

    @Override
    public int getEventType() {
        return ClientEvents.LOGIN_REQUEST.getID();
    }

}
