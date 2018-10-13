package com.jukusoft.mmo.engine.applayer.script;

import com.jukusoft.mmo.engine.applayer.script.lua.LuaScriptEngine;

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

        ScriptEngine.instance.shutdown();
        ScriptEngine.instance = null;
    }

}
