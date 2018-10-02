package com.jukusoft.mmo.engine.applayer.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WebUtilsTest {

    @Test
    public void testConstructor () {
        new WebUtils();
    }

    @Test
    public void testReadContentFromWebsite () throws IOException {
        assertEquals("my-test-content", WebUtils.readContentFromWebsite("http://mmo.jukusoft.com/api/junit-test-file.txt"));
    }

}
