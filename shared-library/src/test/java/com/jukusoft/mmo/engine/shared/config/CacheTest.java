package com.jukusoft.mmo.engine.shared.config;

import com.jukusoft.mmo.engine.shared.config.Cache;
import org.junit.Test;

import java.io.File;
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

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () {
        new Cache(null);
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructorWithNotExistentFile () {
        new Cache("not-existent-dir");
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructorWithFile () {
        new Cache("../config/junit-logger.cfg");
    }

    @Test
    public void testConstructorWithSlash () {
        new Cache("../temp/");
    }

    @Test
    public void testConstructorWithoutSlash () {
        new Cache("../temp");
    }

    @Test
    public void testCreateDirIfAbsent () {
        //delete directory first
        if (new File("../temp/junit-test1").exists()) {
            new File("../temp/junit-test1").delete();
        }

        assertEquals(false, new File("../temp/junit-test1").exists());

        Cache cache = new Cache("../temp/");
        cache.createDirIfAbsent("junit-test1");

        //check, if directory was created
        assertEquals(true,new File("../temp/junit-test1").exists());

        //check, if directory is a directory
        assertEquals(true, new File("../temp/junit-test1").isDirectory());

        //delete test directory
        new File("../temp/junit-test1").delete();
    }

    @Test
    public void testGetCachePath () {
        Cache cache = new Cache("../temp/");
        assertEquals("../temp/test2/", cache.getCachePath("test2"));

        //check, if directory ends with slash
        assertEquals(true, cache.getCachePath("test").endsWith("/"));
    }

    @Test
    public void testClear () throws IOException {
        //create test directory first
        if (!new File("../temp/junit-test/").exists()) {
            new File("../temp/junit-test/").mkdirs();
        }

        //add test file to check, if directory was cleared
        if (!new File("../temp/junit-test/test.txt").exists()) {
            new File("../temp/junit-test/test.txt").createNewFile();
        }

        Cache cache = new Cache("../temp/junit-test/");
        assertEquals(true, new File("../temp/junit-test/").exists());
        assertEquals(true, new File("../temp/junit-test/").isDirectory());

        //check, if test file exists
        assertEquals(true, new File("../temp/junit-test/test.txt").exists());

        //clear cache, so content is deleted, but directory still exists
        cache.clear();

        //check, if test file exists
        assertEquals(false, new File("../temp/junit-test/test.txt").exists());

        //check, if temp directory exists after clear() call, this means only content was deleted, but not directory itself
        assertEquals(true, new File("../temp/junit-test/").exists());
        assertEquals(true, new File("../temp/junit-test/").isDirectory());
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
