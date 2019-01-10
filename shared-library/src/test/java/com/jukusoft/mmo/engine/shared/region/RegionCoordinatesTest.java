package com.jukusoft.mmo.engine.shared.region;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegionCoordinatesTest {

    @Test
    public void testConstructor () {
        new RegionCoordinates(0, 0);
    }

    @Test
    public void testGetterAndSetter () {
        RegionCoordinates region = new RegionCoordinates(0, 0);
        assertEquals(0, region.getRegionID());
        assertEquals(0, region.getInstanceID());

        region.setRegionID(1);
        region.setInstanceID(2);
        assertEquals(1, region.getRegionID());
        assertEquals(2, region.getInstanceID());

        region.set(3, 4);
        assertEquals(3, region.getRegionID());
        assertEquals(4, region.getInstanceID());
    }

}
