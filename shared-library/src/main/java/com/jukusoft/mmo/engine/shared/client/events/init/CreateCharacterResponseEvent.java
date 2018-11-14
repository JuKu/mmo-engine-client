package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.messages.CreateCharacterResponse;

public class CreateCharacterResponseEvent extends EventData {

    public CreateCharacterResponse.CREATE_CHARACTER_RESULT resultCode = null;

    @Override
    public int getEventType() {
        return ClientEvents.CREATE_CHARACTER_RESPONSE.getID();
    }

}
