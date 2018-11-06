package com.jukusoft.mmo.engine.shared.utils;

import com.jukusoft.mmo.engine.shared.utils.Utils;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testConstructor () {
        new Utils();
    }

    @Test (expected = NullPointerException.class)
    public void testPrintNullSection () {
        Utils.printSection(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPrintEmptySection () {
        Utils.printSection("");
    }

    @Test
    public void testPrintSection () {
        Utils.printSection("my-section");
    }

}
