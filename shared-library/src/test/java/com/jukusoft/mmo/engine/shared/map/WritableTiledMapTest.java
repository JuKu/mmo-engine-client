package com.jukusoft.mmo.engine.shared.map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WritableTiledMapTest {

    @Test
    public void testConstructor () {
        new WritableTiledMap();
    }

    @Test
    public void testGetterAndSetter () {
        WritableTiledMap map = new WritableTiledMap();

        map.setOrientation(Orientation.ISOMETRIC);
        assertEquals(Orientation.ISOMETRIC, map.getOrientation());
        map.setOrientation(Orientation.ORTHOGONAL);
        assertEquals(Orientation.ORTHOGONAL, map.getOrientation());

        map.setDimension(100, 200);
        assertEquals(100, map.getWidthInTiles());
        assertEquals(200, map.getHeightInTiles());

        map.setTileDimenstion(300, 400);
        assertEquals(300, map.getTileWidth());
        assertEquals(400, map.getTileHeight());

        map.setBackgroundColor("#000000");
        assertEquals("#000000", map.getBackgroundColor());
        map.setBackgroundColor("#FFFF00AA");
        assertEquals("#FFFF00AA", map.getBackgroundColor());

        map.setNextLayerID(10);
        assertEquals(10, map.getNextLayerID());
        map.setNextObjectID(20);
        assertEquals(20, map.getNextObjectID());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetInvalideBackgroundColor () {
        WritableTiledMap map = new WritableTiledMap();
        map.setBackgroundColor("000000");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetInvalideBackgroundColor1 () {
        WritableTiledMap map = new WritableTiledMap();
        map.setBackgroundColor("#00");
    }

}
