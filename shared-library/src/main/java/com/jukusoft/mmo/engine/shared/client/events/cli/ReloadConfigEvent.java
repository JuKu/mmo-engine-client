package com.jukusoft.mmo.engine.shared.client.events.cli;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired if config was reloaded at runtime (e.q. with CLI command)
*/
public class ReloadConfigEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.RELOAD_CONFIG.getID();
    }

}
