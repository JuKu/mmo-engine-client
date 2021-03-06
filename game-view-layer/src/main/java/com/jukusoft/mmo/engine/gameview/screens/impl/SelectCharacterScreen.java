package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jukusoft.mmo.engine.applayer.utils.FPSManager;
import com.jukusoft.mmo.engine.applayer.utils.SkinFactory;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.gameview.screens.Screens;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.init.CharacterListReceivedEvent;
import com.jukusoft.mmo.engine.shared.client.events.init.EnterGameWorldRequestEvent;
import com.jukusoft.mmo.engine.shared.client.events.init.EnterGameWorldResponseEvent;
import com.jukusoft.mmo.engine.shared.client.events.load.LoadMapEvent;
import com.jukusoft.mmo.engine.shared.client.events.network.PingChangedEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.data.CharacterSlot;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.EnterGameWorldResponse;
import com.jukusoft.mmo.engine.shared.utils.FilePath;
import com.jukusoft.mmo.engine.shared.version.Version;

import java.util.List;
import java.util.Objects;

public class SelectCharacterScreen implements IScreen {

    protected static final String SECTION_NAME = "SelectCharacter";
    protected static final String LOGIN_TAG = "Login";

    protected Stage stage = null;
    protected GameAssetManager assetManager = GameAssetManager.getInstance();
    protected Skin skin = null;
    protected Skin skin2 = null;
    protected Skin skin3 = null;
    protected ScreenManager<IScreen> screenManager = null;
    protected Pixmap labelColor = null;
    protected Pixmap hintLabelColor = null;

    protected boolean loaded = false;

    //texture paths
    protected String bgPath = "";
    protected String logoPath = "";
    protected String slotBGPath = "";
    protected String slotBGHoveredPath = "";
    protected String newSlotPath = "";
    protected String newSlotHoverPath = "";

    //images
    protected Image screenBG = null;
    protected Image logo = null;

    //labels
    protected Label versionLabel = null;
    protected Label pingLabel = null;
    protected Label fpsLabel = null;
    protected Label hintLabel = null;

    //skin paths
    String skinJsonFile = "";

    protected Texture slotBG = null;
    protected Texture slotBGHover = null;
    protected Texture newSlotBG = null;
    protected Texture newSlotHoverBG = null;

    protected Button[] slots = new Button[Config.getInt("Game", "maxSlots")];

    protected int ping = 0;
    protected CharacterSlot[] slotArray = null;
    protected static final int MAX_CHARACTER_SLOTS = 5;

    @Override
    public void onStart(ScreenManager<IScreen> screenManager) {
        this.screenManager = screenManager;

        //get paths from config
        this.bgPath = FilePath.parse(Config.get(SECTION_NAME, "background"));
        this.logoPath = FilePath.parse(Config.get(SECTION_NAME, "logo"));
        this.slotBGPath = FilePath.parse(Config.get(SECTION_NAME, "slotBackground"));
        this.slotBGHoveredPath = FilePath.parse(Config.get(SECTION_NAME, "slotBackgroundHover"));
        this.newSlotPath = FilePath.parse(Config.get(SECTION_NAME, "newSlot"));
        this.newSlotHoverPath = FilePath.parse(Config.get(SECTION_NAME, "newSlotHover"));
        this.skinJsonFile = FilePath.parse(Config.get("UISkin", "json"));

        //create UI stage
        this.stage = new Stage();

        //register event listener to update ping
        Events.addListener(Events.UI_THREAD, ClientEvents.PING_CHANGED, (EventListener<PingChangedEvent>) event -> this.ping = event.ping);

        Events.addListener(Events.UI_THREAD, ClientEvents.CHARACTER_LIST_RECEIVED, (EventListener<CharacterListReceivedEvent>) event -> {
            Log.i(LOGIN_TAG, "character list event received.");

            //set slots in select character screen
            this.setSlots(event.slots);
        });

        Events.addListener(Events.UI_THREAD, ClientEvents.ENTERED_GAME_WORLD, (EventListener<EnterGameWorldResponseEvent>) event -> {
            if (event.resultCode == EnterGameWorldResponse.RESULT_CODE.SUCCESS) {
                Log.i(LOGIN_TAG, "player entered game world.");

                //show success message on UI
                this.hintLabel.setText("Success! Waiting...");
                this.hintLabel.setVisible(true);
                this.hintLabel.invalidate();
            } else {
                //an error occured
                Log.w(LOGIN_TAG, "Error! Player coulnd't enter game world. cause: " + event.resultCode.name());

                //show error message on UI
                this.hintLabel.setText("Error! " + event.resultCode.name() + " Please restart client!");
                this.hintLabel.setVisible(true);
                this.hintLabel.invalidate();
            }
        });

        Events.addListener(Events.UI_THREAD, ClientEvents.LOAD_MAP, (EventListener<LoadMapEvent>) event -> {
            Log.i(LOGIN_TAG, "go to map loading screen now...");

            //set region title and enter region loading screen
            ((LoadRegionScreen) screenManager.getScreenByName(Screens.LOAD_REGION)).setRegion(event.regionID, event.instanceID, event.regionTitle);
            screenManager.leaveAllAndEnter(Screens.LOAD_REGION);
        });
    }

