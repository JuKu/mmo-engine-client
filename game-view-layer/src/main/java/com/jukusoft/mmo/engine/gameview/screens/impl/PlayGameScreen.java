package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.map.WorldMapRenderer;
import com.jukusoft.mmo.engine.gameview.renderer.water.WaterRenderer;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenAdapter;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.shared.client.events.load.RegionInfoLoadedEvent;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

import java.util.Objects;

public class PlayGameScreen extends ScreenAdapter {

    //log tag
    protected static final String LOG_TAG = "PlayScreen";

    //renderer
    protected WaterRenderer waterRenderer = null;
    protected WorldMapRenderer worldMapRenderer = null;

    protected GameTime time = GameTime.getInstance();
    protected CameraHelper camera = null;
    protected SpriteBatch batch = null;

    protected boolean readyToPlay = false;

    protected GameAssetManager assetManager = GameAssetManager.getInstance();

    @Override
    public void onStart(ScreenManager<IScreen> screenManager) {
        //
    }

    @Override
    public void onStop() {
        //
    }

    @Override
    public void onResize(int width, int height) {
        //resize camera
        this.camera.resize(width, height);
    }

    @Override
    public void onResume() {
        //load water renderer
        this.waterRenderer = new WaterRenderer();
        this.waterRenderer.load(FilePath.parse("{data.dir}misc/water/water.atlas"), "water", 1000f / 8);
    }

    @Override
    public void onPause() {
        //dispose water renderer
        this.waterRenderer.dispose();
        this.waterRenderer = null;
    }

    @Override
    public void update(ScreenManager<IScreen> screenManager) {
        if (this.worldMapRenderer == null) {
            throw new IllegalStateException("Cannot update PlayGameScreen, before PlayGameScreen.loadRegion() was called!");
        }

        //update water
        this.waterRenderer.update(time);

        //update game world map renderer
        this.worldMapRenderer.update(time);
    }

    @Override
    public void draw() {
        //render water
        this.waterRenderer.draw(time, camera, batch);

        //render game world maps
        this.worldMapRenderer.draw(time, camera, batch);
    }

    /**
    * method which is called before screen is active (while loading region &amp; maps)
     *
     * @see com.jukusoft.mmo.engine.gameview.HumanView
    */
    public void loadRegion (RegionInfoLoadedEvent event, SpriteBatch spriteBatch, CameraHelper camera) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(spriteBatch);
        Objects.requireNonNull(camera);

        this.batch = spriteBatch;
        this.camera = camera;

        Log.i(LOG_TAG, "loadRegion: " + event);

        //set camera position to player position
        this.camera.setTargetMiddlePos(event.posX, event.posY);
        this.camera.forcePos();
        this.camera.update(time);

        //create new world map renderer
        this.worldMapRenderer = new WorldMapRenderer(event.posX, event.posY, event.posZ, event.regionInfo, this.camera);
        this.worldMapRenderer.load();

        this.readyToPlay = true;
    }

    public boolean isReadyToPlay () {
        return this.readyToPlay && assetManager.getProgress() >= 1;
    }

    /**
    * reset region
     *
     * this method is called before loading the new region
     *
     * @see com.jukusoft.mmo.engine.gameview.HumanView
     * @see PlayGameScreen#loadRegion(RegionInfoLoadedEvent, SpriteBatch, CameraHelper)
    */
    public void resetRegion () {
        this.readyToPlay = false;

        if (this.worldMapRenderer != null) {
            this.worldMapRenderer.dispose();
            this.worldMapRenderer = null;
        }
    }

}
