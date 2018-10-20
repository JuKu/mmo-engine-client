package com.jukusoft.mmo.engine.shared.memory;

import com.jukusoft.mmo.engine.shared.events.EventData;

public class DummyOtherEventDataObject extends EventData {

    @Override
    public int getEventType() {
        return 2;
    }

    @Override
    public boolean allowTrigger() {
        return true;
    }
}
