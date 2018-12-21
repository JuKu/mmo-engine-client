package com.jukusoft.mmo.engine.shared.messages;

import com.jukusoft.vertx.serializer.SerializableObject;
import com.jukusoft.vertx.serializer.annotations.MessageType;
import com.jukusoft.vertx.serializer.annotations.ProtocolVersion;
import com.jukusoft.vertx.serializer.annotations.SJsonArray;
import io.vertx.core.json.JsonArray;
import org.mini2Dx.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;

/**
* message which is sended from client to gameserver to request some invalide files in cache to download them.
 * E.q. to request download of tmx maps for this region.
*/
@MessageType(type = 0x02, extendedType = 0x02)
@ProtocolVersion(1)
public class DownloadRegionFilesRequest implements SerializableObject, Pool.Poolable {

    @SJsonArray
    protected JsonArray requestedRegionFiles = new JsonArray();

    public void addFileRequest(String fileName) {
        this.requestedRegionFiles.add(fileName);
    }

    public void addFiles (List<String> fileList) {
        for (String fileName : fileList) {
            this.requestedRegionFiles.add(fileName);
        }
    }

    public List<String> listRequestedFiles() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < this.requestedRegionFiles.size(); i++) {
            //add filename to list
            list.add(this.requestedRegionFiles.getString(i));
        }

        return list;
    }

    @Override
    public void reset() {
        this.requestedRegionFiles.clear();
    }
    
}
