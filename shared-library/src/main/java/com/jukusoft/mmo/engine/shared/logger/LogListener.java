package com.jukusoft.mmo.engine.shared.logger;

@FunctionalInterface
public interface LogListener {

    /**
    * log message
    */
    public void log (String str);

}
