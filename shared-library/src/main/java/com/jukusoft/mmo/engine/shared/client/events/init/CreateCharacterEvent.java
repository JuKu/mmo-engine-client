package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.data.CharacterSlot;
import com.jukusoft.mmo.engine.shared.events.EventData;

public class CreateCharacterEvent extends EventData {

    public CharacterSlot character = null;

    @Override
    public int getEventType() {
        return ClientEvents.CREATE_CHARACTER.getID();
    }

}
