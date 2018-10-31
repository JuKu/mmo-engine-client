package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.client.events.input.TakeScreenshotEvent;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

public class TakeScreenshotCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        Events.queueEvent(Pools.get(TakeScreenshotEvent.class));
        return "Take screenshot!";
    }

    @Override
    public String getDescription() {
        return "takes a screenshot";
    }

}
