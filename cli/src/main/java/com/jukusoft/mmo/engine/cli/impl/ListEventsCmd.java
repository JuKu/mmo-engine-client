package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;

import java.util.Map;

public class ListEventsCmd implements CLICommand {

    protected final Map<String,Class<?>> eventTypes;

    public ListEventsCmd (Map<String,Class<?>> eventTypes) {
        this.eventTypes = eventTypes;
    }

    @Override
    public String execute(String command, String[] args) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String,Class<?>> entry : this.eventTypes.entrySet()) {
            sb.append("  - " + entry.getKey() + " (" + entry.getValue() + ")\n");
        }

        return sb.toString();
    }

    @Override
    public String getDescription() {
        return "list all available events";
    }

}
