package com.jukusoft.mmo.engine.shared.events;

import org.mini2Dx.gdx.utils.Pool;

public abstract class EventData implements Pool.Poolable {

    /**
    * timestamp when event was fired
    */
    protected volatile long timestamp = 0;

    /**
    * return unique event type ID
     *
     * @return unique event type ID
    */
    public abstract int getEventType ();

    /**
    * initialize event
    */
    public void init () {
        this.timestamp = System.currentTimeMillis();
    }

    /**
    * get unix timestamp as milliseconds
     *
     * @return unix timestamp in milliseconds
    */
    public long getTimestamp () {
        return timestamp;
    }

    @Override
    public void reset() {
        //
    }

}
