package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.json.JsonObject;
import org.mini2Dx.gdx.utils.Pool;

@MessageType(type = 0x02, extendedType = 0x03)
@ProtocolVersion(1)
public class StartSyncGameStateResponse implements SerializableObject, Pool.Poolable {

    //current character position (independent from sector)
    @SFloat
    public float posX = 0;

    @SFloat
    public float posY = 0;

    @SFloat
    public float posZ = 0;

    @SLong
    public long currentServerTime = 0;

    //json string with all static objects of current sector
    @SJsonObject
    public JsonObject staticObjects = new JsonObject();

    //data like weather state, lighting and so on
    @SJsonObject
    public JsonObject currentGameWorldData = new JsonObject();

    @Override
    public void reset() {
        this.staticObjects.clear();
        this.currentGameWorldData.clear();
    }

}
