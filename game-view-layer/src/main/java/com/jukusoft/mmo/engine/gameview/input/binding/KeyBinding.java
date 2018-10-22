package com.jukusoft.mmo.engine.gameview.input.binding;

public interface KeyBinding {

    public boolean keyDown (int keycode);

    public boolean keyUp (int keycode);

    public boolean keyTyped (char character);

}
