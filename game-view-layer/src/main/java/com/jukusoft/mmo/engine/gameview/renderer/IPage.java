package com.jukusoft.mmo.engine.gameview.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.time.GameTime;

/**
 * Created by Justin on 15.09.2017.
 */
public interface IPage {

    public int getWidth();

    public int getHeight();

    public void draw(GameTime time, SpriteBatch batch, float x, float y);

    public void dispose();

}
