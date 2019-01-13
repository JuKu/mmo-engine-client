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
    public void load () throws MapLoaderException;

    /**
    * unload map
    */
    public void unload ();

    /**
    * check, if map renderer is loaded (and ready to render)
     *
     * @return true, if map renderer is loaded
    */
    public boolean isLoaded ();

    /**
    * set floor
    */
    public void setFloor (int floor);

}
