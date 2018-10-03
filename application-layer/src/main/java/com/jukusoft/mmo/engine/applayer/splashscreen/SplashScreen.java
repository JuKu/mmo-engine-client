package com.jukusoft.mmo.engine.applayer.splashscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;

public class SplashScreen {

    protected SpriteBatch batch = null;

    protected Texture bgTexture = null;
    protected Texture logoTexture = null;
    protected Texture animationTexture = null;

    protected float animX = 0;
    protected float animY = 0;
    protected float animWidth = 0;
    protected float animHeight = 0;

    protected boolean animEnabled = false;
    protected TextureRegion anim = null;

    protected float degree = 0;
    protected float rotationSpeed = 10;

    public SplashScreen () {
        //
    }

    public void load () {
        this.batch = new SpriteBatch();

        //set width & height
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Config.getInt("Splashscreen", "width"), Config.getInt("Splashscreen", "height"));

        //load images
        this.bgTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get("Splashscreen", "bgImage"))));
        this.logoTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get("Splashscreen", "logoImage"))));
        this.animationTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get("Splashscreen", "animationImage"))));

        this.animX = Config.getInt("Splashscreen", "animX");
        this.animY = Config.getInt("Splashscreen", "animY");
        this.animWidth = Config.getInt("Splashscreen", "animWidth");
        this.animHeight = Config.getInt("Splashscreen", "animHeight");
        this.rotationSpeed = Config.getFloat("Splashscreen", "animSpeed");
        this.animEnabled = Config.getBool("Splashscreen", "animEnabled");

        this.anim = new TextureRegion(this.animationTexture);
    }

    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //render background
        batch.draw(this.bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float x = (Gdx.graphics.getWidth() - this.logoTexture.getWidth()) / 2;
        float y = (Gdx.graphics.getHeight() - this.logoTexture.getHeight()) / 2;

        //draw logo in center
        batch.draw(this.logoTexture, x, y, this.logoTexture.getWidth(), this.logoTexture.getHeight());

        this.degree = (this.degree + Gdx.graphics.getDeltaTime() * this.rotationSpeed) % 360;

        if (this.animEnabled) {
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
