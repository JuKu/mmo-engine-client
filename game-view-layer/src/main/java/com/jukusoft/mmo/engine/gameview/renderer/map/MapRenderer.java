package com.jukusoft.mmo.engine.gameview.renderer.map;

import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;

public interface MapRenderer extends IRenderer {

    /**
    * get x position
     *
     * @return absolute x position of map
    */
    public float getX ();

    /**
     * get y position
     *
     * @return absolute y position of map
     */
    public float getY ();

    /**
    * get map width in pixels
     *
     * @return map width in pixels
    */
    public int getWidth ();

    /**
     * get map width in pixels
     *
     * @return map width in pixels
     */
    public int getHeight ();

    /**
    * load map
    */
    public void load ();

    /**
    * unload map
    */
    public void unload ();

    /**
    * set floor
    */
    public void setFloor (int floor);

}
