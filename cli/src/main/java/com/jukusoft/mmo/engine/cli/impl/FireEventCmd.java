package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

import java.lang.reflect.Field;
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

        for (int i = 1; i < args.length; i++) {
            String[] array = args[i].split("=");

            if (array.length != 2) {
                return "Invalid array length, parameters has to be set in this form 'fireEvent EVENT_CLASS param1=value1 param2=value2'!";
            }

            String key = array[0];
            String value = array[1];

            try {
                Field field = cls.getField(key);
                field.setAccessible(true);

                switch (field.getType().getSimpleName().toLowerCase()) {
                    case "int":
                    case "integer":
                        field.set(event, Integer.parseInt(value));
                        break;

                    case "float":
                        field.set(event, Float.parseFloat(value));
                        break;

                    default:
                        field.set(event, value);
                        break;
                }
            } catch (NoSuchFieldException e) {
                return "Invalid parameter '" + key + "' (class doesn't contains such a field)!";
            } catch (IllegalAccessException e) {
                return "IllegalAccessException was thrown.";
            }
        }

        Events.queueEvent(event);

        return "Event fired successfully!";
    }

    @Override
    public String getDescription() {
        return "fires an event";
    }

    @Override
    public String getParams() {
        return "EVENT_CLASS [PARAMS]";
    }
}
