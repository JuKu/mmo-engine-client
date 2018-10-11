package com.jukusoft.mmo.engine.applayer.script;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScriptEngineTest {

    @Test
    public void testConstructor () {
        new ScriptEngine();
    }

    @Test
    public void testInit () {
        ScriptEngine.init();

        IScriptEngine engine = ScriptEngine.getInstance();
        assertNotNull(engine);

        IScriptEngine engine1 = ScriptEngine.getInstance();

        //check, if they are the same instances
        assertEquals(engine, engine1);

        //remove instance again, so that other tests work as expected
        ScriptEngine.instance = null;
    }

    @Test
    public void testCleanUpWithoutInit () {
        ScriptEngine.instance = null;
        ScriptEngine.cleanUp();
    }

}
