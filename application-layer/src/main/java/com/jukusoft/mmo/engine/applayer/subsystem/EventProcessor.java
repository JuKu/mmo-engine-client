package com.jukusoft.mmo.engine.applayer.subsystem;

import com.jukusoft.mmo.engine.shared.events.Events;

public class EventProcessor implements SubSystem {

    protected final int threadID;
    protected final int maxMillis;

    /**
    * default constructor
     *
     * @param threadID thread ID
     * @param maxMillis maximum time in milliseconds, how long event proccessing can take
    */
    public EventProcessor (int threadID, int maxMillis) {
        this.threadID = threadID;
        this.maxMillis = maxMillis;
    }

    @Override
    public void onInit() {
        //
    }

    @Override
    public void onGameloop() {
        Events.update(this.threadID, this.maxMillis);
    }

    @Override
    public void onShutdown() {
        //
    }

}
