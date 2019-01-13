package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.carrotsearch.hppc.ObjectArrayList;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.map.MapLoaderException;
import com.jukusoft.mmo.engine.gameview.renderer.map.MapRenderer;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.map.TiledMap;
import com.jukusoft.mmo.engine.shared.map.parser.TiledParserException;
import com.jukusoft.mmo.engine.shared.map.parser.TmxMapParser;
import com.jukusoft.mmo.engine.shared.map.tileset.TextureTileset;
import com.jukusoft.mmo.engine.shared.map.tileset.Tileset;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class TmxMapRenderer implements MapRenderer {

    protected static final String LOG_TAG = "TmxMapRenderer";

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

    //flag, if renderer is ready to render
    protected boolean loading = false;
    protected boolean loaded = false;

    protected TiledMap map = null;

    //current floor to render
    protected int floor = 1;

    protected Array<String> loadedAssets = null;

    protected final GameAssetManager assetManager;

    public TmxMapRenderer (GameAssetManager assetManager, String tmxFile, float absX, float absY, int widthInTiles, int heightInTiles, int tileWidth, int tileHeight) {
        Objects.requireNonNull(assetManager);
        Objects.requireNonNull(tmxFile);

        this.assetManager = assetManager;
        this.filePath = FilePath.parse(tmxFile);

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
                this.loaded = true;

                Log.v(LOG_TAG, "all assets are loaded now.");
            }
        }
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
        //
    }

    @Override
    public void dispose() {
        //unload all assets
        if (isLoaded()) {
            this.unload();
        }
    }

}
