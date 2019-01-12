package com.jukusoft.mmo.engine.shared.utils;

import com.jukusoft.mmo.engine.shared.client.events.load.ready.GameLogicLayerReadyEvent;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.region.RegionMap;

import java.util.List;

public class RegionUtils {

    protected RegionUtils () {
        //
    }

    public static RegionMap getMapWherePlayerStandsOn (float playerX, float playerY, List<RegionMap> list) {
        for (RegionMap map1 : list) {
            if (map1.isPointInnerMap(playerX, playerY)) {
                //map found
                return map1;
            }
        }

        return null;
    }

}
