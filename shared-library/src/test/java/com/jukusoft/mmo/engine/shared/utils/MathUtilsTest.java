package com.jukusoft.mmo.engine.shared.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathUtilsTest {

    @Test
    public void testConstructor () {
        new MathUtils();
    }

    @Test
    public void testRound () {
        assertEquals(0f, MathUtils.round3Digits(1.5259022E-5f), 0.00000001f);
    }

}
