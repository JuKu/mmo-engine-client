package com.jukusoft.mmo.engine.gameview.region.impl;

import com.jukusoft.mmo.engine.gameview.region.RegionLoader;
import com.jukusoft.mmo.engine.shared.client.events.load.ReceivedAllMapSpecificDataEvent;
import com.jukusoft.mmo.engine.shared.logger.Log;

public class RegionLoaderImpl implements RegionLoader {

    protected static final String LOG_TAG = "RegionLoader";

    public RegionLoaderImpl () {
        //
    }

    @Override
    public void loadRegion(ReceivedAllMapSpecificDataEvent event) {
        Log.i(LOG_TAG, "try to load region: " + event.regionDir);
    }

}
