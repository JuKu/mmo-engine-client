package com.jukusoft.mmo.engine.gameview.camera.impl;

import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraModification;
import com.jukusoft.mmo.engine.gameview.camera.ModificationFinishedListener;
import com.jukusoft.mmo.engine.gameview.camera.TempCameraParams;

import java.util.Random;

/**
 * Created by Justin on 11.02.2017.
 */
public class Shake1CameraModification implements CameraModification {

    protected volatile boolean isActive = false;
    protected volatile boolean permanentShake = false;

    protected float elapsed = 0;
    protected float intensity = 0;
    protected float duration = 0;

    protected Random random = new Random();
    protected long startTime = 0l;

    @Override
    public void onUpdate(GameTime time, TempCameraParams camera, ModificationFinishedListener listener) {
        if (!isActive) {
            // mod isnt active, so we dont need to update mod
            return;
        }

        float delta = time.getDelta() * 1000;

        // http://www.netprogs.com/libgdx-screen-shaking/

        // shake only, if activated
        if (elapsed < duration || permanentShake) {
            // Calculate the amount of shake based on how long it has been
            // shaking already
            float currentPower = intensity * camera.getZoom() * ((duration - elapsed) / duration);
            float x = (random.nextFloat() - 0.5f) * 2 * currentPower;
            float y = (random.nextFloat() - 0.5f) * 2 * currentPower;
            camera.translate(-x, -y);
            elapsed += delta;
        } else {
            // shake was finsihed
            this.isActive = false;
        }
    }

    @Override
    public void dispose() {
        //
    }

    public boolean isShaking() {
        return this.isActive;
    }

    /**
     * Start the screen shaking with a given power and duration
     *
     * @param intensity
     *            How much intensity should the shaking use.
     * @param duration
     *            Time in milliseconds the screen should shake.
     */
    public void shake(float intensity, float duration) {
        this.elapsed = 0;
        this.intensity = intensity;
        this.duration = duration;

        this.startTime = System.currentTimeMillis();
        this.isActive = true;
    }

    public void startPermantentShake(float intensity) {
        this.elapsed = 0;
        this.intensity = intensity;

        this.permanentShake = true;
        this.isActive = true;
    }

    public void stopPermanentShake() {
        this.permanentShake = false;
    }

}
