package com.jukusoft.mmo.engine.applayer.splashscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;

public class SplashScreen {

    protected SpriteBatch batch = null;

    protected Texture bgTexture = null;

    public SplashScreen () {
        //
    }

    public void load () {
        this.batch = new SpriteBatch();

        //set width & height
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Config.getInt("Splashscreen", "width"), Config.getInt("Splashscreen", "height"));

        //load images
        this.bgTexture = new Texture(Gdx.files.absolute(FilePath.parse(Config.get("Splashscreen", "bgImage"))));
    }

    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //render background
        batch.draw(this.bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();
    }

    public void cleanUp () {
        this.bgTexture.dispose();
        this.bgTexture = null;
    }

}
