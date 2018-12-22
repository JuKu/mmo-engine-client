package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;

@MessageType(type = 0x02, extendedType = 0x03)
@ProtocolVersion(1)
public class StartSyncGameStateRequest implements SerializableObject {

    //

}
