package com.jukusoft.mmo.engine.shared.map;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TiledLayerTest {

    @Test (expected = NullPointerException.class)
    public void testConstructorNullName () {
        new TiledLayer(null, 0, 0, 0, true, 0, 0, 1, 1);
    }

    @Test
    public void testConstructor () {
        new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);
    }

    @Test
    public void testGetter () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        assertEquals("test", layer.getName());
        assertEquals(10, layer.getWidth());
        assertEquals(20, layer.getHeight());
        assertEquals(1, layer.getOpacity(), 0.0001f);
        assertEquals(true, layer.isVisible());
        assertEquals(30, layer.getOffsetx(), 0.0001f);
        assertEquals(40, layer.getOffsety(), 0.0001f);
    }

    @Test (expected = NullPointerException.class)
    public void testSetNullTileIDs () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        layer.setTileIDs(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetInvalideTileIDs () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        layer.setTileIDs(new int[10]);
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNullTileIDs () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        layer.getTileIDs();
    }

    @Test
    public void testGetTileIDs () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        layer.setTileIDs(new int[10 * 20]);
        assertNotNull(layer.getTileIDs());
        assertEquals(200, layer.getTileIDs().length);
    }

    @Test
    public void testSortRenderOrder () {
        TiledLayer layer = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 2);
        TiledLayer layer1 = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 5);
        TiledLayer layer2 = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);
        TiledLayer layer3 = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 3);
        TiledLayer layer4 = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 4);
        TiledLayer layer5 = new TiledLayer("test", 10, 20, 1, true, 30, 40, 1, 1);

        List<TiledLayer> layers = new ArrayList<>();
        layers.add(layer);
        layers.add(layer1);
        layers.add(layer2);
        layers.add(layer3);
        layers.add(layer4);
        layers.add(layer5);

        //sort list
        Collections.sort(layers);

        //check list positions
        assertEquals(layer2, layers.get(0));
        assertEquals(layer5, layers.get(1));
        assertEquals(layer, layers.get(2));
        assertEquals(layer3, layers.get(3));
        assertEquals(layer4, layers.get(4));
        assertEquals(layer1, layers.get(5));
    }

}
