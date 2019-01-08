package com.jukusoft.mmo.engine.shared.map.tileset;

public abstract class Tileset {

    protected final int firstTileID;
    protected final int lastTileID;

    public Tileset(int firstTileID, int lastTileID) {
        if (lastTileID < firstTileID) {
            throw new IllegalArgumentException("lastTileID has to be greater than firstTileID.");
        }

        this.firstTileID = firstTileID;
        this.lastTileID = lastTileID;
    }

    public int getFirstTileID() {
        return firstTileID;
    }

    public int getLastTileID() {
        return lastTileID;
    }

}
