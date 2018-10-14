package com.jukusoft.mmo.engine.gameview.input;

import com.badlogic.gdx.InputAdapter;

public class KeyboardInputProcessor extends InputAdapter {

    protected boolean enabled = true;

    public KeyboardInputProcessor () {
        //
    }

    /**
    * disable input processor, so no input events were handled, e.q. if game is paused
    */
    public void disable () {
        this.enabled = false;
    }

    /**
     * disable input processor, so input events were handled, e.q. if game isn't paused anymore
     */
    public void enable () {
        this.enabled = true;
    }

}
