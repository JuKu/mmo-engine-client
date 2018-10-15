package com.jukusoft.mmo.engine.gameview.input;

import com.badlogic.gdx.InputAdapter;
import com.jukusoft.mmo.engine.logic.input.InputActions;

public class KeyboardInputProcessor extends InputAdapter {

    protected boolean enabled = true;
    protected InputMapper inputMapper = null;

    public KeyboardInputProcessor () {
        this.inputMapper = new InputMapper();

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

    @Override
    public boolean keyDown (int keycode) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        InputActions action = this.inputMapper.getKeyDownAction(keycode);

        if (action != null) {
            //TODO: call handler
        }

        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        InputActions action = this.inputMapper.getKeyUpAction(keycode);

        if (action != null) {
            //TODO: call handler
        }

        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        //TODO: add code here

        return false;
    }

}
