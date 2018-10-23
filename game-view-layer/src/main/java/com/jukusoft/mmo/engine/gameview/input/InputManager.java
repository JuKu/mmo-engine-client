package com.jukusoft.mmo.engine.gameview.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.jukusoft.mmo.engine.applayer.logger.Log;

public class InputManager {

    //singleton instance
    protected static final InputManager instance = new InputManager();

    protected InputMultiplexer inputMultiplexer = null;

    public InputManager() {
        this.inputMultiplexer = new InputMultiplexer();
    }

    public void setGdxInputProcessor () {
        Log.v("InputManager", "set libGDX input processor.");
        Gdx.input.setInputProcessor(this.inputMultiplexer);
    }

        public void add (InputProcessor processor) {
        //check, that processor wasnt registered before
        this.verifyUnique(processor);

        this.inputMultiplexer.addProcessor(processor);
    }

    public void addFirst (InputProcessor processor) {
        //check, that processor wasnt registered before
        this.verifyUnique(processor);

        this.inputMultiplexer.addProcessor(0, processor);
    }

    public void remove (InputProcessor processor) {
        this.inputMultiplexer.removeProcessor(processor);
    }

    public boolean contains (InputProcessor processor) {
        return this.inputMultiplexer.getProcessors().contains(processor, false);
    }

    public void verifyUnique (InputProcessor processor) {
        if (this.contains(processor)) {
            throw new IllegalStateException("input processor " + processor.getClass().getSimpleName() + " was already added before.");
        }
    }

    public Array<InputProcessor> getProcessors () {
        return this.inputMultiplexer.getProcessors();
    }

    public int countProcessors () {
        return this.inputMultiplexer.size();
    }

    public void clear () {
        this.inputMultiplexer.clear();
    }

    public static InputManager getInstance () {
        return instance;
    }

}
