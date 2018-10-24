package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.network.ServerManager;
import com.jukusoft.mmo.engine.applayer.utils.SkinFactory;
import com.jukusoft.mmo.engine.applayer.version.Version;
import com.jukusoft.mmo.engine.gameview.assetmanager.GameAssetManager;
import com.jukusoft.mmo.engine.gameview.screens.IScreen;
import com.jukusoft.mmo.engine.gameview.screens.ScreenManager;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class SelectServerScreen implements IScreen {

    protected Stage stage = null;
    protected GameAssetManager assetManager = GameAssetManager.getInstance();
    protected Skin skin = null;
    protected Skin skin2 = null;
    protected ScreenManager<IScreen> screenManager = null;
    protected Pixmap labelColor = null;

    protected String bgPath = "";
    protected String logoPath = "";

    //images
    protected Image screenBG = null;
    protected Image logo = null;

    //label
    protected Label versionLabel = null;

    protected TextButton[] buttons;

    //https://github.com/libgdx/libgdx/wiki/Hiero

    @Override
    public void onStart(ScreenManager<IScreen> screenManager) {
        this.screenManager = screenManager;

        this.bgPath = Config.get("SelectServer", "background");
        this.logoPath = Config.get("SelectServer", "logo");

        //create skin
        String atlasFile = Config.get("UI.Skin", "atlas");
        String jsonFile = Config.get("UI.Skin", "json");
        Log.v("SelectServerScreen", "create skin, atlas file: " + atlasFile + ", json file: " + jsonFile);
        this.skin = SkinFactory.createSkin(jsonFile);

        this.skin2 = SkinFactory.createSkin("./data/misc/skins/libgdx/uiskin.json");

        //create UI stage
        this.stage = new Stage();
    }

    @Override
    public void onStop() {
        this.skin.dispose();
        this.skin = null;

        this.skin2.dispose();
        this.skin2 = null;
    }

    @Override
    public void onResume() {
        //load texures
        assetManager.load(this.bgPath, Texture.class);
        assetManager.load(this.logoPath, Texture.class);

        assetManager.finishLoading();

        Texture bgTexture = assetManager.get(this.bgPath, Texture.class);
        this.screenBG = new Image(bgTexture);

        Texture logoTexture = assetManager.get(this.logoPath, Texture.class);
        this.logo = new Image(logoTexture);

        //add widgets to stage
        stage.addActor(screenBG);
        stage.addActor(logo);

        this.buttons = new TextButton[ServerManager.getInstance().listServers().size()];
        int i = 0;

        //create a button for every server
        for (ServerManager.Server server : ServerManager.getInstance().listServers()) {
            TextButton button = new TextButton(server.title + (server.online ? "" : " (offline)"), this.skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked (InputEvent event, float x, float y) {
                    if (button.isDisabled()) return;

                    Log.i("SelectServer", "select server: " + server.ip + ":" + server.port);

                    //select server
                    /*ServerManager.getInstance().setSelectServer(server);

                    //disable button
                    button.setDisabled(true);
                    button.setText("Connecting...");

                    //invalidate, because size has changed
                    button.invalidate();

                    //hide all other buttons
                    for (int k = 0; k < buttons.length; k++) {
                        TextButton btn = buttons[k];

                        //only hide all other buttons, not this button itself
                        if (btn != button) {
                            btn.setVisible(false);
                        }
                    }

                    //connect to server
                    ServerManager.getInstance().connect(success -> {
                        if (success) {
                            Platform.runOnUIThread(() -> {
                                LocalLogger.print("connection established successfully, go to login screen now.");

                                //go to login screen
                                screenManager.leaveAllAndEnter(Screens.LOGIN_SCREEN);
                            });
                        } else {
                            button.setText(server.title + " (Not reachable)");

                            //invalidate, because size has changed
                            button.invalidate();

                            //show all other buttons
                            for (int k = 0; k < buttons.length; k++) {
                                TextButton btn = buttons[k];

                                //only hide all other buttons, not this button itself
                                if (btn != button) {
                                    btn.setVisible(true);
                                }
                            }
                        }
                    });*/
                }
            });

            button.setDisabled(!server.online);

            this.buttons[i] = button;
            this.stage.addActor(button);
            i++;
        }

        //get client version
        Version version = Version.getInstance();

        this.versionLabel = new Label("Version: " + version.getFullVersion(), this.skin2);

        //set label background color
        labelColor = new Pixmap((int) this.versionLabel.getWidth(), (int) this.versionLabel.getHeight(), Pixmap.Format.RGBA8888);
        labelColor.setColor(Color.valueOf("#36581a"));
        labelColor.fill();
        this.versionLabel.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        stage.addActor(versionLabel);

        //set input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void onPause() {
        assetManager.unload(this.bgPath);
        assetManager.unload(this.logoPath);

        labelColor.dispose();
    }

    @Override
    public void onResize(int width, int height) {
        System.out.println("onResize: " + width + "x" + height);

        stage.getViewport().setScreenWidth(width);
        stage.getViewport().setScreenHeight(height);
        stage.getViewport().update(width, height, true);
        stage.getViewport().setScreenPosition(0, 0);
        stage.getViewport().setScreenSize(width, height);

        //make the background fill the screen
        screenBG.setSize(width, height);
        screenBG.invalidate();

        //place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 200);
        logo.invalidate();

        float startY = (height - logo.getHeight()) / 2 + 50;

        for (int i = 0; i < this.buttons.length; i++) {
            buttons[i].setX((width - buttons[i].getWidth()) / 2);
            buttons[i].setY(startY - i * 50);
            buttons[i].invalidate();
        }

        versionLabel.setX(20);
        versionLabel.setY(20);
    }

    @Override
    public void update(ScreenManager<IScreen> screenManager) {

    }

    @Override
    public void draw() {
        //show the loading screen
        stage.act();
        stage.draw();
    }

}
