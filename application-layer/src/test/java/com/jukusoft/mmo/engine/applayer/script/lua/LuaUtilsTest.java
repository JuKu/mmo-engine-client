package com.jukusoft.mmo.engine.applayer.script.lua;

import net.sandius.rembulan.Table;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LuaUtilsTest {

    @Test
    public void testConstructor () {
        new LuaUtils();
    }

    @Test
    public void testArray () {
        Table table = LuaUtils.array(10, 20, 30);
        assertNotNull(table);
        assertEquals(3, table.rawlen());

        assertEquals(10l, table.rawget(1l));
        assertEquals(20l, table.rawget(2l));
        assertEquals(30l, table.rawget(3l));
    }

}
