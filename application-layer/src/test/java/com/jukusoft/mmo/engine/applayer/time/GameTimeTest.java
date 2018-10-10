package com.jukusoft.mmo.engine.applayer.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GameTimeTest {

    @Test
    public void testConstructor () {
        new GameTime();
    }

    @Test
    public void testGetInstance () {
        GameTime instance = GameTime.getInstance();
        assertNotNull(instance);

        //check, that instances are equals
        GameTime instance1 = GameTime.getInstance();
        assertEquals(instance, instance1);
    }

    @Test
    public void testGetStartupTime () {
        assertEquals(true, GameTime.getStartTime() > 0);
    }

    @Test
    public void testGetterAndSetter () {
        GameTime time = new GameTime();

        long currentTime = System.currentTimeMillis();
        time.setTime(currentTime);
        assertEquals(currentTime, time.getTime());

        time.setDelta(0.25f);
        assertEquals(0.25f, time.getDelta(), 0.0001f);
    }

}
