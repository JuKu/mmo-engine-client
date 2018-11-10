package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SBytes;

@MessageType(type = 0x01, extendedType = 0x03)
@ProtocolVersion(1)
public class LoginRequest implements SerializableObject {

    @SBytes
    public byte[] encryptedData = new byte[0];

}
