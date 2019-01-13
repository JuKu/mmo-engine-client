package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.map.MapRenderer;

public class TmxMapRenderer implements MapRenderer {

    //absolute map position
    protected final float x;
    protected final float y;

    //dimension
    protected final int widthInTiles;
    protected final int heightInTiles;
    protected final int tileWidth;
    protected final int tileHeight;

    //current floor to render
    protected int floor = 1;

    public TmxMapRenderer (float absX, float absY, int widthInTiles, int heightInTiles, int tileWidth, int tileHeight) {
        this.x = absX;
        this.y = absY;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.widthInTiles * this.tileWidth;
    }

    @Override
    public int getHeight() {
        return this.heightInTiles * this.tileHeight;
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    @Override
    public void setFloor(int floor) {
        this.floor = floor;

        //TODO: invalidate pages
    }

    @Override
    public void update(GameTime time) {

    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        //
    }

    @Override
    public void dispose() {

    }

}
