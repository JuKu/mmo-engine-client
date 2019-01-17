package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.cli.ChangeConfigEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.map.TiledLayer;

import java.util.Objects;

public class FloorRenderer implements IRenderer {

    protected static final String LOG_TAG = "FloorRenderer";

    protected static final String CONFIG_SECTION = "WorldMapRenderer";
    protected static final String OPTION_PAGING_ENABLED = "paging_enabled";
    protected static final String OPTION_PAGING_WIDTH = "page_width";
    protected static final String OPTION_PAGING_HEIGHT = "page_height";

    //list with all layers
    protected ObjectArrayList<LayerRenderer> layerRendererList = new ObjectArrayList<>();
    protected ObjectArrayList<LayerRenderer> afterPlayerlayerRendererList = new ObjectArrayList<>();

    protected ObjectArrayList<TiledLayer> layerList = new ObjectArrayList<>(10);

    protected LayerRenderer[] layers = null;//cache array for better performance
    protected LayerRenderer[] afterPlayerLayers = null;//cache array for better performance

    //config values
    protected boolean pagingEnabled = false;
    protected int pageWidth = 0;
    protected int pageHeight = 0;

    //dimension of floor
    protected final int widthInTiles;
    protected final int heightInTiles;

    //absolute position of map
    protected final float absX;
    protected final float absY;

    //dimension of a single tile
    protected final int tileWidth;
    protected final int tileHeight;

    //map with tileIDs - texture region
    protected final IntMap<TextureRegion> tiles;

    /**
    * default constructor
    */
    public FloorRenderer (int widthInTiles, int heightInTiles, float absX, float absY, int tileWidth, int tileHeight, IntMap<TextureRegion> tiles) {
        Objects.requireNonNull(tiles);

        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.absX = absX;
        this.absY = absY;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        if (tiles.size <= 0) {
            throw new IllegalArgumentException("tiles map is empty!");
        }

        this.tiles = tiles;

        this.pagingEnabled = Config.getBool(CONFIG_SECTION, OPTION_PAGING_ENABLED);
        this.pageWidth = Config.getInt(CONFIG_SECTION, OPTION_PAGING_WIDTH);
        this.pageHeight = Config.getInt(CONFIG_SECTION, OPTION_PAGING_HEIGHT);

        //register listener to allow to reload config dynamically (on CLI request)
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pagingEnabled = Config.getBool(CONFIG_SECTION, OPTION_PAGING_ENABLED));
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pageWidth = Config.getInt(CONFIG_SECTION, OPTION_PAGING_WIDTH));
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pageHeight = Config.getInt(CONFIG_SECTION, OPTION_PAGING_HEIGHT));
    }

    @Override
    public void update(GameTime time) {
        //
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        if (layers.length == 0) {
            throw new IllegalStateException("no layer is registered. Maybe you haven't called addLayer() and prepare() before first render call?");
        }

        //draw layers
        for (int i = 0; i < this.layers.length; i++) {
            layers[i].draw(time, camera, batch);
        }
    }

    public void drawAfterPlayer (GameTime time, CameraHelper camera, SpriteBatch batch) {
        //draw layers
        for (int i = 0; i < this.afterPlayerLayers.length; i++) {
            afterPlayerLayers[i].draw(time, camera, batch);
        }
    }

    public void addLayer (TiledLayer layer) {
        Log.v(LOG_TAG, "added layer: " + layer.getName());
        this.layerList.add(layer);

        LayerRenderer layerRenderer = new LayerRenderer(widthInTiles, heightInTiles, absX, absY, tileWidth, tileHeight);

        //set texture regions (this means set cells)
        prepareLayer(layer, layerRenderer);

        this.layerRendererList.add(layerRenderer);
    }

    protected void prepareLayer (TiledLayer layer, LayerRenderer renderer) {
        //set cells of layers
        int[] tileIDs = layer.getTileIDs();

        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                //calculate index
                int index = y * layer.getWidth() + x;

                //get id of tile to draw
                int tileID = tileIDs[index];

                if (tileID == 0) {
                    //we dont have to set transparent cells without tiles
                    continue;
                }

                //get texture region of tile
                TextureRegion region = this.tiles.get(tileID);

                if (region != null) {
                    renderer.setCell(x, y, new TextureRegion(region.getTexture(), region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight()));
                } else {
                    Log.w(LOG_TAG, "Cannot found tileID on tilesets: " + tileID);
                }
            }
        }

        //set visible flag
        renderer.setVisible(layer.isVisible());
    }

    /**
    * prepare renderer
     *
     * this method should be called after all layers was added
    */
    protected void prepare () {
        this.layers = new LayerRenderer[this.layerRendererList.size()];

        //convert list to array for better performance at runtime
        for (int i = 0; i < this.layerRendererList.size(); i++) {
            this.layers[i] = this.layerRendererList.get(i);
        }
    }

    @Override
    public void dispose() {
        this.layers = null;
        this.afterPlayerLayers = null;

        //dispose layers
        this.layerRendererList.iterator().forEachRemaining(layer -> {
            layer.value.dispose();
        });
        this.layerRendererList = null;
    }

}