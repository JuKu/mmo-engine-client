package com.jukusoft.mmo.engine.shared.client.events.load;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SJsonArray;
import com.jukusoft.vertx.serializer.annotations.SString;
import io.vertx.core.json.JsonArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
* event which is fired if gameserver sends a LoadMapResponse message with region to load
*/
public class LoadMapEvent extends EventData {

    //region coordinates
    public long regionID = 0;
    public int instanceID = 0;

    /**
     * region title
     */
    public String regionTitle = "";

    protected Map<String,String> requiredMapFiles = null;

    @Override
    public int getEventType() {
        return ClientEvents.LOAD_MAP.getID();
    }

    public void setRequiredMapFiles(Map<String, String> requiredMapFiles) {
        Objects.requireNonNull(requiredMapFiles);
        this.requiredMapFiles = requiredMapFiles;
    }
}
