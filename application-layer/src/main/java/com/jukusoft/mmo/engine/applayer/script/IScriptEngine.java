package com.jukusoft.mmo.engine.applayer.script;

import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;

public interface IScriptEngine {

    public void compile (String rootClassPrefix, String funcName, String programStr) throws ScriptLoadException;

}
