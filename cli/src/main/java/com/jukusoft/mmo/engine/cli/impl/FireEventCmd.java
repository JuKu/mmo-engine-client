package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.client.events.input.PlayerMoveEvent;
import com.jukusoft.mmo.engine.shared.client.events.input.TakeScreenshotEvent;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

import java.util.HashMap;
import java.util.Map;

public class FireEventCmd implements CLICommand {

    //map with all available events to fire
    protected final Map<String,Class<?>> eventTypes;

    public FireEventCmd (Map<String,Class<?>> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @Override
    public String execute(String command, String[] args) {
        if (args.length < 1) {
            return "Invalide use of command! fireEvent <eventName> [args]";
        }

        //search for class
        Class<?> cls = eventTypes.get(args[0]);

        if (cls == null) {
            return "event type '" + args[0] + "' doesn't exists or isn't registered. Execute 'listEvents' to show all available events.";
        }

        Log.v("CLI", "fire event " + cls.getName());

        EventData event = (EventData) Pools.get(cls);

        //TODO: parse params

        Events.queueEvent(event);

        return "Event fired successfully!";
    }

    @Override
    public String getDescription() {
        return "fires an event";
    }

}
