package com.jukusoft.mmo.engine.applayer.script;

public class ScriptEngine {

    /**
    * singleton instance of ScriptEngine
    */
    protected static ScriptEngine instance = null;

    protected ScriptEngine () {
        //
    }

    public static void init () {
        ScriptEngine.instance = new ScriptEngine();
    }

    public static ScriptEngine getInstance () {
        return instance;
    }

    public static void cleanUp () {
        if (getInstance() == null) {
            return;
        }
    }

}
