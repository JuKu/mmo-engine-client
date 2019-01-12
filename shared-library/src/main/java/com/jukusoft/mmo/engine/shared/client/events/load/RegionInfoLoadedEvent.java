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

    //directory where all cached region files like .tmx maps are stored, ends with "/"
    public String regionDir = "";

    //current character position
    public float posX = 0;
    public float posY = 0;
    public float posZ = 0;

    @Override
    public int getEventType() {
        return ClientEvents.REGION_INFO_LOADED.getID();
    }

    @Override
    public String toString() {
        return "RegionInfoLoadedEvent{" +
                "regionInfo=" + regionInfo +
                ", regionDir='" + regionDir + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", posZ=" + posZ +
                '}';
    }
}
