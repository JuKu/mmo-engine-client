package com.jukusoft.mmo.engine.shared.events;

@FunctionalInterface
public interface EventListener<T extends EventData> {

    /**
    * handle event
     *
     * @param eventData single event
    */
    public void handleEvent (T eventData);

}
