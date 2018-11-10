package com.jukusoft.mmo.engine.applayer.utils;

import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.shared.logger.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class FPSManager {

    public static final FPSManager instance = new FPSManager();

    protected AtomicInteger fps = new AtomicInteger(0);
    protected int criticalFPSValue = 50;//value on which warnings are enabled
    protected long lastWarningSecond = 0;//second of last warning
    protected final GameTime time = GameTime.getInstance();

    /**
     * protected constructor, because only one instance is allowed (singleton design pattern)
     */
    protected FPSManager() {
        //
    }

    public void setFPS (int fps) {
        this.fps.set(fps);
    }

    public int getFPS () {
        return this.fps.get();
    }

    public boolean isCriticalFPSValue () {
        return this.getFPS() <= this.criticalFPSValue && this.getFPS() != 0;//on init, fps is 0
    }

    public void showWarningIfNeccessary () {
        if (this.isCriticalFPSValue()) {
            //check if warning was already logged this second
            long now = this.time.getTime();
            long nowWarnSecond = now / 1000;
            long lastWarnSecond = this.lastWarningSecond / 1000;

            if (nowWarnSecond != lastWarnSecond) {
                Log.w("FPS Warn", "Warning! FPS is <= " + this.criticalFPSValue + ", current value: " + this.fps.get() + "!");

                this.lastWarningSecond = System.currentTimeMillis();
            }
        }
    }

    public static FPSManager getInstance () {
        return instance;
    }

}
