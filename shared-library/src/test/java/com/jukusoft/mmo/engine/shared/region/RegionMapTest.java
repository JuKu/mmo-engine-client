package com.jukusoft.mmo.engine.shared.region;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegionMapTest {

    @Test (expected = NullPointerException.class)
    public void testNullFileConstructor () {
        new RegionMap(null, 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void testConstructor () {
        new RegionMap("test.tmx", 1, 1, 1, 1, 1, 1);
    }

    @Test
    public void testIsPointInnerMap () {
        RegionMap map = new RegionMap("test.tmx", 0, 0, 5, 5, 32, 32);

        assertEquals(false, map.isPointInnerMap(200, 200));
        assertEquals(false, map.isPointInnerMap(161, 161));
        assertEquals(true, map.isPointInnerMap(160, 160));

        assertEquals(true, map.isPointInnerMap(150, 160));
        assertEquals(false, map.isPointInnerMap(150, 161));
        assertEquals(false, map.isPointInnerMap(160, 161));
        assertEquals(false, map.isPointInnerMap(161, 160));

        assertEquals(false, map.isPointInnerMap(-1, -1));
        assertEquals(false, map.isPointInnerMap(-1, 1));
        assertEquals(false, map.isPointInnerMap(1, -1));
        assertEquals(true, map.isPointInnerMap(1, 1));

        assertEquals(true, map.isPointInnerMap(0, 0));
    }

}
