package com.jukusoft.mmo.engine.gameview.region;

import com.jukusoft.mmo.engine.shared.client.events.load.ReceivedAllMapSpecificDataEvent;
import com.jukusoft.mmo.engine.shared.events.EventListener;

public interface RegionLoader extends EventListener<ReceivedAllMapSpecificDataEvent> {

    /**
     * this method is called if all region files are received and client can load this region (and map files) now
     */
    @Override
    public default void handleEvent(ReceivedAllMapSpecificDataEvent event) {
        loadRegion(event);
    }

    /**
     * this method is called if all region files are received and client can load this region (and map files) now
     */
    public void loadRegion(ReceivedAllMapSpecificDataEvent event);

}
