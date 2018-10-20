package com.jukusoft.mmo.engine.shared.memory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PoolsTest {

    @Test
    public void testConstructor () {
        new Pools();
    }

    @Test
    public void testGetObject () {
        for (int i = 0; i < 10; i++) {
            Object obj = Pools.get(DummyPoolObject.class);
            assertNotNull(obj);

            //test free
            Pools.free(obj);
        }
    }

    @Test
    public void testGetNonPoolableObject () {
        for (int i = 0; i < 10; i++) {
            Object obj = Pools.get(String.class);
            assertNotNull(obj);

            //test free
            Pools.free(obj);
        }
    }

    @Test
    public void testGetPoolableObject () {
        for (int i = 0; i < 10; i++) {
            Object obj = Pools.get(DummyPoolableObject.class);
            assertNotNull(obj);

            //test free
            Pools.free(obj);
        }
    }

    @Test
    public void testGetEventDataObject () {
        for (int i = 0; i < 10; i++) {
            Object obj = Pools.get(DummyEventDataObject.class);
            assertNotNull(obj);

            //test free
            Pools.free(obj);
        }
    }

    @Test
    public void testFreePoolableObject () {
        DummyPoolableObject obj = Pools.get(DummyPoolableObject.class);
        assertNotNull(obj);
        assertEquals(false, obj.resetCalled);

        Pools.free(obj);
        assertEquals(true, obj.resetCalled);
    }

}
