package com.jukusoft.mmo.engine.applayer.script.lua;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import net.sandius.rembulan.StateContext;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.compiler.CompilerChunkLoader;
import net.sandius.rembulan.env.RuntimeEnvironments;
import net.sandius.rembulan.exec.CallException;
import net.sandius.rembulan.exec.CallPausedException;
import net.sandius.rembulan.exec.DirectCallExecutor;
import net.sandius.rembulan.impl.StateContexts;
import net.sandius.rembulan.lib.impl.StandardLibrary;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.LuaFunction;

public class LuaScriptEngine implements IScriptEngine {

    protected StateContext state = null;
    protected Table env = null;

    //map with all registered global lua functions
    protected ObjectObjectMap<String, LuaFunction> luaFunctions = new ObjectObjectHashMap<>();

    public LuaScriptEngine () {
        //initialize state
        this.state = StateContexts.newDefaultInstance();
        this.env = StandardLibrary.in(RuntimeEnvironments.system()).installInto(state);

        //register global functions
        this.env.rawset("logd", new LogDebugFunc());
        this.env.rawset("logi", new LogInfoFunc());

        this.env.rawset("now", new NowFunc());
    }

    /**
     * compile lua script as string to function
     *
     * @param rootClassPrefix  the class name prefix for compiled classes, must not be {@code null}
     */
    @Override
    public void compile (String rootClassPrefix, String funcName, String programStr) throws ScriptLoadException {
        //compile
        ChunkLoader loader = CompilerChunkLoader.of(rootClassPrefix);
        LuaFunction func = null;
        try {
            func = loader.loadTextChunk(new Variable(env), funcName, programStr);
        } catch (LoaderException e) {
            Log.e("Scripts", "Couldn't compile lua script: " + programStr + ", error: " + e.getLuaStyleErrorMessage(), e);
            throw new ScriptLoadException("Coulnd't compile script: " + programStr + ", error: " + e.getLuaStyleErrorMessage());
        }

        this.luaFunctions.put(funcName, func);
    }

    public void execGlobalFunc (String funcName) {
        LuaFunction func = this.luaFunctions.get(funcName);

        if (func == null) {
            throw new IllegalStateException("lua function '" + funcName + "' doesn't exists in cache, you have to compile it first!");
        }

        try {
            DirectCallExecutor.newExecutor().call(state, func);
        } catch (CallException e) {
            Log.w("Scripts", "CallException: ", e);
        } catch (CallPausedException e) {
            Log.w("Scripts", "CallPausedException: ", e);
        } catch (InterruptedException e) {
            Log.w("Scripts", "InterruptedException: ", e);
        }
    }

}
