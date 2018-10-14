package com.jukusoft.mmo.engine.applayer.script.rhino;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.i18n.I;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.utils.ExceptionUtils;
import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
import org.mozilla.javascript.*;

import java.io.File;
import java.io.IOException;
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

        //ExceptionUtils.logException(SCRIPTS_TAG, "Exception while initializing JSRhinoScriptEngine: ", () -> ScriptableObject.defineClass(this.scope, Log.class));

        //https://stackoverflow.com/questions/14561874/calling-jsfunction-from-javascript-typeerror-cannot-find-default-value-for-ob

        //https://stackoverflow.com/questions/39771148/calling-java-function-from-rhino
        //ScriptableObject.putProperty(this.scope, "log", Context.javaToJS(new Log(), this.scope));
    }

    @Override
    public void compile(String scriptName, String programStr) throws ScriptLoadException {
        String fileDir;

        try {
            File currentDir = FileUtils.getRelativeFile(new File(scriptName), new File("."));
            fileDir = currentDir.getPath().replace("\\", "/");

            //remove filename
            fileDir = fileDir.substring(0, fileDir.lastIndexOf("/") + 1);
        } catch (IOException e) {
            Log.w(SCRIPTS_TAG, "IOException while getting current relative file path of lua script: ", e);
            throw new ScriptLoadException("IOException while loading script: ", e);
        }

        programStr = "/*define global variable for relative dir path*/var relDir = \"" + fileDir + "\";\n" + programStr;

        //this.cx.evaluateString(scope, programStr, scriptName, 1, null);

        //compile string
        Script script = this.cx.compileString(programStr, scriptName, 1 - 2, null);

        this.scripts.put(scriptName, script);
    }

    @Override
    public void loadFile(File file) throws ScriptLoadException {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            throw new ScriptLoadException("js script file doesn't exists: " + file.getAbsolutePath());
        }

        ExceptionUtils.throwSLEOnException(SCRIPTS_TAG, "Cannot read script file: " + file.getAbsolutePath() + ", IOException: ", () -> {
            File relFile = FileUtils.getRelativeFile(file, new File("."));
            String scriptName = relFile.getPath().replace("\\", "/");

            //only compile this file, if it isn't in cache
            if (!this.scripts.containsKey(scriptName)) {
                String content = FileUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
                this.compile(scriptName, content);
            }

            this.execScript(scriptName);
        });
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

        script.exec(this.cx, this.scope);

        return null;
    }

    @Override
    public void shutdown() {
        Context.exit();
    }

}
