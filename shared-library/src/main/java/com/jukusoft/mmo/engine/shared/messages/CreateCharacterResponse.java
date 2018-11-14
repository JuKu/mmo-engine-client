package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;

@MessageType(type = 0x01, extendedType = 0x05)
@ProtocolVersion(1)
public class CreateCharacterResponse implements SerializableObject {

    public enum CREATE_CHARACTER_RESULT {
        DUPLICATE_NAME, INVALIDE_NAME, SERVER_ERROR, CLIENT_ERROR, SUCCESS
    }

    @SInteger
    protected int resultCode = 0;

    public int getResultCode() {
        return resultCode;
    }

    public CREATE_CHARACTER_RESULT getResult () {
        return CREATE_CHARACTER_RESULT.values()[this.getResultCode()];
    }

    public void setResult(CREATE_CHARACTER_RESULT result) {
        this.resultCode = result.ordinal();
    }

}
