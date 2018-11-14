package com.jukusoft.mmo.engine.shared.logger;

@FunctionalInterface
public interface LogListener {

    /**
    * log message
     *
     * @param str log message string
    */
    public void log (String str);

}
