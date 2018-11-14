package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;

@MessageType(type = 0x01, extendedType = 0x06)
@ProtocolVersion(1)
public class EnterGameWorldRequest implements SerializableObject {

    @SInteger
    public int cid = 0;

}
