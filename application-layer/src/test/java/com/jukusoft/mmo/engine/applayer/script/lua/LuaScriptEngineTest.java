package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LuaScriptEngineTest {

    @Test
    public void testConstructor () {
        new LuaScriptEngine();
    }

    @Test (expected = IllegalStateException.class)
    public void testExecNotExistentFunc () {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.execFunc("test");
    }

    @Test
    public void testCompile () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return test()");
    }

    @Test
    public void testExec () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return 2");
        assertEquals(2l, engine.execFunc("test"));
    }

    @Test
    public void testExec1 () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return 2");
        assertEquals(2l, engine.execFunc("test", "param1", "param2"));
    }

    @Test
    public void testExecGlobalFunc () throws ScriptLoadException {
        GameTime time = GameTime.getInstance();
        time.setTime(System.currentTimeMillis());

        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return now();");
        assertEquals(time.getTime(), engine.execFunc("test"));
    }

}
