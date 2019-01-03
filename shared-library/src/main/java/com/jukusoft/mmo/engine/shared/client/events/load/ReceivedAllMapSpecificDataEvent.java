package com.jukusoft.mmo.engine.shared.client.events.load;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;
import io.vertx.core.json.JsonObject;

/**
* event which is fired if map sync is ready to load map
*/
public class ReceivedAllMapSpecificDataEvent extends EventData {

    //directory where all cached region files like .tmx maps are stored, ends with "/"
    public String regionDir = "";

    //current character position
    public float posX = 0;
    public float posY = 0;
    public float posZ = 0;

    //current server time in milliseconds
    public long currentServerTime = 0;

    //json string with all static objects of current sector
    public JsonObject staticObjects = new JsonObject();

    //data like weather state, lighting and so on
    public JsonObject currentGameWorldData = new JsonObject();

    @Override
    public int getEventType() {
        return ClientEvents.ALL_MAP_SPECIFIC_DATA_RECEIVED.getID();
    }

}
