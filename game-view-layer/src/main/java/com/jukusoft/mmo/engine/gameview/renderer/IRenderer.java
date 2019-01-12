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
     *
     * @param time game time instance to get current game time
     */
    public void update(GameTime time);

    /**
     * draw renderer
     *
     * @param time game time instance to get current game time
     * @param camera game camera
     * @param batch sprite batch
     */
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch);

    /**
    * dispose renderer
    */
    public void dispose();

}
