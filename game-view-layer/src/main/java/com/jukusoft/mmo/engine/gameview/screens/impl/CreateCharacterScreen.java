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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jukusoft.mmo.engine.applayer.utils.FPSManager;
import com.jukusoft.mmo.engine.applayer.utils.SkinFactory;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.network.PingChangedEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.data.CharacterSlot;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.utils.FilePath;
import com.jukusoft.mmo.engine.shared.version.Version;

public class CreateCharacterScreen implements IScreen {

    protected static final String LOG_TAG = "CreateCharacterScreen";
    protected static final String SECTION_NAME = "CreateCharacter";

    protected Stage stage = null;
    protected GameAssetManager assetManager = GameAssetManager.getInstance();
    protected Skin skin = null;
    protected Skin skin2 = null;
    protected Skin skin3 = null;
    protected ScreenManager<IScreen> screenManager = null;
    protected Pixmap labelColor = null;
    protected Pixmap hintLabelColor = null;

    //texture paths
    protected String bgPath = "";
    protected String newCharacterBGPath = "";

    //images
    protected Image screenBG = null;
    protected Label newCharacterLabel = null;

    //labels
    protected Label versionLabel = null;
    protected Label pingLabel = null;
    protected Label fpsLabel = null;
    protected Label hintLabel = null;

    //skin paths
    String skinJsonFile = "";

    //widgets
    protected TextField characterNameTextField = null;
    protected TextButton createButton = null;
    protected CheckBox maleCheckBox = null;
    protected CheckBox femaleCheckBox = null;
    protected ButtonGroup<CheckBox> genderButtonGroup = null;

    protected static final String TEXT_CREATE = "Create";
    protected static final String TEXT_DEFAULT = "default";

    protected int ping = 0;

    @Override
    public void onStart(ScreenManager<IScreen> screenManager) {
        this.screenManager = screenManager;

        this.bgPath = FilePath.parse(Config.get(SECTION_NAME, "background"));
        this.newCharacterBGPath = FilePath.parse(Config.get(SECTION_NAME, "newCharacterBackground"));
        this.skinJsonFile = FilePath.parse(Config.get("UISkin", "json"));

        //create UI stage
        this.stage = new Stage();

        //register event listener to update ping
        Events.addListener(Events.UI_THREAD, ClientEvents.PING_CHANGED, (EventListener<PingChangedEvent>) event -> {
            this.ping = event.ping;
        });
    }

    @Override
    public void onStop() {
        this.stage.dispose();
        this.stage = null;
    }

