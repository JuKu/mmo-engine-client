package com.jukusoft.mmo.engine.gameview.region.impl;

import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.gameview.region.RegionLoader;
import com.jukusoft.mmo.engine.shared.client.events.load.ReceivedAllMapSpecificDataEvent;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;

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

        //TODO: find map, where player stands on

    }

}
