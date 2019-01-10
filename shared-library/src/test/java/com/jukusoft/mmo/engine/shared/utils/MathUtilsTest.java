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
        assertEquals(0f, MathUtils.round3Digits(0.0001f), 0.00000001f);
        assertEquals(0f, MathUtils.round3Digits(0.000001f), 0.00000001f);
        assertEquals(0.001f, MathUtils.round3Digits(0.001f), 0.00000001f);
        assertEquals(1f, MathUtils.round3Digits(1.000000f), 0.00000001f);
        assertEquals(0.5f, MathUtils.round3Digits(0.500000f), 0.00000001f);
    }

}
