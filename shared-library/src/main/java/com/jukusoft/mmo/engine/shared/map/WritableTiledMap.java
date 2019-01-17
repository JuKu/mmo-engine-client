package com.jukusoft.mmo.engine.shared.map;

import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;

import java.util.List;

public class WritableTiledMap extends TiledMap {

    public void setOrientation (Orientation orientation) {
        this.orientation = orientation;
    }

    public void setDimension (int widthInTiles, int heightInTiles) {
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
    }

    public void setTileDimenstion (int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void setBackgroundColor (String bgColor) {
        if (!bgColor.startsWith("#")) {
            throw new IllegalArgumentException("background color has to start with '#'!");
        }

        if (bgColor.length() < 7) {
            throw new IllegalArgumentException("background color has to min. 7 characters, e.q. '#0000FF' or '#AARRGGBB'");
        }

        this.bgColor = bgColor;
    }

    public void setNextLayerID (int nextLayerID) {
        this.nextLayerID = nextLayerID;
    }

    public void setNextObjectID (int nextObjectID) {
        this.nextObjectID = nextObjectID;
    }

    public void setTilesets (List<Tileset> tilesets) {
        this.tilesets = tilesets;
    }

    public void setLayers (List<TiledLayer> layers) {
        this.layers = layers;
    }

}
