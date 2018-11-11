package com.jukusoft.mmo.engine.shared.client.events.init;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.data.CharacterSlot;
import com.jukusoft.mmo.engine.shared.events.EventData;

import java.util.ArrayList;
import java.util.List;

public class CharacterListReceivedEvent extends EventData {

    public List<CharacterSlot> slots = new ArrayList<>();

    @Override
    public int getEventType() {
        return ClientEvents.CHARACTER_LIST_RECEIVED.getID();
    }

}
