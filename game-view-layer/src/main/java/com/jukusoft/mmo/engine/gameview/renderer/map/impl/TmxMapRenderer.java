package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.map.MapLoaderException;
import com.jukusoft.mmo.engine.gameview.renderer.map.MapRenderer;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.map.TiledLayer;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.parser.TiledParserException;
import com.jukusoft.mmo.engine.shared.map.parser.TmxMapParser;
import com.jukusoft.mmo.engine.shared.map.tileset.TextureTileset;
import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class TmxMapRenderer implements MapRenderer {

    protected static final String LOG_TAG = "TmxMapRenderer";

    //tmx format uses some flags for tileIDs to check flipping of tiles
    protected static final int FLAG_FLIP_HORIZONTALLY = 0x80000000;
    protected static final int FLAG_FLIP_VERTICALLY = 0x40000000;
    protected static final int FLAG_FLIP_DIAGONALLY = 0x20000000;
    protected static final int MASK_CLEAR = 0xE0000000;

    //absolute map position
    protected final float x;
    protected final float y;

    //dimension
    protected final int widthInTiles;
    protected final int heightInTiles;
    protected final int tileWidth;
    protected final int tileHeight;

    //tmx filepath
    protected final String filePath;

    protected final RegionInfo regionInfo;

    //flag, if renderer is ready to render
    protected boolean loading = false;
    protected boolean loaded = false;

    protected TiledMap map = null;

    //current floor to render
    protected int floor = 1;

    protected Array<String> loadedAssets = null;

    protected final GameAssetManager assetManager;

    protected IntObjectMap<FloorRenderer> floorRenderers = new IntObjectHashMap<>(10);

    //map with tileIDs - texture region
    protected IntMap<TextureRegion> tiles = new IntMap<>();

    public TmxMapRenderer (GameAssetManager assetManager, String tmxFile, RegionInfo regionInfo, float absX, float absY, int widthInTiles, int heightInTiles, int tileWidth, int tileHeight) {
        Objects.requireNonNull(assetManager);
        Objects.requireNonNull(tmxFile);
        Objects.requireNonNull(regionInfo);

        this.assetManager = assetManager;
        this.filePath = FilePath.parse(tmxFile);
        this.regionInfo = regionInfo;

        //check, if file exists
        if (!new File(this.filePath).exists()) {
            throw new IllegalStateException("tmx file doesn't exists: " + new File(filePath).getAbsolutePath());
        }

        this.x = absX;
        this.y = absY;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.widthInTiles * this.tileWidth;
    }

    @Override
    public int getHeight() {
        return this.heightInTiles * this.tileHeight;
    }

    @Override
    public void load() throws MapLoaderException {
        if (isLoaded()) {
            Log.d(LOG_TAG, "map is already loaded.");
            return;
        }

        Log.v(LOG_TAG, "load tmx map renderer: " + filePath);

        //load and parse tmx file
        try {
            this.map = TmxMapParser.parse(new File(filePath));
        } catch (TiledParserException e) {
            throw new MapLoaderException("TiledParserException while parsing tmx map: " + filePath, e);
        }

        Objects.requireNonNull(this.map);

        //check dimensions
        if (this.widthInTiles != this.map.getWidthInTiles()) {
            throw new IllegalStateException("tmx map has another widthInTiles as in region.json! (region.json: " + this.widthInTiles + ", tmx map: " + map.getWidthInTiles() + ")");
        }

        if (this.heightInTiles != this.map.getHeightInTiles()) {
            throw new IllegalStateException("tmx map has another heightInTiles as in region.json! (region.json: " + this.heightInTiles + ", tmx map: " + map.getHeightInTiles() + ")");
        }

        this.loadedAssets = new Array<>(map.listTilesets().size());
        this.loading = true;

        //load assets
        for (Tileset tileset : map.listTilesets()) {
            if (tileset instanceof TextureTileset) {
                TextureTileset tileset1 = (TextureTileset) tileset;
                ObjectArrayList<TextureTileset.TilesetImage> requiredTextures = tileset1.listTextures();

                //load images
                for (int k = 0; k < requiredTextures.size(); k++) {
                    TextureTileset.TilesetImage texture = requiredTextures.get(k);

                    //get texture path
                    String texturePath = texture.source;
                    Log.v(LOG_TAG, "load tileset: " + texturePath);

                    //check, if file exists
                    if (!new File(texturePath).exists()) {
                        throw new MapLoaderException("required tileset texture doesnt exists: " + texturePath);
                    }

                    //load texture
                    assetManager.load(texturePath, Texture.class);
                    this.loadedAssets.add(texturePath);
                }
            } else {
                throw new UnsupportedOperationException("tileset type " + tileset.getClass().getSimpleName() + " isnt supported yet.");
            }
        }

        if (map.listLayers().isEmpty()) {
            throw new IllegalStateException("map doesn't contaisn any layer!");
        }
    }

    protected void loadAfterAssetLoadingFinished () {
        Log.d(LOG_TAG, "loadAfterAssetLoadingFinished().");

        //check, required assets
        map.listTilesets().iterator().forEachRemaining(tileset -> {
            if (tileset instanceof TextureTileset) {
                TextureTileset tileset1 = (TextureTileset) tileset;

                //check, if images are loaded
                tileset1.listTextures().iterator().forEachRemaining(texture -> {
                    Log.v(LOG_TAG, "finish loading of asset: " + texture.value.source);
                    assetManager.finishLoading(texture.value.source);

                    int firstTileID = texture.value.firstTileID;

                    //get texture
                    Texture tex = assetManager.get(texture.value.source, Texture.class);
                    Log.v(LOG_TAG, "texture: " + texture.value.source + ", width: " + tex.getWidth() + ", height: " + tex.getHeight());

                    int cols = texture.value.width / texture.value.tilesetTileWidth;

                    for (int y = 0; y < texture.value.height / texture.value.tilesetTileHeight; y++) {
                        for (int x = 0; x < cols; x++) {
                            //calculate tileID
                            int tileID = y * cols + x + firstTileID;

                            //clear flip mask
                            tileID = tileID & ~MASK_CLEAR;

                            int tileX = x * texture.value.tilesetTileWidth;
                            int tileY = y * texture.value.tilesetTileHeight;

                            //create texture region
                            TextureRegion tileRegion = new TextureRegion(tex, tileX, tileY, texture.value.tilesetTileWidth, texture.value.tilesetTileHeight);

                            //put id to map
                            this.tiles.put(tileID, tileRegion);
                        }
                    }
                });
            } else {
                throw new UnsupportedOperationException("tileset type '" + tileset.getClass().getSimpleName() + "' is not supported yet.");
            }
        });

        //group floors
        for (TiledLayer layer : map.listLayers()) {
            int floor = layer.getFloor();

            Log.v(LOG_TAG, "found layer '" + layer.getName() + "' on floor '" + floor + "'");

            //create new renderer if no floor renderer exists for this floor
            if (!this.floorRenderers.containsKey(floor)) {
                Log.v(LOG_TAG, "create new floor: " + floor);
                this.floorRenderers.put(floor, new FloorRenderer(widthInTiles, heightInTiles, x, y, tileWidth, tileHeight, this.tiles));
            }

            FloorRenderer floorRenderer = this.floorRenderers.get(floor);
            floorRenderer.addLayer(layer);
        }

        //prepare renderers
        floorRenderers.forEach((Consumer<IntObjectCursor<FloorRenderer>>) cursor -> cursor.value.prepare());
    }

    @Override
    public void unload() {
        //unload assets
        for (String filePath : this.loadedAssets) {
            Log.v(LOG_TAG, "unload tileset: " + filePath);
            assetManager.unload(filePath);
        }

        this.loading = false;
        this.loaded = false;

        this.loadedAssets = null;
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public void setFloor(int floor) {
        this.floor = floor;

        //TODO: invalidate pages
    }

    @Override
    public void update(GameTime time) {
        if (!isLoaded() && loading) {
            //check, if all assets are loaded
            if (checkIfAllAssetsAreLoaded()) {
                //all assets are loaded now
                this.loading = false;

                Log.v(LOG_TAG, "all " + loadedAssets.size + " assets are loaded now.");
                this.loadAfterAssetLoadingFinished();

                this.loaded = true;
            }

            return;
        }

        //TODO: create pages or update them
    }

    protected boolean checkIfAllAssetsAreLoaded () {
        //check, if all assets are loaded
        for (String filePath : loadedAssets) {
            if (!assetManager.isLoaded(filePath)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        if (this.floor != 1 && regionInfo.isDrawGroundAlways()) {
            //player is in aother floor, but we should draw ground too (0 means water, 1 means ground)
            this.drawFloor(time, camera, batch, 1);
        }

        //draw current floor
        this.drawFloor(time, camera, batch, this.floor);
    }

    protected void drawFloor (GameTime time, CameraHelper camera, SpriteBatch batch, int floor) {
        FloorRenderer renderer = this.floorRenderers.get(floor);

        if (renderer == null) {
            Log.w(LOG_TAG, "floor renderer not found for floor '" + floor + "'!");
        } else {
            renderer.draw(time, camera, batch);
        }
    }

    @Override
    public void dispose() {
        //unload all assets
        if (isLoaded()) {
            this.unload();
        }
    }

}
