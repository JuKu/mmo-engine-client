package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.*;
import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * message which is sended from proxy server to gameserver to join player
 */
@MessageType(type = 0x01, extendedType = 0x07)
@ProtocolVersion(1)
public class JoinRegionMessage implements SerializableObject {

    //cluster credentials
    @SString(maxCharacters = Integer.MAX_VALUE)
    protected String cluster_username = "";

    @SString(maxCharacters = Integer.MAX_VALUE)
    protected String cluster_password = "";

    //user & character information
    @SInteger
    public int cid = -1;

    @SInteger
    public int userID = -1;

    @SString(maxCharacters = 64)
    public String username = "";

    @SString(maxCharacters = Integer.MAX_VALUE)
    protected String groupsJson = "";

    //region information
    @SLong
    public long regionID = 0;

    @SInteger
    public int instanceID = 0;

    @SInteger
    public int shardID = 0;

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

}
