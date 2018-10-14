package com.jukusoft.mmo.engine.gameview.screens;

/**
 * Thrown when a pushed screen is not found.
 */
public class ScreenNotFoundException extends RuntimeException {

    public ScreenNotFoundException(String message) {
        super(message);
    }

}
