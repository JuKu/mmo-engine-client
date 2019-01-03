package com.jukusoft.mmo.engine.gameview.renderer.water;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.renderer.BasePage;

/**
 * Created by Justin on 15.09.2017.
 */
public class WaterRenderPage extends BasePage {

    //tile width
    protected int tileWidth = 32;
    protected int tileHeight = 32;

    protected TextureRegion region = null;

    //texture to render
    protected Texture texture = null;

    /**
     * default constructor
     *
     * @param width  page width
     * @param height page height
     */
    public WaterRenderPage(int width, int height, TextureRegion textureRegion) {
        super(width, height);

        this.region = textureRegion;

        //create texture and pre-render tiles to texture (CPU rendering)
        this.prepare();
    }

    @Override
    public void draw(GameTime time, SpriteBatch batch, float x, float y) {
        batch.draw(this.texture, x, y);
    }

    @Override
    public void dispose() {
        //
    }

    protected void prepare () {
        //first, get tile texture
        Texture tileTexture = region.getTexture();

        if (tileTexture == null) {
            throw new NullPointerException("tile texture cannot be null.");
        }

        //prepare pixmap, if neccessary
        if (!tileTexture.getTextureData().isPrepared()) {
            tileTexture.getTextureData().prepare();
        }

        //get pixmap from tile texture
        Pixmap tilePixmap = tileTexture.getTextureData().consumePixmap();

        //create new pixmap
        Pixmap pixmap = new Pixmap(this.width, this.height, Pixmap.Format.RGBA8888);

        //calculate, how many tiles on width and height are required
        int requiredTilesX = this.width / this.tileWidth;
        int requiredTilesY = this.height / this.tileHeight;

        //draw tiles
        for (int x = 0; x < requiredTilesX; x++) {
            for (int y = 0; y < requiredTilesY; y++) {
                int startX = x * tileWidth;
                int startY = y * tileWidth;

                //draw tile
                pixmap.drawPixmap(tilePixmap, startX, startY, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());
            }
        }

        //create texture from pixmap
        this.texture = new Texture(pixmap);

        //dispose tile texture pixmal
        tileTexture.getTextureData().disposePixmap();

        //dispose generated pixmap
        pixmap.dispose();
    }

}
