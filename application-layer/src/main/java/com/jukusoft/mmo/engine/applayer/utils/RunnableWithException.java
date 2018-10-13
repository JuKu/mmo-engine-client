package com.jukusoft.mmo.engine.applayer.utils;

@FunctionalInterface
public interface RunnableWithException {

    public void run () throws Exception;

}
