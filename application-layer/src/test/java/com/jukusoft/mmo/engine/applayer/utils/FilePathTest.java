package com.jukusoft.mmo.engine.applayer.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FilePathTest {

    @Test
    public void testConstructor () {
        new FilePath();
    }

    @Test
    public void testParse () {
        //test string without placeholder
        assertEquals("test", FilePath.parse("test"));

        assertEquals("C:/User/test/my-app", FilePath.parse("C:\\User\\test\\my-app"));
        assertNotNull(FilePath.parse("{user.home}test"));
        assertEquals(true, FilePath.parse("{user.home}test").endsWith("/test"));
    }

}
