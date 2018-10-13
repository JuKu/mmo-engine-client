package com.jukusoft.mmo.engine.applayer.utils;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.script.exception.ScriptLoadException;

public class ExceptionUtils {

    protected static final String SCRIPTS_TAG = "Scripts";

    protected ExceptionUtils () {
        //
    }

    public static void logException (String tag, String message, RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            Log.e(tag, message, e);
        }
    }

    public static void throwSLEOnException(String tag, String message, RunnableWithException runnable) throws ScriptLoadException {
        try {
            runnable.run();
        } catch (Exception e) {
            Log.e(tag, message, e);
            throw new ScriptLoadException(message, e);
        }
    }

}
