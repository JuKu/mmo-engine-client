package com.jukusoft.mmo.engine.applayer.utils;

import org.junit.Test;

public class ExceptionUtilsTest {

    @Test
    public void testConstructor () {
        new ExceptionUtils();
    }

    @Test
    public void testLogException () {
        ExceptionUtils.logException("test", "test message", () -> {
            //don't do anything here
        });
    }

    @Test
    public void testLogException1 () {
        ExceptionUtils.logException("test", "test message", () -> {
            //throw exception
            throw new Exception("test");
        });
    }

}
