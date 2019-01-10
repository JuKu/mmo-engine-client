package com.jukusoft.mmo.engine.shared.map;

import java.util.Objects;

public class TiledLayer {

    protected String name = "";
    protected int width = 0;//layer width in tiles
    protected int height = 0;//layer height in tiles

    protected float opacity = 1;
    protected boolean visible = true;
    protected float offsetx = 0;
    protected float offsety = 0;

    protected int[] tileIDs = null;

    /**
    * default constructor
     *
     * @param name layer name
     * @param width layer width in tiles
     * @param height layer height in tiles
    */
    public TiledLayer(String name, int width, int height, float opacity, boolean visible, float offsetx, float offsety) {
        Objects.requireNonNull(name);

        this.name = name;
        this.width = width;
        this.height = height;

        this.opacity = opacity;
        this.visible = visible;
        this.offsetx = offsetx;
        this.offsety = offsety;
    }

    public void setTileIDs(int[] tileIDs) {
        if (tileIDs == null) {
            throw new NullPointerException("tileIDs cannot be null.");
        }

        if (tileIDs.length != this.width * this.height) {
            throw new IllegalArgumentException("length of array does not fit with width & height, expected length: " + (this.width * this.height) + ", current length: " + tileIDs.length);
        }

        this.tileIDs = tileIDs;
    }

    public void addBoolProperty (String key, boolean value) {
        //
    }

    public void addIntProperty (String key, int value) {
        //
    }

    public void addFloatProperty (String key, float value) {
        //
    }

    public void addStringProperty (String key, String value) {
        //
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getOpacity() {
        return opacity;
    }

    public boolean isVisible() {
        return visible;
    }

    public float getOffsetx() {
        return offsetx;
    }

    public float getOffsety() {
        return offsety;
    }

    public int[] getTileIDs() {
        if (this.tileIDs == null) {
            throw new IllegalStateException("tileIDs wasnt set before, call setTileIDs() first.");
        }

        return tileIDs;
    }

}
