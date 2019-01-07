package com.jukusoft.mmo.engine.shared.map;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class TiledMapTest {

    @Test
    public void testConstructor () {
        new TiledMap();
    }

    @Test
    public void testGetter () {
        TiledMap map = new TiledMap();
        assertNull(map.getOrientation());
    }

}