    @Override
    public void onResume() {
        //create skin
        this.skin = SkinFactory.createSkin(this.skinJsonFile);

        this.skin2 = SkinFactory.createSkin("./data/misc/skins/libgdx/uiskin.json");
        this.skin3 = SkinFactory.createSkin("./data/misc/skins/libgdx/uiskin.json");

        //load texures
        assetManager.load(this.bgPath, Texture.class);
        assetManager.load(this.newCharacterBGPath, Texture.class);

        assetManager.finishLoading();

        Texture bgTexture = assetManager.get(this.bgPath, Texture.class);
        this.screenBG = new Image(bgTexture);

        Texture newCharacterBGTexture = assetManager.get(this.newCharacterBGPath, Texture.class);
        this.newCharacterLabel = new Label("     Create new Character ", this.skin);
        this.newCharacterLabel.setWidth(newCharacterBGTexture.getWidth());
        this.newCharacterLabel.setHeight(newCharacterBGTexture.getHeight());
        this.newCharacterLabel.getStyle().background = new TextureRegionDrawable(new TextureRegion(newCharacterBGTexture, newCharacterBGTexture.getWidth(), newCharacterBGTexture.getHeight()));
        this.newCharacterLabel.invalidate();

        //add widgets to stage
        stage.addActor(screenBG);
        stage.addActor(newCharacterLabel);

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

        //text fields
        this.characterNameTextField = new TextField("", this.skin2);
        this.characterNameTextField.setFocusTraversal(true);
        this.characterNameTextField.setMessageText("Character Name");
        stage.addActor(characterNameTextField);

        //gender radio buttons
        this.maleCheckBox = new CheckBox("Male", this.skin2);
        this.maleCheckBox.setChecked(true);
        stage.addActor(maleCheckBox);
        this.femaleCheckBox = new CheckBox("Female", this.skin2);
        stage.addActor(femaleCheckBox);

        this.genderButtonGroup = new ButtonGroup<>(maleCheckBox, femaleCheckBox);

        //set toggle mode
        genderButtonGroup.setMaxCheckCount(1);
        genderButtonGroup.setMinCheckCount(1);

        //it may be useful to use this method:
        genderButtonGroup.setUncheckLast(true); //If true, when the maximum number of buttons are checked and an additional button is checked, the last button to be checked is unchecked so that the maximum is not exceeded.

        //submit button
        this.createButton = new TextButton(TEXT_CREATE, this.skin);
        this.createButton.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                Log.v(LOG_TAG, "create character button clicked.");

                //get values
                String name = characterNameTextField.getText();

                //check, if name is empty
                if (name.isEmpty()) {
                    characterNameTextField.getStyle().messageFontColor = Color.RED;
                    characterNameTextField.invalidate();

                    Log.v(LOG_TAG, "character name field is empty.");

                    return;
                }

                Log.v(LOG_TAG, "try to create new character...");

                createButton.setText("Loading...");
                createButton.setDisabled(true);

                //reset loaded character slot list, because a new character will be added
                //game.getCharacterSlots().reset();

                //try to create character on server
                /*CharacterSlot character = CharacterSlot.create(name, (maleCheckBox.isChecked() ? CharacterSlot.GENDER.MALE : CharacterSlot.GENDER.FEMALE), TEXT_DEFAULT, TEXT_DEFAULT, TEXT_DEFAULT, TEXT_DEFAULT);
                game.getCharacterSlots().createCharacter(character, res -> {
                    if (res == CharacterSlots.CREATE_CHARACTER_RESULT.DUPLICATE_NAME) {
                        //character name already exists on server
                        hintLabel.setText(" Error! Character name already exists!");
                        hintLabel.setVisible(true);
                        hintLabel.invalidate();

                        createButton.setText(TEXT_CREATE);
                        createButton.setDisabled(false);
                    } else if (res == CharacterSlots.CREATE_CHARACTER_RESULT.INVALIDE_NAME) {
                        //server error
                        hintLabel.setText(" Invalide character name! Only A-Z, a-z and 0-9 is allowed.");
                        hintLabel.setVisible(true);
                        hintLabel.invalidate();

                        createButton.setText(TEXT_CREATE);
                        createButton.setDisabled(false);
                    } else if (res == CharacterSlots.CREATE_CHARACTER_RESULT.SERVER_ERROR) {
                        //server error
                        hintLabel.setText(" Server Error!");
                        hintLabel.setVisible(true);
                        hintLabel.invalidate();

                        createButton.setText(TEXT_CREATE);
                        createButton.setDisabled(false);
                    } else if (res == CharacterSlots.CREATE_CHARACTER_RESULT.CLIENT_ERROR) {
                        //server error
                        hintLabel.setText(" Client Error!");
                        hintLabel.setVisible(true);
                        hintLabel.invalidate();

                        createButton.setText(TEXT_CREATE);
                        createButton.setDisabled(false);
                    } else if (res == CharacterSlots.CREATE_CHARACTER_RESULT.SUCCESS) {
                        //character was created, go back to character screen
                        Platform.runOnUIThread(() -> screenManager.leaveAllAndEnter(Screens.CHARACTER_SELECTION));
                    }
                });*/
            }
        });
        stage.addActor(createButton);

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
        assetManager.unload(this.newCharacterBGPath);

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
        newCharacterLabel.setX((width - newCharacterLabel.getWidth()) / 2);
        newCharacterLabel.setY((height - newCharacterLabel.getHeight()) / 2 + 250);

        versionLabel.setX(20);
        versionLabel.setY(20);

        pingLabel.setX(20);
        pingLabel.setY(height - 50f);

        fpsLabel.setX(20);
        fpsLabel.setY(height - 80f);

        hintLabel.setX((width - hintLabel.getWidth()) / 2);
        hintLabel.setY((height - hintLabel.getHeight()) / 2 - 200);

        float startY = (height - characterNameTextField.getHeight()) / 2;

        characterNameTextField.setWidth(400);
        characterNameTextField.setX((width - characterNameTextField.getWidth()) / 2);
        characterNameTextField.setY(startY);

        float paddingBetweenCheckBoxes = 50;
        float checkBoxX = (width - maleCheckBox.getWidth() - femaleCheckBox.getWidth() - paddingBetweenCheckBoxes) / 2;

        maleCheckBox.setX(checkBoxX);
        maleCheckBox.setY(startY - 50);
        femaleCheckBox.setX(checkBoxX + maleCheckBox.getWidth() + paddingBetweenCheckBoxes);
        femaleCheckBox.setY(startY - 50);

        createButton.setWidth(400);
        createButton.setX((width - createButton.getWidth()) / 2);
        createButton.setY(startY - 100);

        //invalidate widgets, because width and height was changed
        characterNameTextField.invalidate();
        hintLabel.invalidate();
        createButton.invalidate();
    }

    @Override
    public void update(ScreenManager<IScreen> screenManager) {
        //set ping
        this.pingLabel.setText("Ping: " + this.ping + "");

        //set fps
        this.fpsLabel.setText("FPS: " + FPSManager.getInstance().getFPS());
    }

    @Override
    public void draw() {
        //show the character selection screen
        stage.act();
        stage.draw();
    }

}
