package com.jukusoft.mmo.engine.applayer.utils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.jukusoft.mmo.engine.applayer.config.Cache;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TexturePackerHelper {

    protected static final String TEXTURE_PACKER_TAG = "TexturePacker";

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
        JSONObject json = new JSONObject(content);
        JSONArray array = json.getJSONArray("packs");

        TexturePacker.Settings settings = new TexturePacker.Settings();

        for (int i = 0; i < array.length(); i++) {
            JSONObject pack = array.getJSONObject(i);
            String title = pack.getString("title");
            String sourceDir = pack.getString("source_dir");
            String targetDir = Cache.getInstance().getPath() + pack.getString("target_dir");
            String packName = pack.getString("pack_name");

            Log.i(TEXTURE_PACKER_TAG, "pack texture '" + title + "' with name '" + packName + "'...");

            if (new File(targetDir + "/" + packName + ".atlas").exists()) {
                Log.d(TEXTURE_PACKER_TAG, "Dont pack '" + title + "' because texture pack already exists.");
            } else {
                Log.d(TEXTURE_PACKER_TAG, "pack '" + title + "'...");

                //pack textures
                TexturePacker.process(settings, sourceDir, targetDir, packName);
            }
        }
    }

}
