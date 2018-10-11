package com.jukusoft.mmo.engine.applayer.script;

import org.junit.Test;

public class ScriptEngineTest {

    @Test
    public void testConstructor () {
        new ScriptEngine();
    }

    @Test
    public void testInit () {
        ScriptEngine.init();

        //remove instance again, so that other tests work as expected
        ScriptEngine.instance = null;
    }

    @Test
    public void testCleanUpWithoutInit () {
        ScriptEngine.instance = null;
        ScriptEngine.cleanUp();
    }

}
