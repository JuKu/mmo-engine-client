package com.jukusoft.mmo.engine.applayer.script.jsv8;

import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;

public class JSV8ScriptEngine implements IScriptEngine {

    //https://github.com/eclipsesource/J2V8

    //https://eclipsesource.com/blogs/2015/02/25/announcing-j2v8-2-0/

    //https://stackoverflow.com/questions/6369919/how-to-embed-v8-in-a-java-application#

    //javascript runtime

    public JSV8ScriptEngine () {
        //V8 v8 = V8.createV8Runtime();
    }

    @Override
    public void compile(String funcName, String programStr) throws ScriptLoadException {

    }

    @Override
    public Object execFunc(String funcName, Object... args) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

    @Override
    public Object execFunc(String funcName) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

    @Override
    public Object execScript(String scriptName, Object... args) {
        throw new UnsupportedOperationException("method isn't implemented yet.");
    }

}
