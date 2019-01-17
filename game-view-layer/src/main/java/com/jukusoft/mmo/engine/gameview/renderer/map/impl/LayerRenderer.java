package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;

public class LayerRenderer implements IRenderer {

    protected TextureRegion[][] cells;
    protected boolean visible = true;

    //position
    protected float xPos = 0;
    protected float yPos = 0;

    //width & height in tiles
    protected final int widthInTiles;
    protected final int heightInTiles;

    //width & height of tiles in pixel
    protected final int tileWidth;
    protected final int tileHeight;

    protected LayerRenderer(int widthInTiles, int heightInTiles, float x, float y, int tileWidth, int tileHeight) {
        this.cells = new TextureRegion[widthInTiles][heightInTiles];

        this.xPos = x;
        this.yPos = y;

        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public void update(GameTime time) {
        //we dont need this method here
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        //we dont need to draw invisible layers
        if (!this.visible) {
            return;
        }

        //draw a clipping
        drawPartly(time, camera, batch, camera.getX(), camera.getY(), camera.getX() + camera.getViewportWidth(), camera.getY() + camera.getViewportHeight());
    }

    public void drawPartly (GameTime time, CameraHelper camera, SpriteBatch batch, float startX, float startY, float endX, float endY) {
        //we dont need to draw invisible layers
        if (!this.visible) {
            return;
        }

        //calculate first tiles to draw
        int startXIndex = (int) (startX - this.xPos) / this.tileWidth;
        int startYIndex = (int) (startY - this.yPos) / this.tileHeight;

        //max(startX, 0)
        startXIndex = startXIndex > 0 ? startXIndex : 0;
        startYIndex = startYIndex > 0 ? startYIndex : 0;

        //calculate last tiles to draw
        int endXIndex = startXIndex + ((int) (endX - startX + tileWidth) / tileWidth);
        int endYIndex = startYIndex + ((int) (endY - startY + tileHeight) / tileHeight);

        //max(endXIndex, widthInTiles)
        endXIndex = endXIndex > this.widthInTiles ? this.widthInTiles : endXIndex;
        endYIndex = endYIndex > this.heightInTiles ? this.heightInTiles : endYIndex;

        for (int y = startYIndex; y < endYIndex; y++) {
            for (int x = startXIndex; x < endXIndex; x++) {
                //get cell
                TextureRegion region = getCell(x, this.heightInTiles - y - 1);

                if (region == null) {
                    //transparent cell without tile
                    continue;
                }

                float tileXPos = x * this.tileWidth + this.xPos;
                float tileYPos = y * this.tileHeight + this.yPos;

                //render tile
                batch.draw(region, tileXPos, tileYPos, this.tileWidth, this.tileHeight);
            }
        }
    }

    @Override
    public void dispose() {
        this.cells = null;
    }

    public void setCell (int x, int y, TextureRegion region) {
        this.cells[x][y] = region;
    }

    public TextureRegion getCell (int x, int y) {
        return this.cells[x][y];
    }

    public boolean isVisible () {
        return visible;
    }

    public void setVisible (boolean visible) {
        this.visible = visible;
    }

}
