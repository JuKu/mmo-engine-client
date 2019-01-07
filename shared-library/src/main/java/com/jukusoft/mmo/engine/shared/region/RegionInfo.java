package com.jukusoft.mmo.engine.shared.region;

import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RegionInfo {

    protected String title = "";

    public RegionInfo() {
        //
    }

    public void load (File file) throws IOException {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            throw new IOException("region info file (by default region.json) doesn't exists: " + file.getAbsolutePath());
        }

        String content = FileUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        JsonObject json = new JsonObject(content);

        this.load(json);
    }

    protected void load (JsonObject json) {
        this.title = json.getString("title");
    }

}