    @Override
    public void onStop() {
        this.stage.dispose();
        this.stage = null;
    }

    @Override
    public void onResume() {
        this.loaded = false;

        //create skin
        this.skin = SkinFactory.createSkin(this.skinJsonFile);
        Objects.requireNonNull(this.skin);

        this.skin2 = SkinFactory.createSkin("./data/misc/skins/libgdx/uiskin.json");
        this.skin3 = SkinFactory.createSkin("./data/misc/skins/libgdx/uiskin.json");

        //load texures
        assetManager.load(this.bgPath, Texture.class);
        assetManager.load(this.logoPath, Texture.class);
        assetManager.load(this.slotBGPath, Texture.class);
        assetManager.load(this.slotBGHoveredPath, Texture.class);
        assetManager.load(this.newSlotPath, Texture.class);
        assetManager.load(this.newSlotHoverPath, Texture.class);

        assetManager.finishLoading();

        Texture bgTexture = assetManager.get(this.bgPath, Texture.class);
        this.screenBG = new Image(bgTexture);

        Texture logoTexture = assetManager.get(this.logoPath, Texture.class);
        this.logo = new Image(logoTexture);

        this.slotBG = assetManager.get(this.slotBGPath, Texture.class);
        this.slotBGHover = assetManager.get(this.slotBGHoveredPath, Texture.class);
        this.newSlotBG = assetManager.get(this.newSlotPath, Texture.class);
        this.newSlotHoverBG = assetManager.get(this.newSlotHoverPath, Texture.class);

        //add widgets to stage
        stage.addActor(screenBG);
        //stage.addActor(logo);

        //get client version
        Version version = Version.getInstance();

        this.versionLabel = new Label("Version: " + version.getFullVersion(), this.skin2);

        //set label background color
        labelColor = new Pixmap((int) this.versionLabel.getWidth() + 100, (int) this.versionLabel.getHeight(), Pixmap.Format.RGBA8888);
        labelColor.setColor(Color.valueOf("#36581a"));
        labelColor.fill();
        this.versionLabel.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        stage.addActor(versionLabel);

        //ping label
        this.pingLabel = new Label("Ping: n/a", this.skin2);
        this.pingLabel.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        stage.addActor(pingLabel);

        //fps label
        this.fpsLabel = new Label("FPS: n/a", this.skin2);
        this.fpsLabel.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        stage.addActor(fpsLabel);

        //hint label (e.q. for error messages)
        this.hintLabel = new Label("Hints", this.skin3);
        this.hintLabel.setWidth(400);
        this.hintLabel.getStyle().fontColor = Color.RED;

        //set label background color
        hintLabelColor = new Pixmap((int) this.versionLabel.getWidth() + 100, (int) this.versionLabel.getHeight(), Pixmap.Format.RGBA8888);
        hintLabelColor.setColor(Color.valueOf("#FFFFFF"));
        hintLabelColor.fill();
        this.hintLabel.getStyle().background = new Image(new Texture(hintLabelColor)).getDrawable();

        stage.addActor(hintLabel);

        //hide hint label
        this.hintLabel.setVisible(false);

        //set input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void onPause() {
        this.skin.dispose();
        this.skin = null;

        this.skin2.dispose();
        this.skin2 = null;

        this.skin3.dispose();
        this.skin3 = null;

        assetManager.unload(this.bgPath);
        assetManager.unload(this.logoPath);
        assetManager.unload(this.slotBGPath);
        assetManager.unload(this.slotBGHoveredPath);
        assetManager.unload(this.newSlotPath);
        assetManager.unload(this.newSlotHoverPath);

        this.labelColor.dispose();
        this.labelColor = null;
        this.hintLabelColor.dispose();
        this.hintLabelColor = null;
    }

