package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
* message which is sended from gameserver to client, so client should load map and switch to map loading screen
*/
@MessageType(type = 0x02, extendedType = 0x01)
@ProtocolVersion(1)
public class LoadMapResponse {

    @SLong
    public long regionID = 0;

    @SInteger
    public int instanceID = 0;

    /**
    * region name
    */
    @SString(maxCharacters = 64)
    public String regionTitle = "";

    @SJsonArray
    protected JsonArray requiredMapFiles = new JsonArray();

    public void addRequiredMap (String fileName, String checksum) {
        JsonObject json = new JsonObject();
        json.put("filename", fileName);
        json.put("checksum", checksum);

        this.requiredMapFiles.add(json);
    }

}
