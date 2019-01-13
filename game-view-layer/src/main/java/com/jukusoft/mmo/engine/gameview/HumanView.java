package com.jukusoft.mmo.engine.gameview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.applayer.utils.FPSManager;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.camera.manager.CameraManager;
import com.jukusoft.mmo.engine.gameview.camera.manager.DefaultCameraManager;
import com.jukusoft.mmo.engine.gameview.region.RegionAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.impl.*;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.load.RegionInfoLoadedEvent;
import com.jukusoft.mmo.engine.shared.client.events.load.ready.GameLogicLayerReadyEvent;
import com.jukusoft.mmo.engine.shared.client.events.load.ready.GameViewLayerReadyEvent;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.Screens;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.process.ProcessManager;
import com.jukusoft.mmo.engine.shared.process.impl.DefaultProcessManager;

/**
* game view layer
 *
 * @see com.jukusoft.mmo.engine.applayer.base.BaseApp
*/
public class HumanView implements SubSystem {

    protected static final String LOG_TAG = "GameView";

    protected ScreenManager<IScreen> screenManager = null;
    protected ProcessManager processManager = null;

    //OpenGL clear color
    protected final Color clearColor = Color.BLACK;

    //SpriteBatch instance
    protected SpriteBatch spriteBatch = null;

    //game camera
    protected CameraManager cameraManager = null;

    //asset manager
    GameAssetManager assetManager = GameAssetManager.getInstance();

    protected GameTime time = GameTime.getInstance();

    //fps manager
    protected final FPSManager fps = FPSManager.getInstance();

    //protected CameraManager cameraManager = null;

    @Override
    public void onInit() {
        Log.i(LOG_TAG, "initialize game-view-layer.");

        //initialize screen manager
        screenManager = new DefaultScreenManager();

        //initialize process manager
        this.processManager = new DefaultProcessManager();

        //initialize cameras
        Log.d("Camera", "initialize cameras...");
        this.cameraManager = new DefaultCameraManager(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.spriteBatch = new SpriteBatch();

        //add screens
        this.screenManager.addScreen(Screens.SELECT_SERVER_SCREEN, new SelectServerScreen());
        this.screenManager.addScreen(Screens.LOGIN_SCREEN, new LoginScreen());
        this.screenManager.addScreen(Screens.CHARACTER_SELECTION, new SelectCharacterScreen());
        this.screenManager.addScreen(Screens.CREATE_CHARACTER, new CreateCharacterScreen());
        this.screenManager.addScreen(Screens.LOAD_REGION, new LoadRegionScreen());
        this.screenManager.addScreen(Screens.PLAY, new PlayGameScreen());

        this.screenManager.leaveAllAndEnter(Screens.SELECT_SERVER_SCREEN);

        //TODO: initialize audio engine

        Events.addListener(Events.UI_THREAD, ClientEvents.REGION_INFO_LOADED, (EventListener<RegionInfoLoadedEvent>) event -> {
            //load region (maps and so on)
            PlayGameScreen screen = (PlayGameScreen) this.screenManager.getScreenByName(Screens.PLAY);
            screen.resetRegion();
            screen.loadRegion(event, this.spriteBatch, this.cameraManager.getMainCamera());

            new Thread(() -> {
                Log.v(LOG_TAG, "wait while PlayGameScreen is ready to play...");
                while (!screen.isReadyToPlay()) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(LOG_TAG, "PlayGameScreen is ready to play now.");

                //inform network layer
                GameViewLayerReadyEvent event1 = Pools.get(GameViewLayerReadyEvent.class);
                Events.queueEvent(event1);
            }).start();

            //TODO: reset & load audio engine
        });
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

        //update camera
        this.cameraManager.update(this.time);

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
