package com.jukusoft.mmo.engine.gameview.renderer.map;

public class MapLoaderException extends Exception {

    public MapLoaderException (String message, Throwable e) {
        super(message, e);
    }

    public MapLoaderException (String message) {
        super(message);
    }

}
