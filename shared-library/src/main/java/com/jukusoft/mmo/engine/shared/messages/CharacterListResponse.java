package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SString;

@MessageType(type = 0x01, extendedType = 0x04)
@ProtocolVersion(1)
public class CharacterListResponse implements SerializableObject {

    @SString(maxCharacters = Integer.MAX_VALUE)
    public String jsonStr = "";

}
