package com.jukusoft.mmo.engine.gameview.renderer.map.impl.cache;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;

public class CachedRenderPage implements Disposable {

    protected FrameBuffer fbo = null;

    //dimension of page
    protected final int pageWidth;
    protected final int pageHeight;

    //the final (cached) texture page
    protected Texture cachedPage = null;

    //flag, if cache was written before
    protected boolean cached = false;

    //position
    protected final float absX;
    protected final float absY;

    public CachedRenderPage (float absX, float absY, int pageWidth, int pageHeight) {
        this.absX = absX;
        this.absY = absY;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    public void begin () {
        //create new frame buffer
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.pageWidth, this.pageHeight, false);
        this.fbo.begin();

        //clear framebuffer
        //Important! Patrick said we don't should clear framebuffer, else we remove transparency
        //Gdx.gl.glClearColor(0, 0, 0, 0);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void end () {
        this.fbo.end();

        //get texture from framebuffer
        this.cachedPage = this.fbo.getColorBufferTexture();

        //dispose old framebuffer, because we don't need it anymore
        this.fbo.dispose();
        this.fbo = null;

        this.cached = true;

        //we have to clear buffer, else it will also drawn to actual buffer instad only to framebuffer
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
    * draw page from cache
    */
    public void drawCached (SpriteBatch batch) {
        if (!this.cached) {
            throw new IllegalStateException("Cannot draw cached page, because page wasn't drawn into cache before. Call begin() and end() first to create cached page.");
        }

        batch.draw(this.cachedPage, this.absX, this.absY, this.pageWidth, this.pageHeight);
    }

    public boolean isLoaded () {
        return this.cached;
    }

    public float getX() {
        return absX;
    }

    public float getY() {
        return absY;
    }

    @Override
    public void dispose() {
        if (this.cachedPage != null) {
            this.cachedPage.dispose();
            this.cachedPage = null;
        }

        this.cached = false;
    }

}
