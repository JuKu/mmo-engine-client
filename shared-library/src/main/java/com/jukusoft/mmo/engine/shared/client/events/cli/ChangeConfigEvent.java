package com.jukusoft.mmo.engine.shared.client.events.cli;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
 * event which is fired if config was changed
 */
public class ChangeConfigEvent extends EventData {

    protected String section = "";
    protected String key = "";
    protected String value = "";

    public String getSection() {
        return section;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void set (String section, String key, String value) {
        this.section = section;
        this.key = key;
        this.value = value;
    }

    @Override
    public int getEventType() {
        return ClientEvents.CHANGE_CONFIG_VALUE.getID();
    }

}
