package com.jukusoft.mmo.engine.gameview.renderer.map.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.carrotsearch.hppc.ObjectArrayList;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.renderer.IRenderer;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.cli.ChangeConfigEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;

public class FloorRenderer implements IRenderer {

    protected static final String CONFIG_SECTION = "WorldMapRenderer";
    protected static final String OPTION_PAGING_ENABLED = "paging_enabled";
    protected static final String OPTION_PAGING_WIDTH = "page_width";
    protected static final String OPTION_PAGING_HEIGHT = "page_height";

    //list with all layers
    protected ObjectArrayList<LayerRenderer> layerRendererList = new ObjectArrayList<>();
    protected ObjectArrayList<LayerRenderer> afterPlayerlayerRendererList = new ObjectArrayList<>();

    protected LayerRenderer[] layers = null;//cache array for better performance
    protected LayerRenderer[] afterPlayerLayers = null;//cache array for better performance

    //config values
    protected boolean pagingEnabled = false;
    protected int pageWidth = 0;
    protected int pageHeight = 0;

    /**
    * default constructor
    */
    public FloorRenderer () {
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
