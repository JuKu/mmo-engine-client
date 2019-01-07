package com.jukusoft.mmo.engine.shared.region;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class RegionInfoTest {

    @Test
    public void testConstructor () {
        new RegionInfo();
    }

    @Test
    public void testLoad () throws IOException {
        RegionInfo regionInfo = new RegionInfo();
        regionInfo.load(new File(""));
    }

}
