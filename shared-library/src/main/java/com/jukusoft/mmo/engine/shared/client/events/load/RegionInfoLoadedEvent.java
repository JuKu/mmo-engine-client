package com.jukusoft.mmo.engine.shared.client.events.load;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.region.RegionInfo;

/**
* event which is fired after RegionInfo was loaeded
 *
 * @see com.jukusoft.mmo.engine.shared.region.RegionInfo
*/
public class RegionInfoLoadedEvent extends EventData {

    public RegionInfo regionInfo = null;

    @Override
    public int getEventType() {
        return ClientEvents.REGION_INFO_LOADED.getID();
    }

}
