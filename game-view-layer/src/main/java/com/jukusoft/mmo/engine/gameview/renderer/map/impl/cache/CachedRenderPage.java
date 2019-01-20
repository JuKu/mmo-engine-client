package com.jukusoft.mmo.engine.gameview.renderer.map.impl.cache;

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

    public CachedRenderPage (int pageWidth, int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    public void begin () {
        //create new frame buffer
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.pageWidth, this.pageHeight, false);
        this.fbo.begin();
    }

    public void end () {
        this.fbo.end();

        //get texture from framebuffer
        this.cachedPage = this.fbo.getColorBufferTexture();

        //dispose old framebuffer, because we don't need it anymore
        this.fbo.dispose();

        this.cached = true;
    }

    /**
    * draw page from cache
    */
    public void drawCached (GameTime time, CameraHelper camera, SpriteBatch batch, float xPos, float yPos) {
        if (!this.cached) {
            throw new IllegalStateException("Cannot draw cached page, because page wasn't drawn into cache before. Call begin() and end() first to create cached page.");
        }

        batch.draw(this.cachedPage, xPos, yPos);
    }

    @Override
    public void dispose() {
        if (this.cachedPage != null) {
            this.cachedPage.dispose();
            this.cachedPage = null;
        }
    }

}
