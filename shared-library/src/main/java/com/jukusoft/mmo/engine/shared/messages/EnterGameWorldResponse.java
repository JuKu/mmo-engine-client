package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SInteger;

import java.util.Arrays;
import java.util.Optional;

@MessageType(type = 0x01, extendedType = 0x06)
@ProtocolVersion(1)
public class EnterGameWorldResponse implements SerializableObject {

    public enum RESULT_CODE {
        /**
        * character doesn't exists
        */
        CHARACTER_NOT_EXISTS(1),

        CHARACTER_DOESNT_BELONGS_TO_USER(2),

        /**
        * internal server error
        */
        SERVER_ERROR(3),

        /**
        * user doesn't have ldap group / permission to play game
        */
        NO_PERMISSION_TO_PLAY(4),

        /**
        * character was confirmed, can enter loading screen now
        */
        SUCCESS(5);

        private final int code;

        RESULT_CODE (final int code) {
            this.code = code;
        }

        public int getCodeID() {
            return code;
        }

    }

    @SInteger
    protected int resultCodeID = 0;

    public RESULT_CODE getResult() {
        return valueOf(this.resultCodeID);
    }

    public void setResult(RESULT_CODE result) {
        this.resultCodeID = result.getCodeID();
    }

    public static RESULT_CODE valueOf(int value) {
        Optional<RESULT_CODE> opt = Arrays.stream(RESULT_CODE.values())
                .filter(code -> code.getCodeID() == value)
                .findFirst();

        if (opt.isPresent()) {
            return opt.get();
        } else {
            return null;
        }
    }

}
