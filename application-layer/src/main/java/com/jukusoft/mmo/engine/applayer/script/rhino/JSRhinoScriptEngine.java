package com.jukusoft.mmo.engine.applayer.script.rhino;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptExecutionException;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
import net.sandius.rembulan.exec.CallException;
import org.mozilla.javascript.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JSRhinoScriptEngine implements IScriptEngine {

    protected Context cx = null;
    protected Scriptable scope = null;

    protected int optimisationLevel = 9;
    protected int languageVersion = Context.VERSION_ES6;

    protected ObjectObjectMap<String,Script> scripts = new ObjectObjectHashMap<>();

    protected static final String SCRIPTS_TAG = "Scripts";

    //https://github.com/mozilla/rhino/tree/master/examples

    //https://stackoverflow.com/questions/12399462/rhino-print-function

    public JSRhinoScriptEngine () {
        //creates and enters a Context. A Context stores information about the execution environment of a script.
        this.cx = Context.enter();
        cx.setOptimizationLevel(optimisationLevel);
        cx.setLanguageVersion(languageVersion);
        cx.setLocale(I.getLanguage());

        //initializes the standard objects (Object, Function, etc.)
        //This must be done before scripts can be executed.
        //The null parameter tells initStandardObjects to create and return a scope object that we use in later calls.
        this.scope = cx.initStandardObjects();

        try {
            ScriptableObject.defineClass(this.scope, Log.class);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Log.e(SCRIPTS_TAG, "Exception while initializing JSRhinoScriptEngine: ", e);
        }
    }

    @Override
    public void compile(String scriptName, String programStr) throws ScriptLoadException {
        String fileDir;

        //get current script directory (required for dofile() command)
        try {
            File currentDir = FileUtils.getRelativeFile(new File(scriptName), new File("."));
            fileDir = currentDir.getPath().replace("\\", "/");

            //remove filename
            fileDir = fileDir.substring(0, fileDir.lastIndexOf("/") + 1);
        } catch (IOException e) {
            Log.w(SCRIPTS_TAG, "IOException while getting current relative file path of lua script: ", e);
            throw new ScriptLoadException("IOException while loading script: ", e);
        }

        programStr = "/*define global variable for relative dir path*/\nvar relDir = \"" + fileDir + "\";\n" + programStr;

        //scriptName = "script_" + scriptName;

        //this.cx.evaluateString(scope, programStr, scriptName, 1, null);

        //compile string
        Script script = this.cx.compileString(programStr, scriptName, 1, null);

        //scriptName = "script_" + scriptName;
        this.scripts.put(scriptName, script);
    }

    @Override
    public void loadFile(File file) throws ScriptLoadException {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            throw new ScriptLoadException("js script file doesn't exists: " + file.getAbsolutePath());
        }

        try {
            File relFile = FileUtils.getRelativeFile(file, new File("."));
            String scriptName = relFile.getPath().replace("\\", "/");

            //String scriptName1 = "script_" + scriptName;
            String scriptName1 = scriptName;

            //only compile this file, if it isn't in cache
            if (!this.scripts.containsKey(scriptName1)) {
                String content = FileUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
                this.compile(scriptName1, content);
            }

            this.execScript(scriptName1);
        } catch (IOException e) {
            Log.e(SCRIPTS_TAG, "IOException while loading lua script file: ", e);
            throw new ScriptLoadException("Cannot read script file: " + file.getAbsolutePath() + ", IOException: " + e.getLocalizedMessage());
        }
    }

    @Override
    public Object execFunc (String funcName, Object... args) {
        return ScriptableObject.callMethod(this.cx, this.scope, funcName, args);
    }

    @Override
    public Object execFunc(String funcName) {
        return this.execFunc(funcName, null);
    }

    @Override
    public Object execScript(String scriptName, Object... args) {
        Script script = this.scripts.get(scriptName);

        if (script == null) {
            throw new IllegalStateException("js script '" + scriptName + "' doesn't exists in cache, you have to compile it first!");
        }

        if (args != null && args.length > 0) {
            throw new UnsupportedOperationException("javascript doesn't supports args for scripts.");
        }

        Log.d(SCRIPTS_TAG, "execute js script '" + scriptName + "'");

        Object res = script.exec(this.cx, this.scope);

        if (Undefined.isUndefined(res)) {
            return null;
        }

        return res;
    }

    @Override
    public void shutdown() {
        Context.exit();
    }

}
