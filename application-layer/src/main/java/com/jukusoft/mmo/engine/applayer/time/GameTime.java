package com.jukusoft.mmo.engine.applayer.time;

public class GameTime {

    protected static final GameTime instance = new GameTime();

    //startup time
    public static final long START_TIME = System.currentTimeMillis();

    //current time
    protected volatile long time = 0;

    //delta time for libGDX
    protected volatile float delta = 0;

    /**
    * protected constructor, because only one instance is allowed (singleton design pattern)
    */
    protected GameTime() {
        //
    }

    public static GameTime getInstance () {
        return instance;
    }

    public static long getStartTime() {
        return START_TIME;
    }

    public long getTime() {
        return time;
    }

    public void setTime (long time) {
        this.time = time;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta (float delta) {
        this.delta = delta;
    }

}
