package com.jukusoft.mmo.engine.applayer.utils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.jukusoft.mmo.client.engine.cache.Cache;
import com.jukusoft.mmo.client.engine.logging.LocalLogger;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TexturePackerHelper {

    protected TexturePackerHelper () {
        //
    }

    public static void packTextures (File configFile) throws IOException {
        if (configFile == null) {
            throw new NullPointerException("config file cannot be null.");
        }

        if (!configFile.exists()) {
            throw new FileNotFoundException("config file doesnt exists: " + configFile.getAbsolutePath());
        }

        String content = FileUtils.readFile(configFile.getAbsolutePath(), StandardCharsets.UTF_8);
        JsonObject json = new JsonObject(content);
        JsonArray array = json.getJsonArray("packs");

        TexturePacker.Settings settings = new TexturePacker.Settings();

        for (int i = 0; i < array.size(); i++) {
            JsonObject pack = array.getJsonObject(i);
            String title = pack.getString("title");
            String sourceDir = pack.getString("source_dir");
            String targetDir = Cache.getInstance().getPath() + pack.getString("target_dir");
            String packName = pack.getString("pack_name");

            LocalLogger.print("pack texture '" + title + "' with name '" + packName + "'...");

            if (new File(targetDir + "/" + packName + ".atlas").exists()) {
                LocalLogger.print("Dont pack '" + title + "' because texture pack already exists.");
            } else {
                LocalLogger.print("pack '" + title + "'...");

                //pack textures
                TexturePacker.process(settings, sourceDir, targetDir, packName);
            }
        }
    }

}
