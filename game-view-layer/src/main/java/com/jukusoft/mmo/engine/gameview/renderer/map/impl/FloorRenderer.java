package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.carrotsearch.hppc.ObjectArrayList;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;
import com.jukusoft.mmo.engine.gameview.renderer.map.impl.cache.CachedRenderPage;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.map.TiledLayer;
import com.jukusoft.mmo.engine.shared.utils.MathUtils;

import java.util.Objects;

public class FloorRenderer implements IRenderer {

    protected static final String LOG_TAG = "FloorRenderer";

    protected static final String CONFIG_SECTION = "WorldMapRenderer";
    protected static final String OPTION_PAGING_ENABLED = "paging_enabled";
    protected static final String OPTION_PAGING_WIDTH = "page_width";
    protected static final String OPTION_PAGING_HEIGHT = "page_height";
    protected static final int CONFIG_UPDATE_VISIBLE_PAGES_EVERY_X_FRAMES = Config.getInt(CONFIG_SECTION, "update_visible_pages_every_x_frames");
    protected static final int CONFIG_BORDER_PADDING = Config.getInt(CONFIG_SECTION, "update_visible_pages_camera_border_padding");

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

    protected final Array<CachedRenderPage> visiblePages = new Array<>(10);
    protected final Array<CachedRenderPage> allPages = new Array<>(20);
    protected final Array<CachedRenderPage> tempList = new Array<>(20);

    //frame counter
    protected int frameCounter = 0;

    protected final CameraHelper camera;
    protected GameTime time = GameTime.getInstance();

    //temp spritebatch for fbo's
    protected SpriteBatch tmpBatch = new SpriteBatch();
    protected CameraHelper tmpCamera = null;

