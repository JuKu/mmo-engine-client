package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class LoginResponseEvent extends EventData {

    public enum LOGIN_RESPONSE {
        /**
         * network problems
         */
        NO_SERVER,

        /**
         * internal client error, e.q. on encryption
         */
        CLIENT_ERROR,

        /**
        * internal server error, e.q. on decryption
        */
        INTERNAL_SERVER_ERROR,

        WRONG_CREDENTIALS,

        SUCCESSFUL
    }

    public LOGIN_RESPONSE loginResponse = null;

    //public String username = "Guest";

    @Override
    public int getEventType() {
        return ClientEvents.LOGIN_RESPONSE.getID();
    }

}
