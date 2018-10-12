package com.jukusoft.mmo.engine.applayer.script.lua;

import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.compiler.CompilerChunkLoader;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.impl.StandardLibrary;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class LuaScriptEngineTest {

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
        new LuaScriptEngine();
    }

    @Test (expected = IllegalStateException.class)
    public void testExecNotExistentScript () {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.execScript("test");
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
        assertEquals(2l, engine.execScript("test"));
    }

    @Test
    public void testExec1 () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return 2");
        assertEquals(2l, engine.execScript("test", "param1", "param2"));
    }

    @Test
    public void testExecScript () throws ScriptLoadException {
        GameTime time = GameTime.getInstance();
        time.setTime(System.currentTimeMillis());

        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("test", "return now();");
        assertEquals(time.getTime(), engine.execScript("test"));
    }

    @Test
    public void testExecGlobalFunc () throws ScriptLoadException, CallException {
        GameTime time = GameTime.getInstance();
        time.setTime(System.currentTimeMillis());

        LuaScriptEngine engine = new LuaScriptEngine();
        engine.compile("add", "function add (a)\n" +
                "      local sum = 0\n" +
                "      for i,v in ipairs(a) do\n" +
                "        print(v);" +
                "        sum = sum + v\n" +
                "      end\n" +
                "      return sum\n" +
                "    end");
        engine.execScript("add");

        assertEquals(6l, engine.execFunc("add", LuaUtils.array(1, 2, 3)));
    }

    @Test
    public void testLuaExample () throws LoaderException, InterruptedException, CallPausedException, CallException {
        //https://github.com/mjanicek/rembulan/issues/22

        String program = "function dummy(a,b) print('a=',a,'b=',b); end";

        //compile the program
        StateContext state = StateContexts.newDefaultInstance();
        Table env = StandardLibrary.in(RuntimeEnvironments.system()).installInto(state);
        ChunkLoader loader = CompilerChunkLoader.of("MyDummyMainProgram");
        LuaFunction main = loader.loadTextChunk(new Variable(env), "MyDummyChunk", program);

        //run program in order to declare the "dummy" function
        DirectCallExecutor.newExecutor().call(state, main);

        //get a reference to the function
        LuaFunction dummy = (LuaFunction)env.rawget("dummy");

        // call it
        String a = "hello";
        String b = "world!";
        Object[] rtnValues = DirectCallExecutor.newExecutor().call(state, dummy, a, b);
    }

    @Test
    public void testLuaExample1 () throws LoaderException, InterruptedException, CallPausedException, CallException {
        //https://github.com/mjanicek/rembulan/issues/22

        String program = "function dummy() return 10; end";

        //compile the program
        StateContext state = StateContexts.newDefaultInstance();
        Table env = StandardLibrary.in(RuntimeEnvironments.system()).installInto(state);
        ChunkLoader loader = CompilerChunkLoader.of("MyDummyMainProgram");
        LuaFunction main = loader.loadTextChunk(new Variable(env), "MyDummyChunk", program);

        //run program in order to declare the "dummy" function
        DirectCallExecutor.newExecutor().call(state, main);

        //get a reference to the function
        LuaFunction dummy = (LuaFunction)env.rawget("dummy");

        // call it
        Object[] rtnValues = DirectCallExecutor.newExecutor().call(state, dummy);
        assertEquals(1, rtnValues.length);
        assertEquals(10l, rtnValues[0]);
    }

    @Test (expected = ScriptLoadException.class)
    public void testLoadFileNotExists () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.loadFile(new File("not-existent-file.lua"));
    }

    @Test
    public void testLoadInitFile () throws ScriptLoadException {
        LuaScriptEngine engine = new LuaScriptEngine();
        engine.loadFile(new File("../data/init/scripts/init.lua"));
    }

}
