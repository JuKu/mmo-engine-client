package com.jukusoft.mmo.engine.shared.client.events.input;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
* event which is fired from input system or network system to move player's position
*/
public class PlayerMoveEvent extends EventData {

    //move direction (vector 2D)
    public float x = 0;
    public float y = 0;

    //movement speed between 0 and 1 (e.q. used from controller input)
    public float speed = 0;

    @Override
    public int getEventType() {
        return ClientEvents.MOVE_PLAYER.getID();
    }

}
