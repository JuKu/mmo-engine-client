package com.jukusoft.mmo.engine.gameview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.utils.FPSManager;
import com.jukusoft.mmo.engine.gameview.screens.impl.LoginScreen;
import com.jukusoft.mmo.engine.gameview.screens.impl.SelectCharacterScreen;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.Screens;
import com.jukusoft.mmo.engine.gameview.screens.impl.DefaultScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.impl.SelectServerScreen;
import com.jukusoft.mmo.engine.shared.process.ProcessManager;
import com.jukusoft.mmo.engine.shared.process.impl.DefaultProcessManager;

public class HumanView implements SubSystem {

    protected ScreenManager<IScreen> screenManager = null;
    protected ProcessManager processManager = null;

    //OpenGL clear color
    protected final Color clearColor = Color.BLACK;

    //SpriteBatch instance
    protected SpriteBatch spriteBatch = null;

    //asset manager
    GameAssetManager assetManager = GameAssetManager.getInstance();

    //fps manager
    protected final FPSManager fps = FPSManager.getInstance();

    @Override
    public void onInit() {
        Log.i("GameView", "initialize game-view-layer.");

        //initialize screen manager
        screenManager = new DefaultScreenManager();

        //initialize process manager
        this.processManager = new DefaultProcessManager();

        this.spriteBatch = new SpriteBatch();

        //add screens
        this.screenManager.addScreen(Screens.SELECT_SERVER_SCREEN, new SelectServerScreen());
        this.screenManager.addScreen(Screens.LOGIN_SCREEN, new LoginScreen());
        this.screenManager.addScreen(Screens.CHARACTER_SELECTION, new SelectCharacterScreen());

        this.screenManager.leaveAllAndEnter(Screens.SELECT_SERVER_SCREEN);

        //TODO: initialize audio engine
    }

    @Override
    public void onGameloop() {
        //update FPS
        fps.setFPS(Gdx.graphics.getFramesPerSecond());

        //show FPS warning, if neccessary
        fps.showWarningIfNeccessary();

        //clear OpenGL buffer
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //load assets
        this.assetManager.update();

        //execute process manager first
        this.processManager.update();

        //update game screens
        screenManager.update();

        //draw game screen
        screenManager.draw();
    }

    @Override
    public void onShutdown() {
        //pop all active screens first
        while (this.screenManager.pop() != null) {
            //don't do anything here
        }

        //remove all screens
        this.screenManager.dispose();

        this.spriteBatch.dispose();
    }

}
