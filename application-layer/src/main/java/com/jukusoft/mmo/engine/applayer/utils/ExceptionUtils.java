package com.jukusoft.mmo.engine.applayer.utils;

import com.jukusoft.mmo.engine.applayer.logger.Log;

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

}
