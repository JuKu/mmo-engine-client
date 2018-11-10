package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;

/**
 * message to send a request to proxy server to get RTT (round trip time) to detect ping
 */
@MessageType(type = 0x01, extendedType = 0x02)
@ProtocolVersion(1)
public class RTTRequest implements SerializableObject {

    //

}
