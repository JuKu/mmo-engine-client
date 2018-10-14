package com.jukusoft.mmo.engine.gameview.screens;

/**
 * Screen interface - screens are responsible for drawing, not for updating your game state!
 *
 * Created by Justin on 06.02.2017.
 */
public interface IScreen {

    /**
    * method which should be executed if screen is created
    */
    public void onStart(ScreenManager<IScreen> screenManager);

    /**
     * method which should be executed if screen has stopped
     */
    public void onStop();

    /**
     * method is executed, if screen is set to active state now.
     */
    public void onResume();

    /**
    * method is executed, if screen isn't active anymore
    */
    public void onPause();

    /**
    * window was resized
     *
     * @param width new window width
     * @param height new window height
    */
    public void onResize(int width, int height);

    /**
     * update game screen
     */
    public void update(ScreenManager<IScreen> screenManager);

    /**
     * beforeDraw game screen
     */
    public void draw();

}
