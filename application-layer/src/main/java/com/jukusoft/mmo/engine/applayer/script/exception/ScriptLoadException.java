package com.jukusoft.mmo.engine.applayer.script.exception;

public class ScriptLoadException extends Exception {

    public ScriptLoadException (String message) {
        super(message);
    }

    public ScriptLoadException (String message, Throwable e) {
        super(message, e);
    }

}