    /**
    * default constructor
    */
    public FloorRenderer (int widthInTiles, int heightInTiles, float absX, float absY, int tileWidth, int tileHeight, CameraHelper camera, IntMap<TextureRegion> tiles) {
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

        this.camera = camera;
        this.tiles = tiles;

        this.pagingEnabled = Config.getBool(CONFIG_SECTION, OPTION_PAGING_ENABLED);
        this.pageWidth = Config.getInt(CONFIG_SECTION, OPTION_PAGING_WIDTH);
        this.pageHeight = Config.getInt(CONFIG_SECTION, OPTION_PAGING_HEIGHT);

        //register listener to allow to reload config dynamically (on CLI request)
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pagingEnabled = Config.getBool(CONFIG_SECTION, OPTION_PAGING_ENABLED));
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pageWidth = Config.getInt(CONFIG_SECTION, OPTION_PAGING_WIDTH));
        Events.addListener(Events.UI_THREAD, ClientEvents.RELOAD_CONFIG, event -> this.pageHeight = Config.getInt(CONFIG_SECTION, OPTION_PAGING_HEIGHT));

        Log.d(LOG_TAG, "paging is " + (this.pagingEnabled ? "enabled" : "disabled") + ".");

        //create camera for framebuffer objects (paging)
        this.tmpCamera = new CameraHelper(pageWidth, pageHeight);
        this.tmpCamera.forcePos();
        this.tmpCamera.update(GameTime.getInstance());
        this.tmpBatch.setProjectionMatrix(this.tmpCamera.getCombined());
    }

    @Override
    public void update(GameTime time) {
        if (this.pagingEnabled) {
            if (frameCounter % CONFIG_UPDATE_VISIBLE_PAGES_EVERY_X_FRAMES == 0) {
                //check, which maps are visible
                this.checkForNewVisiblePages(this.camera);
            }
        }

        //increment frame counter
        frameCounter = (frameCounter + 1) % CONFIG_UPDATE_VISIBLE_PAGES_EVERY_X_FRAMES;
    }

    @Override
    public void draw(GameTime time, CameraHelper camera, SpriteBatch batch) {
        if (layers.length == 0) {
            throw new IllegalStateException("no layer is registered. Maybe you haven't called addLayer() and prepare() before first render call?");
        }

        if (this.pagingEnabled) {
            float cameraX = camera.getX();
            float cameraY = camera.getY();
            float cameraXEnd = camera.getX() + camera.getViewportWidth();
            float cameraYEnd = camera.getY() + camera.getViewportHeight();

            //only draw cached pages
            for (CachedRenderPage page : this.visiblePages) {
                //check, if page is visible
                if (MathUtils.overlapping(cameraX, cameraXEnd, page.getX(), page.getX() + pageWidth) && MathUtils.overlapping(cameraY, cameraYEnd, page.getY(), page.getY() + pageHeight)) {
                    page.drawCached(batch);
                }
            }

            return;
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
    * prepare renderer and create pages, if neccessary
     *
     * this method should be called after all layers was added
    */
    protected void prepare () {
        this.layers = new LayerRenderer[this.layerRendererList.size()];

        //convert list to array for better performance at runtime
        for (int i = 0; i < this.layerRendererList.size(); i++) {
            this.layers[i] = this.layerRendererList.get(i);
        }

        //create pages, if neccessary
        this.recreatePages();
    }

    protected void recreatePages () {
        //first, delete old pages
        this.unloadAllPages();

        int mapWidth = widthInTiles * tileWidth;
        int mapHeight = heightInTiles * tileHeight;

        //check, how many pages are required
        float tmpX = mapWidth / this.pageWidth;
        float tmpY = mapHeight / this.pageHeight;

        int cols = ((int) tmpX) + (mapWidth % this.pageWidth != 0 ? 1 : 0);
        int rows = ((int) tmpY) + (mapHeight % this.pageHeight != 0 ? 1 : 0);
        Log.v(LOG_TAG, "" + rows + " rows and " + cols + " cols (pages) required for " + mapWidth + "x" + mapHeight + " pixels.");

        int pageCounter = 0;

        //create pages
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                CachedRenderPage page = new CachedRenderPage(this.absX + (x * this.pageWidth), this.absY + (y * this.pageHeight), this.pageWidth, this.pageHeight);
                this.allPages.add(page);

                pageCounter++;
            }
        }

        Log.d(LOG_TAG, "" + pageCounter + " pages created.");

        this.frameCounter = 0;
    }

    protected void unloadAllPages () {
        this.visiblePages.clear();

        for (CachedRenderPage page : this.allPages) {
            page.dispose();
        }

        this.allPages.clear();
    }

    protected void checkForNewVisiblePages (CameraHelper camera) {
        for (CachedRenderPage page : this.allPages) {
            if (isPageVisible(page)) {
                tempList.add(page);
            }
        }

        //check, which pages aren't visible anymore and unload them
        for (CachedRenderPage page : this.visiblePages) {
            if (!tempList.contains(page, false)) {
                //page isn't visible anymore, so unload them

                page.dispose();
            }
        }

        for (CachedRenderPage page : tempList) {
            if (!visiblePages.contains(page, false)) {
                //map is new visible, so we need to load the map
                if (!page.isLoaded()) {
                    //load page
                    this.loadPage(page);
                }
            }
        }

        this.visiblePages.clear();
        this.visiblePages.addAll(tempList);

        Log.v(LOG_TAG, "" + visiblePages.size + " pages visible.");

        this.tempList.clear();
    }

    protected void loadPage (CachedRenderPage page) {
        Log.v(LOG_TAG, "load page: " + page.getX() + ", " + page.getY());

        page.begin();
        tmpBatch.begin();

        tmpBatch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA, Gdx.gl.GL_ONE, Gdx.gl.GL_ONE);

        //draw layers
        for (int i = 0; i < this.layers.length; i++) {
            layers[i].draw(time, camera, this.tmpBatch);
        }

        tmpBatch.end();
        page.end();
    }

    protected boolean isPageVisible (CachedRenderPage page) {
        return MathUtils.overlapping(camera.getX() - CONFIG_BORDER_PADDING, camera.getX() + camera.getViewportWidth() + CONFIG_BORDER_PADDING, page.getX(), page.getX() + this.pageWidth) &&
                MathUtils.overlapping(camera.getY()- CONFIG_BORDER_PADDING, camera.getY() + camera.getViewportHeight() + CONFIG_BORDER_PADDING, page.getY(), page.getY() + this.pageHeight);
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

        this.unloadAllPages();
    }

}
