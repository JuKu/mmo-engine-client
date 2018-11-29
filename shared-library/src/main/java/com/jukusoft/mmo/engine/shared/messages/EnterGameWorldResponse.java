package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.json.JsonArray;

import java.util.*;

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

    @SInteger
    public int cid = 0;

    @SString(maxCharacters = 64)
    public String username = "";

    @SString(maxCharacters = Integer.MAX_VALUE)
    protected String groupsJson = "";

    /**
    * set groups and convert them to internal json array to use for serializer
     *
     * @param groups list with all permission group names user belongs to
    */
    public void setGroups (List<String> groups) {
        JsonArray jsonArray = new JsonArray();

        for (String groupName : groups) {
            jsonArray.add(groupName);
        }

        this.groupsJson = jsonArray.encode();
    }

    /**
    * converts json string to list with groups and returns them
     *
     * @return list with all permission group names user belongs to
    */
    public List<String> listGroups () {
        //check, if enter game world was successfully, else these attributes aren't available
        throwExceptionIfNotSuccess();

        Objects.requireNonNull(this.groupsJson);

        if (this.groupsJson.isEmpty()) {
            throw new IllegalStateException("groupsJson is empty.");
        }

        JsonArray jsonArray = new JsonArray(groupsJson);
        List<String> groups = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            groups.add(jsonArray.getString(i));
        }

        return groups;
    }

    protected void throwExceptionIfNotSuccess () {
        if (this.getResult() != RESULT_CODE.SUCCESS) {
            throw new IllegalStateException("Cannot use this method, because attribute is only available on SUCCESS.");
        }
    }

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
