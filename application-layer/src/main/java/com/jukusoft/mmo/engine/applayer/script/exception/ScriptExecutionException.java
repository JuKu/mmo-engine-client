package com.jukusoft.mmo.engine.applayer.script.exception;

public class ScriptExecutionException extends RuntimeException {

    public ScriptExecutionException (String message) {
        super(message);
    }

    public ScriptExecutionException (Throwable e) {
        super(e);
    }

}
