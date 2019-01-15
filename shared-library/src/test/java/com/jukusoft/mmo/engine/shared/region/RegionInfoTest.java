package com.jukusoft.mmo.engine.shared.region;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RegionInfoTest {

    @Test
    public void testConstructor () {
        new RegionInfo();
    }

    @Test
    public void testLoad () throws IOException {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.load(new File("../data/junit/regions/region.json"));

        assertEquals("Kardor", regionInfo.getTitle());
        assertEquals(32, regionInfo.getTileWidth());
        assertEquals(32, regionInfo.getTileHeight());
        assertEquals(1, regionInfo.listMaps().size());
        assertEquals(false, regionInfo.isDrawGroundAlways());
    }

    @Test (expected = FileNotFoundException.class)
    public void testLoadRegionWithNotExistentMap () throws IOException {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.load(new File("../data/junit/regions/region1.json"));
    }

    @Test (expected = IllegalStateException.class)
    public void testLoadRegionWithOverlappingMaps () throws IOException {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.load(new File("../data/junit/regions/region2.json"));
    }

}
