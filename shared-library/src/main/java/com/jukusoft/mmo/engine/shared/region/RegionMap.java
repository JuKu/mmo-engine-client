package com.jukusoft.mmo.engine.shared.region;

import com.jukusoft.mmo.engine.shared.utils.MathUtils;

import java.util.Objects;

public class RegionMap {

    //map filename
    public final String file;

    //absolute position
    public final int absX;
    public final int absY;

    //dimension
    public final int widthInTiles;
    public final int heightInTiles;

    //tileset dimensions
    protected int tileWidth = 0;
    protected int tileHeight = 0;

    public RegionMap (String file, int absX, int absY, int widthInTiles, int heightInTiles, int tileWidth, int tileHeight) {
        Objects.requireNonNull(file);

        this.file = file;
        this.absX = absX;
        this.absY = absY;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
    * check, if player / object stands on this map
     *
     * @return true, if player / object is in this map
    */
    public boolean isPointInnerMap (float objX, float objY) {
        int width = widthInTiles * tileWidth;
        int height = heightInTiles * tileHeight;

        return MathUtils.overlapping(absX, absX + width, objX, objX) &&
                MathUtils.overlapping(absY, absY + height, objY, objY);
    }

}
