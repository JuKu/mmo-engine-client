package com.jukusoft.mmo.engine.applayer.script;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.lua.LuaScriptEngine;
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

public class ScriptEngine {

    /**
    * singleton instance of ScriptEngine
    */
    protected static IScriptEngine instance = null;

    protected ScriptEngine () {
        //
    }

    public static void init () {
        ScriptEngine.instance = new LuaScriptEngine();
    }

    public static IScriptEngine getInstance () {
        return instance;
    }

    public static void cleanUp () {
        if (getInstance() == null) {
            return;
        }
    }

}
