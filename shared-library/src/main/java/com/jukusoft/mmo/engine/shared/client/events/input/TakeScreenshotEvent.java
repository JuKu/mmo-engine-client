package com.jukusoft.mmo.engine.shared.client.events.input;

import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventData;

/**
 * event which is fired from input system to take a screenshot
 */
public class TakeScreenshotEvent extends EventData {

    @Override
    public int getEventType() {
        return ClientEvents.TAKE_SCREENSHOT.getID();
    }

}
