package com.jukusoft.mmo.engine.shared.messages.play;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;

@MessageType(type = 0x02, extendedType = 0x04)
@ProtocolVersion(1)
public class ClientReadyToPlayRequest implements SerializableObject {
}
