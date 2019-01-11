package com.jukusoft.mmo.engine.shared.region;

import com.jukusoft.mmo.engine.shared.utils.FileUtils;
import com.jukusoft.mmo.engine.shared.utils.MathUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegionInfo {

    protected String title = "";

    //tileset dimensions
    protected int tileWidth = 0;
    protected int tileHeight = 0;

    protected List<RegionMap> maps = null;

    public RegionInfo() {
        //
    }

    public void load (File file) throws IOException {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            throw new FileNotFoundException("region info file (by default region.json) doesn't exists: " + file.getAbsolutePath());
        }

        String content = FileUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        JsonObject json = new JsonObject(content);

        this.load(json, file.getParentFile().getAbsolutePath() + "/");
    }

    protected void load (JsonObject json, String dir) throws IOException {
        this.title = json.getString("title");
        this.maps = new ArrayList<>();

        //parse properties
        JsonObject propJson = json.getJsonObject("properties");
        Objects.requireNonNull(propJson, "no properties are set.");

        this.tileWidth = propJson.getInteger("tile_width");
        this.tileHeight = propJson.getInteger("tile_height");

        if (this.tileWidth <= 0) {
            throw new IllegalStateException("tile width cannot <= 0 (current value: " + this.tileWidth + ")!");
        }

        if (this.tileHeight <= 0) {
            throw new IllegalStateException("tile height cannot <= 0 (current value: " + this.tileHeight + ")!");
        }

        JsonArray mapsArray = json.getJsonArray("maps");

        if (mapsArray.size() == 0) {
            throw new IllegalStateException("no map is specified in region.json!");
        }

        for (int i = 0; i < mapsArray.size(); i++) {
            JsonObject mapJson = mapsArray.getJsonObject(i);

            String file = mapJson.getString("file");
            int absX = mapJson.getInteger("abs_x");
            int absY = mapJson.getInteger("abs_y");
            int widthInTiles = mapJson.getInteger("width_in_tiles");
            int heightInTiles = mapJson.getInteger("width_in_tiles");

            //check, if map name ends with ".tmx"
            if (!file.endsWith(".tmx")) {
                throw new IllegalStateException("map file has to end with '.tmx', current map filename: " + file);
            }

            //check, if tmx map file exists
            if (!new File(dir + file).exists()) {
                throw new FileNotFoundException("map file doesn't exists: " + dir + file);
            }

            RegionMap map = new RegionMap(file, absX, absY, widthInTiles, heightInTiles);
            this.maps.add(map);
        }

        //check, if some maps are overlapping (then we cannot really draw them both and in most cases this isn't wanted)
        for (RegionMap map : this.maps) {
            for (RegionMap map1 : this.maps) {
                if (map.equals(map1)) {
                    continue;
                }

                if (MathUtils.overlapping(map.absX, map.absX + (map.widthInTiles * this.tileWidth), map.absY, map.absY + (map.heightInTiles * this.tileHeight))) {
                    throw new IllegalStateException("some maps in region '" + dir + "' are overlapping!");
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public List<RegionMap> listMaps() {
        return maps;
    }
}
