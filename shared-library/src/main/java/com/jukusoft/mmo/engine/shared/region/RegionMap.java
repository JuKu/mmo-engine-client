package com.jukusoft.mmo.engine.shared.region;

public class RegionMap {

    //map filename
    public final String file;

    //absolute position
    public final int absX;
    public final int absY;

    //dimension
    public final int widthInTiles;
    public final int heightInTiles;

    public RegionMap (String file, int absX, int absY, int widthInTiles, int heightInTiles) {
        this.file = file;
        this.absX = absX;
        this.absY = absY;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
    }

}
