package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jukusoft.mmo.engine.applayer.utils.SkinFactory;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.utils.LoadingBar;
import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

public class LoadRegionScreen implements IScreen {

    protected Stage stage;

    protected GameAssetManager assetManager = GameAssetManager.getInstance();

    //paths
    protected String WALLPAPER_PATH = "";
    protected String TEXTURE_ATLAS_PATH = Cache.getInstance().getPath() + "assets/loading/loading.pack.atlas";
    protected String SKIN_JSON_PATH = "";

    protected static final String CONFIG_SECTION = "LoadRegion";

    //background texture
    protected Texture bgTexture = null;

    //skins
    protected Skin skin = null;

    //widgets
    protected Image logo;
    protected Image loadingFrame;
    protected Image loadingBarHidden;
    protected Image screenBg;
    protected Image loadingBg;
    protected Label label;

    protected Image header;
    protected Image footer;

    //loading bar
    protected Actor loadingBar;

    protected float startX = 0;
    protected float endX = Gdx.graphics.getBackBufferWidth();
    protected float percent = 0;

    //region data
    protected String regionTitle = "######## ???? #########";
    protected long regionID = 0;
    protected int instanceID = 0;

    @Override
    public void onStart(ScreenManager<IScreen> screenManager) {
        this.stage = new Stage();
    }

    @Override
    public void onStop() {
        this.stage.dispose();
        this.stage = null;
    }

    public void setRegion (long regionID, int instanceID, String regionTitle) {
        this.regionID = regionID;
        this.instanceID = instanceID;
        this.regionTitle = regionTitle;
    }

    @Override
    public void onResume() {
        WALLPAPER_PATH = FilePath.parse(Config.get(CONFIG_SECTION, "background"));
        TEXTURE_ATLAS_PATH = FilePath.parse(Config.get(CONFIG_SECTION, "textureAtlas"));
        SKIN_JSON_PATH = FilePath.parse(Config.get(CONFIG_SECTION, "json"));

        //load assets
        assetManager.load(WALLPAPER_PATH, Texture.class);
        assetManager.load(TEXTURE_ATLAS_PATH, TextureAtlas.class);

        //wait until they are finished loading
        assetManager.finishLoading();

        //get texture
        this.bgTexture = assetManager.get(WALLPAPER_PATH, Texture.class);

        // Get our textureatlas from the manager
        TextureAtlas atlas = assetManager.get(TEXTURE_ATLAS_PATH, TextureAtlas.class);

        //create skin
        this.skin = SkinFactory.createSkin(SKIN_JSON_PATH);

        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("logo_large"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(this.bgTexture);
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        this.header = new Image(atlas.findRegion("footer_bg"));
        this.footer = new Image(atlas.findRegion("footer_bg"));

        //add label with region title
        this.label = new Label("   Loading " + this.regionTitle + "...   ", this.skin);
        this.label.getStyle().background = new TextureRegionDrawable(atlas.findRegion("label"));
        this.label.setHeight(50);
        this.label.invalidate();

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(header);
        stage.addActor(footer);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
        stage.addActor(label);

        //dont accept input while loading region
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void onPause() {
        assetManager.unload(WALLPAPER_PATH);
        assetManager.unload(TEXTURE_ATLAS_PATH);

        this.skin.dispose();
        this.skin = null;
    }

    @Override
    public void onResize(int width, int height) {
        stage.getViewport().setScreenWidth(width);
        stage.getViewport().setScreenHeight(height);
        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;
        stage.getCamera().update();

        // Make the background fill the screen
        screenBg.setSize(width, height);

        float yOffset = 90;

        // Place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + yOffset);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY(0);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);

        label.setX((width - label.getWidth()) / 2);
        label.setY((height - label.getHeight()) / 2 + yOffset + 220);

        //set position of header and footer
        header.setWidth(width);
        header.setHeight(50);
        header.setX(0);
        header.setY(height - header.getHeight());

        footer.setWidth(width);
        footer.setHeight(60);
        footer.setX(0);
        footer.setY(0);
    }

    @Override
    public void update(ScreenManager<IScreen> screenManager) {
        //
    }

    @Override
    public void draw() {
        //interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, this.getCurrentProgress(), 0.1f);

        //update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);

        //invalidate image, because size has changed
        loadingBg.invalidate();

        //show the loading screen
        stage.act();
        stage.draw();
    }

    protected float getCurrentProgress () {
        return 0.1f;
    }

}
