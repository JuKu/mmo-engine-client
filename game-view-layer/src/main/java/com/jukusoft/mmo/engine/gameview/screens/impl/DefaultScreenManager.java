package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectStack;
import com.jukusoft.mmo.engine.applayer.utils.Platform;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.ScreenNotFoundException;

/**
 * Created by Justin on 06.02.2017.
 */
public class DefaultScreenManager implements ScreenManager<IScreen> {

    protected static final String TAG_INJECT_SERVICE = "inject_service";
    protected static final String TAG_NAME_CANNOT_NULL = "name cannot be null.";
    protected static final String TAG_NAME_CANNOT_EMPTY = "name cannot be empty.";
    protected static final String TAG_SCREENS = "Screens";

    /**
     * map with all initialized screens
     */
    protected ObjectMap<String, IScreen> screens = new ObjectMap<>(30);

    /**
     * list with all active screens
     */
    protected ObjectStack<IScreen> activeScreens = new ObjectStack<>(20);

    /**
     * only for performance improvements!
     *
     * caching list
     */
    protected ObjectArrayList<IScreen> cachedScreenList = new ObjectArrayList<>(20);

    /**
    * default constructor
    */
    public DefaultScreenManager() {
        //
    }

    @Override
    public void addScreen(String name, IScreen screen) {
        if (name == null) {
            throw new NullPointerException(TAG_NAME_CANNOT_NULL);
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException(TAG_NAME_CANNOT_EMPTY);
        }

        if (screen == null) {
            throw new NullPointerException("screen cannot be null.");
        }

        //check, if screen already exists
        if (this.screens.get(name) != null) {
            throw new IllegalStateException("screen '" + name + "' already exists!");
        }

        // initialize screen first
        screen.onStart(this);

        this.screens.put(name, screen);

        this.cachedScreenList.add(screen);

        Gdx.app.debug(TAG_SCREENS, "add screen: " + name);
    }

    @Override
    public void removeScreen(String name) {
        if (name == null) {
            throw new NullPointerException(TAG_NAME_CANNOT_NULL);
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException(TAG_NAME_CANNOT_EMPTY);
        }

        IScreen screen = this.screens.get(name);

        if (screen != null) {
            screen.onStop();

            this.activeScreens.removeFirst(screen);
            this.cachedScreenList.removeFirst(screen);
        }

        this.screens.remove(name);

        Gdx.app.debug(TAG_SCREENS, "remove screen: " + name);
    }

    @Override
    public void push(String name) {
        if (name == null) {
            throw new NullPointerException(TAG_NAME_CANNOT_NULL);
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException(TAG_NAME_CANNOT_EMPTY);
        }

        IScreen screen = this.screens.get(name);

        if (screen == null) {
            throw new ScreenNotFoundException(
                    "Couldnt found initialized screen '" + name + "', add screen with method addScreen() first.");
        }

        screen.onResume();

        //call resize
        screen.onResize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.activeScreens.push(screen);

        Gdx.app.debug(TAG_SCREENS, "push screen: " + name);
    }

    @Override
    public void leaveAllAndEnter(String name) {
        // leave all active game states
        IScreen screen = pop();

        // pop and pause all active screens
        while (screen != null) {
            screen = pop();
        }

        // push new screen
        this.push(name);
    }

    @Override
    public IScreen pop() {
        IScreen screen = null;

        if (this.activeScreens.size() > 0) {
            screen = this.activeScreens.pop();
            final IScreen screen1 = screen;

            Platform.runOnUIThread(() -> {
                //pause screen
                screen1.onPause();
            });
        }

        Gdx.app.debug(TAG_SCREENS, "pop screen.");

        return screen;
    }

    @Override
    public IScreen getScreenByName(String name) {
        return this.screens.get(name);
    }

    @Override
    public ObjectArrayList<IScreen> listScreens() {
        return this.cachedScreenList;
    }

    @Override
    public ObjectArrayList<IScreen> listActiveScreens() {
        return this.activeScreens;
    }

    @Override
    public void resize(int width, int height) {
        for (int i = this.activeScreens.size() - 1; i >= 0; i--) {
            //get screen
            IScreen screen = this.activeScreens.get(i);

            //resize screen
            screen.onResize(width, height);
        }
    }

    @Override
    public void update() {
        for (int i = 0; i < this.activeScreens.size(); i++) {
            //get screen
            IScreen screen = this.activeScreens.get(i);

            //update screen
            screen.update(this);
        }
    }

    @Override
    public void draw() {
        for (int i = 0; i < this.activeScreens.size(); i++) {
            //get screen
            IScreen screen = this.activeScreens.get(i);

            //draw screen
            screen.draw();
        }
    }

    @Override
    public void dispose() {
        //iterate through all screens
        for (String key : this.screens.keys()) {
            //remove screen
            this.removeScreen(key);
        }
    }

}
