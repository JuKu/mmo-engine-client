package com.jukusoft.mmo.engine.applayer.script.lua;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.IScriptEngine;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptExecutionException;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;
import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LuaScriptEngine implements IScriptEngine {

    protected StateContext state = null;
    protected Table env = null;

    //map with all registered global lua functions
    protected ObjectObjectMap<String, LuaFunction> luaFunctions = new ObjectObjectHashMap<>();

    protected static final String SCRIPTS_TAG = "Scripts";

    public LuaScriptEngine () {
        //initialize state
        this.state = StateContexts.newDefaultInstance();
        this.env = StandardLibrary.in(RuntimeEnvironments.system()).installInto(state);

        //register global functions
        this.env.rawset("logd", new LogDebugFunc());
        this.env.rawset("logi", new LogInfoFunc());

        this.env.rawset("now", new NowFunc());

        //add function to load other scripts
        this.env.rawset("dofile", new DoFileFunc(this));
    }

    /**
     * compile lua script as string to function
     *
     * //@param rootClassPrefix  the class name prefix for compiled classes, must not be {@code null}
     */
    @Override
    public void compile (String scriptName, String programStr) throws ScriptLoadException {
        Log.d(SCRIPTS_TAG, "compile script '" + scriptName + "'");

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

        programStr = "--define global variable for relative dir path\nrelDir = \"" + fileDir + "\"; " + programStr;

        scriptName = "script_" + scriptName;

        //compile
        ChunkLoader loader = CompilerChunkLoader.of("engine");
        LuaFunction func = null;
        try {
            func = loader.loadTextChunk(new Variable(env), scriptName, programStr);
        } catch (LoaderException e) {
            Log.e(SCRIPTS_TAG, "Couldn't compile lua script: " + programStr + ", error: " + e.getLuaStyleErrorMessage(), e);
            throw new ScriptLoadException("Coulnd't compile script: " + programStr + ", error: " + e.getLuaStyleErrorMessage());
        }

        this.luaFunctions.put(scriptName, func);
    }

    @Override
    public void loadFile(File file) throws ScriptLoadException {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            throw new ScriptLoadException("lua script file doesn't exists: " + file.getAbsolutePath());
        }

        try {
            File relFile = FileUtils.getRelativeFile(file, new File("."));
            String scriptName = relFile.getPath().replace("\\", "/");

            String scriptName1 = "script_" + scriptName;

            //only compile this file, if it isn't in cache
            if (!this.luaFunctions.containsKey(scriptName1)) {
                String content = FileUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
                this.compile(scriptName, content);
            }

            this.execScript(scriptName);
        } catch (IOException e) {
            Log.e(SCRIPTS_TAG, "IOException while loading lua script file: ", e);
            throw new ScriptLoadException("Cannot read script file: " + file.getAbsolutePath() + ", IOException: " + e.getLocalizedMessage());
        } catch (CallException e) {
            throw new ScriptExecutionException(e);
        }
    }

    @Override
    public Object execFunc(String funcName, Object... args) {
        // get a reference to the function
        LuaFunction func = (LuaFunction) env.rawget(funcName);

        if (func == null) {
            throw new NullPointerException("lua function cannot be null.");
        }

        try {
            Object[] objs = DirectCallExecutor.newExecutor().call(state, func, args);

            if (objs.length > 0) {
                return objs[0];
            } else {
                //it's a void method
                return null;
            }
        } catch (CallException e) {
            Log.w(SCRIPTS_TAG, "CallException: ", e);
            throw new ScriptExecutionException(e);
        } catch (CallPausedException e) {
            Log.w(SCRIPTS_TAG, "CallPausedException: ", e);
        } catch (InterruptedException e) {
            Log.w(SCRIPTS_TAG, "InterruptedException: ", e);
        }

        return null;
    }

    @Override
    public Object execFunc(String funcName) throws CallException {
        return this.execFunc(funcName, new Object[0]);
    }

    @Override
    public Object execScript(String scriptName, Object... args) throws CallException {
        String fileName = scriptName;
        Log.v(SCRIPTS_TAG, "execScript: " + scriptName);

        scriptName = "script_" + scriptName;

        LuaFunction func = this.luaFunctions.get(scriptName);

        if (func == null) {
            throw new IllegalStateException("lua script '" + scriptName + "' doesn't exists in cache, you have to compile it first!");
        }

        Log.d(SCRIPTS_TAG, "execute lua script '" + scriptName + "'");

        try {
            Object[] objs = DirectCallExecutor.newExecutor().call(state, func);

            if (objs.length > 0) {
                return objs[0];
            } else {
                //it's a void method
                return null;
            }
        } catch (CallException e) {
            Log.w(SCRIPTS_TAG, "CallException: ", e);
            throw e;
            //throw new ScriptExecutionException("CallException: " + e.getLocalizedMessage());
        } catch (CallPausedException e) {
            Log.w(SCRIPTS_TAG, "CallPausedException: ", e);
        } catch (InterruptedException e) {
            Log.w(SCRIPTS_TAG, "InterruptedException: ", e);
        }

        return null;
    }

    protected void printEnvDebug () {
        for (long i = 0; i < env.rawlen(); i++) {
            Log.d(SCRIPTS_TAG, "rawget[" + i + "]: " + env.rawget(i));
        }
    }

}
