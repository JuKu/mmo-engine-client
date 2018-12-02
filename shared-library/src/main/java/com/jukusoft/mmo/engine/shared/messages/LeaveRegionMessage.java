package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;
import com.jukusoft.vertx.serializer.annotations.SLong;

@MessageType(type = 0x01, extendedType = 0x08)
@ProtocolVersion(1)
public class LeaveRegionMessage implements SerializableObject {

    //user & character information
    @SInteger
    public int cid = -1;

    //next region to join
    @SLong
    public long regionID = 0;

    @SInteger
    public int instanceID = 0;

    @SInteger
    public int shardID = 0;

}
