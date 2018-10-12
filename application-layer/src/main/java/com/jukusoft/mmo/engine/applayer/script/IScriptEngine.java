package com.jukusoft.mmo.engine.applayer.script;

import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;

public interface IScriptEngine {

    public void compile (String scriptName, String programStr) throws ScriptLoadException;

    public Object execFunc (String funcName, Object... args);

    public Object execFunc (String funcName);

    public Object execScript (String scriptName, Object... args);

}
