package com.jukusoft.mmo.engine.shared.map.tileset;

import com.carrotsearch.hppc.ObjectArrayList;

import java.io.File;

public class TextureTileset extends Tileset {

    protected ObjectArrayList<TilesetImage> images = new ObjectArrayList<>();

    public TextureTileset (int firstTileID, String name, int tilesetTileWidth, int tilesetTileHeight, int tileCount, int columns) {
        super(firstTileID, firstTileID + tileCount - 1);//-1 because first tile counts also
    }

    public void addImage (String source, int width, int height, int firstTileID, int tilesetTileWidth, int tilesetTileHeight, int tileCount, int columns) {
        //check, if file exists
        if (!new File(source).exists()) {
            throw new IllegalArgumentException("source file doesnt exists: " + source);
        }

        this.images.add(new TilesetImage(source, width, height, firstTileID, tilesetTileWidth, tilesetTileHeight, tileCount, columns));
    }

    public ObjectArrayList<TilesetImage> listTextures () {
        return this.images;
    }

    public static class TilesetImage {

        public final String source;
        public final int width;
        public final int height;
        public final int firstTileID;
        public final int tilesetTileWidth;
        public final int tilesetTileHeight;
        public final int tileCount;
        public final int columns;

        public TilesetImage (String source, int width, int height, int firstTileID, int tilesetTileWidth, int tilesetTileHeight, int tileCount, int columns) {
            this.source = source;
            this.width = width;
            this.height = height;
            this.firstTileID = firstTileID;
            this.tilesetTileWidth = tilesetTileWidth;
            this.tilesetTileHeight = tilesetTileHeight;
            this.tileCount = tileCount;
            this.columns = columns;
        }

    }

}
