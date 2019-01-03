package com.jukusoft.mmo.engine.gameview.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;

/**
 * Created by Justin on 14.09.2017.
 */
public interface IRenderer {

    /**
     * update renderer
     */
    public void update(GameTime time);

    /**
     * draw renderer
     */
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch);

    public void dispose();

}
