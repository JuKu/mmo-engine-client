package com.jukusoft.mmo.engine.applayer.script.rhino;

import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Undefined;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JSRhinoScriptEngineTest {

    @BeforeClass
    public static void beforeClass () {
        Config.set("Logger", "enabled", "true");
        Config.set("Logger", "printToConsole", "true");
        Config.set("Logger", "writeToFile", "false");
        Config.set("Logger", "file", "../temp/junit.log");
        Config.set("Logger", "level", "VERBOSE");
        Config.set("Logger", "timeFormat", "dd-MM-yyyy HH:mm:SS");
        Log.init();
    }

    @AfterClass
    public static void afterClass () {
        Log.shutdown();
    }

    @Test
    public void testConstructor () {
        new JSRhinoScriptEngine();
    }

    @Test
    public void testCompile () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        assertNull(engine.scripts.get("script_test"));

        //compile script
        engine.compile("test", "java.lang.System.out.println(\"test\");");
        assertNotNull(engine.scripts.get("test"));
    }

    @Test (expected = EvaluatorException.class)
    public void testCompileInvalideJS () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        assertNull(engine.scripts.get("script_test"));

        //compile script
        engine.compile("script_test", "va test = 2;");
    }

    @Test (expected = IllegalStateException.class)
    public void testExecNotExistentScript () {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        engine.execScript("not-existent-script", null);
    }

    @Test
    public void testExecScript () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();

        //compile script first
        engine.compile("script_test", "java.lang.System.out.println(\"test\");");

        //execute script
        Object obj = engine.execScript("script_test", null);
        assertNull(obj);
    }

    @Test
    public void testExecScriptWithReturnValue () throws ScriptLoadException, InterruptedException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();

        //compile script first
        engine.compile("test", "function test(a) { return 2 * a; } java.lang.System.out.println(test(4)); ");
        assertNotNull(engine.scripts.get("test"));

        //execute script
        Object obj = engine.execScript("test", null);
        assertNull(obj);

        //wait for log output
        Thread.sleep(200);
    }

    @Test (expected = EcmaError.class)
    public void testExecNotExistentFunc () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        engine.execFunc("test1");
    }

    @Test
    public void testExecFunc () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();

        //compile script first
        engine.compile("script_test", "function test(a) { return 2 * a; } ");

        //register function first (interpreter has to read this function)
        engine.execScript("script_test", null);

        Object obj = engine.execFunc("test", 8);
        assertNotNull(obj);
        assertEquals(16d, obj);
    }

    @Test (expected = ScriptLoadException.class)
    public void testLoadNotExistentFile () throws ScriptLoadException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        engine.loadFile(new File("not-existent-file.js"));
    }

    @Test
    public void testLoadInitFile () throws ScriptLoadException, InterruptedException {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        engine.loadFile(new File("../data/init/scripts/init.js"));

        //wait for logger
        Thread.sleep(200);
    }

    @Test
    public void testShutdown () {
        JSRhinoScriptEngine engine = new JSRhinoScriptEngine();
        engine.shutdown();
    }

}
