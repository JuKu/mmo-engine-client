package com.jukusoft.mmo.engine.shared.memory;

import com.jukusoft.mmo.engine.shared.events.EventData;

public class DummyEventDataObject extends EventData {

    @Override
    public int getEventType() {
        return 1;
    }

}
