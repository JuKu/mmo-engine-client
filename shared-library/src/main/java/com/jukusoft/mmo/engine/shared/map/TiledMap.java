package com.jukusoft.mmo.engine.shared.map;

import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;

import java.util.List;

public class TiledMap {

    protected Orientation orientation = null;

    /**
    * width: The map width in tiles.
     * height: The map height in tiles.
     * tilewidth: The width of a tile.
     * tileheight: The height of a tile.
     *
     * The tilewidth and tileheight properties determine the general grid size of the map.
     * The individual tiles may have different sizes.
     * Larger tiles will extend at the top and right (anchored to the bottom left).
    */
    protected int widthInTiles = 0;
    protected int heightInTiles = 0;
    protected int tileWidth = 0;
    protected int tileHeight = 0;

    //background color
    protected String bgColor = "";

    /**
     * from tmx format documentation:
     *
     * Stores the next available ID for new layers. This number is stored to prevent reuse of the same ID after layers have been removed. (since 1.2)
    */
    protected int nextLayerID = 0;

    /**
     * from tmx format documentation:
     *
     * Stores the next available ID for new objects. This number is stored to prevent reuse of the same ID after objects have been removed. (since 0.11)
     */
    protected int nextObjectID = 0;

    protected List<Tileset> tilesets = null;

    protected TiledMap() {
        //
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getWidthInTiles() {
        return widthInTiles;
    }

    public int getHeightInTiles() {
        return heightInTiles;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public String getBackgroundColor() {
        return bgColor;
    }

    public int getNextLayerID() {
        return nextLayerID;
    }

    public int getNextObjectID() {
        return nextObjectID;
    }

    public List<Tileset> listTilesets() {
        return tilesets;
    }

}
