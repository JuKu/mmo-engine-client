package com.jukusoft.mmo.engine.shared.region;

public class RegionCoordinates {

    public long regionID = 0;
    public int instanceID = 0;

    public RegionCoordinates(long regionID, int instanceID) {
        this.regionID = regionID;
        this.instanceID = instanceID;
    }

    public long getRegionID() {
        return regionID;
    }

    public void setRegionID(long regionID) {
        this.regionID = regionID;
    }

    public int getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(int instanceID) {
        this.instanceID = instanceID;
    }

    public void set (long regionID, int instanceID) {
        this.regionID = regionID;
        this.instanceID = instanceID;
    }

}
