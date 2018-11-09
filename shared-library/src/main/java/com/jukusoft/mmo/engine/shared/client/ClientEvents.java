package com.jukusoft.mmo.engine.shared.client;

public enum ClientEvents {

    MOVE_PLAYER(1),
    TAKE_SCREENSHOT(2),
    SELECT_SERVER(3),
    CONNECTION_ESTABLISHED(4),
    CONNECTION_FAILED(5),
    CONNECTION_LOST(6);

    private final int id;

    ClientEvents (int id) {
        this.id = id;
    }

    public int getID () {
        return id;
    }

}
