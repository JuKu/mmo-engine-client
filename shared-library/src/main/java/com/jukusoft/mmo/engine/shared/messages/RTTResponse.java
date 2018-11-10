package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;

/**
 * RTT (round trip time) response message from proxy server to client to detect ping
 */
@MessageType(type = 0x01, extendedType = 0x02)
@ProtocolVersion(1)
public class RTTResponse implements SerializableObject {

    //

}
