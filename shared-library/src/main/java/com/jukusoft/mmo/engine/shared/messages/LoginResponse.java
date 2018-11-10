package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;

@MessageType(type = 0x01, extendedType = 0x03)
@ProtocolVersion(1)
public class LoginResponse implements SerializableObject {

    @SInteger
    protected int userID = 0;

    @SBytes
    protected byte[] usernameEncrypted = new byte[0];

}
