package com.jukusoft.mmo.engine.logic.region;

import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.shared.client.events.load.ReceivedAllMapSpecificDataEvent;
import com.jukusoft.mmo.engine.shared.client.events.load.RegionInfoLoadedEvent;
import com.jukusoft.mmo.engine.shared.client.events.load.ready.GameLogicLayerReadyEvent;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;
import com.jukusoft.mmo.engine.shared.region.RegionLoader;
import com.jukusoft.mmo.engine.shared.region.RegionMap;
import com.jukusoft.mmo.engine.shared.utils.RegionUtils;

import java.io.File;
import java.io.IOException;

public class RegionLoaderImpl implements RegionLoader {

    protected static final String LOG_TAG = "RegionLoader";

    public RegionLoaderImpl () {
        //
    }

    @Override
    public void loadRegion(ReceivedAllMapSpecificDataEvent event) {
        Log.i(LOG_TAG, "try to load region: " + event.regionDir);

        RegionInfo regionInfo = new RegionInfo();

        try {
            regionInfo.load(new File(event.regionDir + "region.json"));
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while loading region.json of directory: " + event.regionDir, e);

            //show error
            JavaFXUtils.showExceptionDialog(I.tr("Error!"), "An exception was thrown while loading region (" + event.regionDir + "region.json). Please report this to the developers!", e);

            return;
        }

        //fire event so that game logic layer can load collision data and game view layer can load assets
        RegionInfoLoadedEvent event1 = Pools.get(RegionInfoLoadedEvent.class);
        event1.regionInfo = regionInfo;
        event1.regionDir = event.regionDir;
        event1.posX = event.posX;
        event1.posY = event.posY;
        event1.posZ = event.posZ;
        Events.queueEvent(event1);

        //get current player position
        float x = event.posX;
        float y = event.posY;

        //find map, where player stands on
        RegionMap map = RegionUtils.getMapWherePlayerStandsOn(x, y, regionInfo.listMaps());

        if (map == null) {
            throw new IllegalStateException("Something went wrong, player doesn't stands on any map!");
        }

        //fire event for NetworkView that game logic layer is ready to go to GameScreen
        GameLogicLayerReadyEvent event2 = Pools.get(GameLogicLayerReadyEvent.class);
        Events.queueEvent(event2);
    }

}
