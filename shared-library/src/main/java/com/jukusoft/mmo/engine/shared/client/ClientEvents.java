package com.jukusoft.mmo.engine.shared.client;

public enum ClientEvents {

    MOVE_PLAYER(1),
    TAKE_SCREENSHOT(2),
    SELECT_SERVER(3),
    CONNECTION_ESTABLISHED(4),
    CONNECTION_FAILED(5),
    CONNECTION_LOST(6),
    PUBLIC_KEY_RECEIVED(7),
    CONNECTION_READY(8),
    PING_CHANGED(9),
    LOGIN_REQUEST(10),
    LOGIN_RESPONSE(11),
    CHARACTER_LIST_RECEIVED(12),
    TEST_ERROR(13),
    CREATE_CHARACTER(14),
    CREATE_CHARACTER_RESPONSE(15),
    ENTER_GAME_WORLD(16),
    ENTERED_GAME_WORLD(17),
    ALL_SUBSYSTEMS_READY(18),
    LOAD_MAP(19);

    private final int id;

    ClientEvents (int id) {
        this.id = id;
    }

    public int getID () {
        return id;
    }

}
