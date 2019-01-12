package com.jukusoft.mmo.engine.gameview.renderer.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;

public class WorldMapRenderer implements IRenderer {

    protected static final String LOG_TAG = "WorldMapRenderer";

    //constants
    protected static final String CONFIG_SECTION = "WorldMapRenderer";
    protected static final int CONFIG_UPDATE_VISIBLE_MAPS_EVERY_X_FRAMES = Config.getInt(CONFIG_SECTION, "update_visible_maps_every_x_frames");

    //current player position
    protected float playerX = 0;
    protected float playerY = 0;
    protected float playerZ = 0;

    //current region
    protected final RegionInfo regionInfo;

    //list with all renderable maps
    protected Array<MapRenderer> mapList = null;

    //frame counter
    protected int frameCounter = 0;

    protected final CameraHelper camera;

    /**
    * default constructor
     *
     * @param playerX player x coordinate
     * @param playerY player y coordinate
     * @param playerZ player z-level (floor)
     * @param regionInfo region information
    */
    public WorldMapRenderer (float playerX, float playerY, float playerZ, RegionInfo regionInfo, CameraHelper camera) {
        //set player position
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerZ = playerZ;

        this.regionInfo = regionInfo;
        this.camera = camera;

        //create new map list
        this.mapList = new Array<>(false, regionInfo.listMaps().size());
    }

    public void load () {
        //
    }

    @Override
    public void update(GameTime time) {
        if (frameCounter % CONFIG_UPDATE_VISIBLE_MAPS_EVERY_X_FRAMES == 0) {
            //check, which maps are visible
            this.checkForNewVisibleMaps();
        }

        frameCounter++;
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        //
    }

    @Override
    public void dispose() {
        Log.d(LOG_TAG, "dispose all maps.");

        //unload all maps
        for (MapRenderer renderer : this.mapList) {
            renderer.dispose();
        }

        //remove all maps
        this.mapList.clear();
    }

    protected void checkForNewVisibleMaps () {
        //
    }

}
