package com.jukusoft.mmo.engine.gameview.renderer.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;
import com.jukusoft.mmo.engine.gameview.renderer.map.impl.TmxMapRenderer;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;
import com.jukusoft.mmo.engine.shared.region.RegionMap;
import com.jukusoft.mmo.engine.shared.utils.MathUtils;

import java.util.Objects;

public class WorldMapRenderer implements IRenderer {

    protected static final String LOG_TAG = "WorldMapRenderer";

    //constants
    protected static final String CONFIG_SECTION = "WorldMapRenderer";
    protected static final int CONFIG_UPDATE_VISIBLE_MAPS_EVERY_X_FRAMES = Config.getInt(CONFIG_SECTION, "update_visible_maps_every_x_frames");
    protected static final int CONFIG_BORDER_PADDING = Config.getInt(CONFIG_SECTION, "update_visible_maps_camera_border_padding");

    //current player position
    protected float playerX = 0;
    protected float playerY = 0;
    protected float playerZ = 0;

    //current region
    protected final RegionInfo regionInfo;

    //list with all renderable maps
    protected Array<MapRenderer> mapList = null;
    protected Array<MapRenderer> visibleMaps = null;
    protected Array<MapRenderer> tempList = null;

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
        Objects.requireNonNull(regionInfo);
        Objects.requireNonNull(camera);

        //set player position
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerZ = playerZ;

        this.regionInfo = regionInfo;
        this.camera = camera;

        //create new map list
        this.mapList = new Array<>(false, regionInfo.listMaps().size());
        this.visibleMaps = new Array<>(false, regionInfo.listMaps().size());
        this.tempList = new Array<>(false, regionInfo.listMaps().size());
    }

    public void load () {
        //create map renderer instances
        for (RegionMap map : regionInfo.listMaps()) {
            MapRenderer mapRenderer = new TmxMapRenderer(map.absX, map.absY, map.widthInTiles, map.heightInTiles, regionInfo.getTileWidth(), regionInfo.getTileHeight());
            this.mapList.addAll(mapRenderer);
        }

        this.checkForNewVisibleMaps();
    }

    @Override
    public void update(GameTime time) {
        if (frameCounter % CONFIG_UPDATE_VISIBLE_MAPS_EVERY_X_FRAMES == 0) {
            //check, which maps are visible
            this.checkForNewVisibleMaps();
        }

        //update all visible maps
        for (MapRenderer map : this.visibleMaps) {
            if (map != null) {
                map.update(time);
            }
        }

        frameCounter++;
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        //draw all visible maps
        for (MapRenderer map : visibleMaps) {
            map.draw(time, camera, batch);
        }
    }

    public void setFloor (int floor) {
        Log.v(LOG_TAG, "set floor: " + floor);

        for (MapRenderer map : mapList) {
            map.setFloor(floor);
        }
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
        for (MapRenderer map : this.mapList) {
            if (isMapVisible(map)) {
                tempList.add(map);
            }
        }

        //check, which maps aren't visible anymore and unload them
        for (MapRenderer map : this.visibleMaps) {
            if (!tempList.contains(map, false)) {
                //map isn't visible anymore, so unload them
                map.unload();
            }
        }

        for (MapRenderer map : tempList) {
            if (!visibleMaps.contains(map, false)) {
                //map is new visible, so we need to load the map
                map.load();
            }
        }

        this.visibleMaps.clear();
        this.visibleMaps.addAll(tempList);

        this.tempList.clear();
    }

    protected boolean isMapVisible (MapRenderer map) {
        return MathUtils.overlapping(camera.getX() - CONFIG_BORDER_PADDING, camera.getX() + camera.getViewportWidth() + CONFIG_BORDER_PADDING, map.getX(), map.getX() + map.getWidth()) &&
                MathUtils.overlapping(camera.getY()- CONFIG_BORDER_PADDING, camera.getY() + camera.getViewportHeight() + CONFIG_BORDER_PADDING, map.getY(), map.getY() + map.getHeight());
    }

}
