package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;

@MessageType(type = 0x01, extendedType = 0x03)
@ProtocolVersion(1)
public class LoginResponse implements SerializableObject {

    @SInteger
    public int userID = 0;

    @SString(maxCharacters = 20)
    public String username = "";

}
