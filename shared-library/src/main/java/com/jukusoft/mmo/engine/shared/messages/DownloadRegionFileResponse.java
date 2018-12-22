package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.buffer.Buffer;

@MessageType(type = 0x02, extendedType = 0x02)
@ProtocolVersion(1)
public class DownloadRegionFileResponse implements SerializableObject {

    @SLong
    public long regionID = 0;

    @SInteger
    public int instanceID = 0;

    @SString(maxCharacters = Integer.MAX_VALUE)
    public String filePath = "";

    @SBuffer
    public Buffer content = Buffer.buffer(new byte[0]);

}
