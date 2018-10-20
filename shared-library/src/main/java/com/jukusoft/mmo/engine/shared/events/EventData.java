package com.jukusoft.mmo.engine.shared.events;

import org.mini2Dx.gdx.utils.Pool;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class EventData implements Pool.Poolable {

    /**
    * reference counter
    */
    private final AtomicInteger refCount = new AtomicInteger(1);

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
    * allow to trigger this event (without using queues)
     * Only return true, if this event is absolutely thread safe!
     *
     * @return true, if trigger is allowed and event is thread safe
    */
    public boolean allowTrigger () {
        return false;
    }

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
        this.refCount.set(1);
    }

    public final int retain () {
        return this.retain(1);
    }

    public final int retain (int i) {
        return this.refCount.addAndGet(i);
    }

    public final int release () {
        return this.refCount.decrementAndGet();
    }

    public final int getRefCount () {
        return this.refCount.get();
    }

}
