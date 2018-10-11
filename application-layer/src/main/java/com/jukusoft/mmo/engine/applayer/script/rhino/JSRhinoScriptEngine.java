package com.jukusoft.mmo.engine.applayer.script.rhino;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.InvocationTargetException;

public class JSRhinoScriptEngine implements IScriptEngine {

    protected Context cx = null;
    protected Scriptable scope = null;

    //https://github.com/mozilla/rhino/tree/master/examples

    public JSRhinoScriptEngine () {
        //creates and enters a Context. A Context stores information about the execution environment of a script.
        this.cx = Context.enter();

        //initializes the standard objects (Object, Function, etc.)
        //This must be done before scripts can be executed.
        //The null parameter tells initStandardObjects to create and return a scope object that we use in later calls.
        this.scope = cx.initStandardObjects();

        try {
            ScriptableObject.defineClass(this.scope, Log.class);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Log.e("Scripts", "Exception while initializing JSRhinoScriptEngine: ", e);
        }
    }

    @Override
    public void compile(String funcName, String programStr) throws ScriptLoadException {

    }

    @Override
    public Object execFunc (String funcName, Object... args) {
        return ScriptableObject.callMethod(this.cx, this.scope, funcName, args);
    }

    @Override
    public Object execFunc(String funcName) {
        return this.execFunc(funcName, null);
    }

}
