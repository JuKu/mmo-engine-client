package com.jukusoft.mmo.engine.shared.map.parser;

public class TiledParserException extends Exception {

    public TiledParserException(String message, Throwable e) {
        super(message, e);
    }

    public TiledParserException(String message) {
        super(message);
    }

}
