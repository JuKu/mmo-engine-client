package com.jukusoft.mmo.engine.applayer.config;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CacheTest {

    @Test
    public void testConstructor () throws IOException {
        Cache cache = new Cache("../temp/");
        assertEquals("../temp/", cache.getPath());

        //delete cache directory again
        new File("../temp/").delete();
    }

    @Test
    public void testConstructor1 () throws IOException {
        Cache cache = new Cache("../temp/");

        assertEquals("../temp/", cache.getPath());

        //check, if cache directory exists
        assertEquals(true, new File("../temp/").exists());
    }

    @Test (expected = IllegalStateException.class)
    public void testGetInstance () {
        Cache.instance = null;

        Cache.getInstance();
    }

    @Test
    public void testGetInstance1 () throws IOException {
        Cache.instance = null;

        Cache.init("../temp/");

        Cache instance = Cache.getInstance();
        assertNotNull(instance);

        //check, that instances are equals
        Cache instance1 = Cache.getInstance();
        assertNotNull(instance1);
        assertEquals(instance, instance1);
    }

}
