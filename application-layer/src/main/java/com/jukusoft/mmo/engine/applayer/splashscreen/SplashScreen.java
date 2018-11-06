package com.jukusoft.mmo.engine.applayer.splashscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.utils.FilePath;

public class SplashScreen {

    protected SpriteBatch batch = null;

    protected Texture bgTexture = null;
    protected Texture logoTexture = null;
    protected Texture animationTexture = null;

    protected float logoOffsetX = 0;
    protected float logoOffsetY = 0;
    protected float animX = 0;
    protected float animY = 0;
    protected float animWidth = 0;
    protected float animHeight = 0;

    protected boolean animEnabled = false;
    protected TextureRegion anim = null;

    protected float degree = 0;
    protected float rotationSpeed = 10;

    protected static final String SPLASHSCREEN_STR = "Splashscreen";

    public SplashScreen () {
        //
    }

    public void load () {
        this.batch = new SpriteBatch();

        //set width & height
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Config.getInt(SPLASHSCREEN_STR, "width"), Config.getInt(SPLASHSCREEN_STR, "height"));

        //load images
        this.bgTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get(SPLASHSCREEN_STR, "bgImage"))));
        this.logoTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get(SPLASHSCREEN_STR, "logoImage"))));
        this.animationTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get(SPLASHSCREEN_STR, "animationImage"))));

        this.logoOffsetX = Config.getInt(SPLASHSCREEN_STR, "logoOffsetX");
        this.logoOffsetY = Config.getInt(SPLASHSCREEN_STR, "logoOffsetY");

        this.animX = Config.getInt(SPLASHSCREEN_STR, "animX");
        this.animY = Config.getInt(SPLASHSCREEN_STR, "animY");
        this.animWidth = Config.getInt(SPLASHSCREEN_STR, "animWidth");
        this.animHeight = Config.getInt(SPLASHSCREEN_STR, "animHeight");
        this.rotationSpeed = Config.getFloat(SPLASHSCREEN_STR, "animSpeed");
        this.animEnabled = Config.getBool(SPLASHSCREEN_STR, "animEnabled");

        this.anim = new TextureRegion(this.animationTexture);
    }

    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //render background
        batch.draw(this.bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float x = (Gdx.graphics.getWidth() - this.logoTexture.getWidth()) / 2f + this.logoOffsetX;
        float y = (Gdx.graphics.getHeight() - this.logoTexture.getHeight()) / 2f + this.logoOffsetY;

        //draw logo in center
        batch.draw(this.logoTexture, x, y, this.logoTexture.getWidth(), this.logoTexture.getHeight());

        if (this.animEnabled) {
            this.degree = (this.degree + Gdx.graphics.getDeltaTime() * this.rotationSpeed) % 360;

            //draw rotating wheel
            batch.draw(this.anim, this.animX, this.animY, (this.animWidth / 2), (this.animHeight / 2), this.animWidth, this.animHeight, 1, 1, this.degree);
        }

        batch.end();
    }

    public void cleanUp () {
        this.bgTexture.dispose();
        this.bgTexture = null;
        this.logoTexture.dispose();
        this.logoTexture = null;
        this.animationTexture.dispose();
        this.animationTexture = null;
    }

}