    @Override
    public void onResize(int width, int height) {
        stage.getViewport().setScreenWidth(width);
        stage.getViewport().setScreenHeight(height);

        //make the background fill the screen
        screenBG.setSize(width, height);

        //place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 200);

        versionLabel.setX(20);
        versionLabel.setY(20);

        pingLabel.setX(20);
        pingLabel.setY(height - 50f);

        fpsLabel.setX(20);
        fpsLabel.setY(height - 80f);

        hintLabel.setX((width - hintLabel.getWidth()) / 2);
        hintLabel.setY((height - hintLabel.getHeight()) / 2 - 200);

        float startY = (height - 100) / 2f + 200f;

        for (int i = 0; i < MAX_CHARACTER_SLOTS; i++) {
            if (this.slots[i] == null) {
                continue;
            }

            this.slots[i].setX((width - slots[i].getWidth()) / 2);
            this.slots[i].setY(startY - (i * 110));
        }
    }

    /**
    * add slots to ui
     *
     * this method is called if character list response was received from proxy server
    */
    protected void init (CharacterSlot[] slots) {
        Log.v("SelectCharacterScreen", "init slots, length: " + slots.length);

        for (int i = 0; i < MAX_CHARACTER_SLOTS; i++) {
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(this.slotBG, this.slotBG.getWidth(), this.slotBG.getHeight()));
            Drawable drawableHover = new TextureRegionDrawable(new TextureRegion(this.slotBGHover, this.slotBG.getWidth(), this.slotBG.getHeight()));

            if (slots[i] == null) {
                //slot is empty
                drawable = new TextureRegionDrawable(new TextureRegion(this.newSlotBG, this.newSlotBG.getWidth(), this.newSlotBG.getHeight()));
                drawableHover = new TextureRegionDrawable(new TextureRegion(this.newSlotHoverBG, this.newSlotBG.getWidth(), this.newSlotBG.getHeight()));

                ImageButton btn = new ImageButton(drawable, drawableHover, drawableHover);
                btn.getStyle().over = drawableHover;
                btn.getStyle().imageOver = drawableHover;
                btn.getStyle().checkedOver = drawableHover;
                btn.getStyle().imageCheckedOver = drawableHover;

                this.slots[i] = btn;
            } else {
                TextButton btn = new TextButton(slots[i].getName(), this.skin);
                btn.getStyle().up = drawable;
                btn.getStyle().down = drawableHover;
                btn.getStyle().over = drawableHover;
                btn.getStyle().checked = drawableHover;
                btn.getStyle().checkedOver = drawableHover;

                btn.setWidth(this.slotBG.getWidth());
                btn.setHeight(this.slotBG.getHeight());

                this.slots[i] = btn;
            }

            this.slots[i].setChecked(false);

            if (slots[i] == null) {
                this.slots[i].addListener(new ClickListener() {
                    @Override
                    public void clicked (InputEvent event, float x, float y) {
                        //go to create character screen
                        screenManager.leaveAllAndEnter(Screens.CREATE_CHARACTER);
                    }
                });
            } else {
                final int slotID = i;

                this.slots[i].addListener(new ClickListener() {
                    @Override
                    public void clicked (InputEvent event, float x, float y) {
                        Log.v(SECTION_NAME, "select character button clicked.");

                        //disable button and set text
                        ((TextButton) SelectCharacterScreen.this.slots[slotID]).setText("Wait...");
                        SelectCharacterScreen.this.slots[slotID].setDisabled(true);

                        //fire event
                        EnterGameWorldRequestEvent event1 = Pools.get(EnterGameWorldRequestEvent.class);
                        event1.cid = slots[slotID].getCID();
                        Events.queueEvent(event1);

                        //hide all other buttons buttons
                        for (int i = 0; i < MAX_CHARACTER_SLOTS; i++) {
                            if (i != slotID) {
                                (SelectCharacterScreen.this.slots[i]).setVisible(false);
                            }
                        }
                    }
                });
            }

            stage.addActor(this.slots[i]);
        }

        this.onResize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update(ScreenManager<IScreen> screenManager) {
        //set ping
        this.pingLabel.setText("Ping: " + this.ping + "");

        //set fps
        this.fpsLabel.setText("FPS: " + FPSManager.getInstance().getFPS());

        if (!loaded) {
            if (this.slotArray != null) {
                init(this.slotArray);

                loaded = true;
            } else {
                return;
            }
        }
    }

    @Override
    public void draw() {
        //show the character selection screen
        stage.act();
        stage.draw();
    }

    public void setSlots (List<CharacterSlot> slots) {
        this.slotArray = slots.toArray(new CharacterSlot[MAX_CHARACTER_SLOTS]);
        this.loaded = false;
    }
    
}
