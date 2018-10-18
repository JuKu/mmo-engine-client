package com.jukusoft.mmo.engine.applayer.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Justin on 10.03.2017.
 */
public class SpriteBatcherUtils {

    protected static Texture pixelTexture = null;
    protected static TextureRegion textureRegion = null;
    protected static Affine2 affine2 = new Affine2();

    /**
    * private constructor
    */
    protected SpriteBatcherUtils() {
        //
    }

    public static void drawRect(SpriteBatch batch, Rectangle hitbox, float thickness, Color color) {
        float x = hitbox.getX();
        float y = hitbox.getY();
        float width = hitbox.getWidth();
        float height = hitbox.getHeight();

        // draw border of rectangle
        fillRectangle(batch, x, y, width, thickness, color);
        fillRectangle(batch, x, y, thickness, height, color);
        fillRectangle(batch, x + width - thickness, y, thickness, height, color);
        fillRectangle(batch, x, y + height - thickness, width, thickness, color);
    }

    public static void drawRectangle(SpriteBatch batch, float x, float y, float width, float height, Color color) {
        initTextureIfAbsent();

        float thickness = 1;

        // draw border of rectangle
        fillRectangle(batch, x, y, width, thickness, color);
        fillRectangle(batch, x, y, thickness, height, color);
        fillRectangle(batch, x + width - thickness, y, thickness, height, color);
        fillRectangle(batch, x, y + height - thickness, width, thickness, color);
    }

    public static void fillRectangle(SpriteBatch batch, float x, float y, float width, float height, Color color) {
        initTextureIfAbsent();

        // backup color
        Color backupColor = batch.getColor();

        // set rectangle color
        batch.setColor(color);

        batch.draw(textureRegion, x, y, width, height);

        // reset color
        batch.setColor(backupColor);
    }

    protected static void initTextureIfAbsent() {
        if (pixelTexture == null) {
            // create pixmap
            Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);

            // draw one pixel
            pixmap.setColor(1, 1, 1, 1);
            pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());

            pixelTexture = new Texture(pixmap);

            // create texture region
            textureRegion = new TextureRegion(pixelTexture, pixelTexture.getWidth(), pixelTexture.getHeight());

            // cleanup pixmap
            pixmap.dispose();
        }
    }

}
