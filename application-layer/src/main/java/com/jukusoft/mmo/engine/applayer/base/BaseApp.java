package com.jukusoft.mmo.engine.applayer.base;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.Utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseApp implements ApplicationListener {

    @Override
    public void create() {
        //load logger config
        try {
            Config.load(Gdx.files.absolute("./config/logger.cfg"));

            //initialize logger
            Log.init();

            Utils.printSection("Game Engine");

            //TODO: print game engine version information
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Couldn't initialize config and logger!", e);
            System.exit(0);
        }
    }

    @Override
    public void resize(int width, int height) {
        //
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
