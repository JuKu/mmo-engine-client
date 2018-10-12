package com.jukusoft.mmo.engine.applayer.script;

import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import net.sandius.rembulan.exec.CallException;

public interface IScriptEngine {

    public void compile (String scriptName, String programStr) throws ScriptLoadException;

    public Object execFunc (String funcName, Object... args) throws CallException;

    public Object execFunc (String funcName) throws CallException;

    public Object execScript (String scriptName, Object... args);

}
