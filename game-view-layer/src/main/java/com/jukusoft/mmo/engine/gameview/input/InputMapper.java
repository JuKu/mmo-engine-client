package com.jukusoft.mmo.engine.gameview.input;

import com.badlogic.gdx.Input;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class InputMapper {

    //protected IntObjectMap<InputActions> keyDownMapping = new IntObjectHashMap<>();
    //protected IntObjectMap<InputActions> keyUpMapping = new IntObjectHashMap<>();

    protected static final String LOG_TAG = "InputMapper";

    public InputMapper () {
        //
    }

    /**
    * load keyboard bindings
     *
     * @param configFile keyboard bindings file, e.q. keyboard.cfg
    */
    public void load (File configFile) {
        Log.i(LOG_TAG, "load keyboard bindings...");

        if (!configFile.exists()) {
            throw new IllegalStateException("keyboard bindings configuration file doesn't exists: " + configFile.getAbsolutePath());
        }

        Log.d(LOG_TAG, "load keyboard bindings from file: " + configFile.getAbsolutePath());

        Ini ini = null;

        try {
            ini = new Ini(configFile);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while loading keyboard bindings: ", e);
            throw new IllegalStateException("Coulnd't load keyboard bindings!");
        }

        //get keyboard mapping section
        Profile.Section section = ini.get("KeyboardMapping");

        Class<?> cls = Input.Keys.class;

        //iterate through keys
        for (String key : section.keySet()) {
            //find variable and get value
            Field field = null;
            String value = section.get(key);

            try {
                field = cls.getDeclaredField(key);

                field.setAccessible(true);
                int keyCode = field.getInt(null);

                Log.v(LOG_TAG, "register keyboard binding " + key + " (" + keyCode + ") --> " + value);
            } catch (NoSuchFieldException e) {
                Log.e(LOG_TAG, "Illegal keyboard binding '" + key + "', this variable doesn't exists in libGDX class Input.Keys!", e);
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "IllegalAccessException while get keyboard binding '" + key + "': ", e);
            }
        }

        //TODO: add code here
    }

    /*public void init () {
        //TODO: read config file and add mappings to map
    }

    public InputActions getKeyDownAction (int keyCode) {
        return this.keyDownMapping.get(keyCode);
    }

    public InputActions getKeyUpAction (int keyCode) {
        return this.keyDownMapping.get(keyCode);
    }

    public void addMapping(int keyCode, InputActions action) {
        if (this.keyDownMapping.containsKey(keyCode)) {
            throw new IllegalStateException("keyCode is already registered.");
        }

        this.keyDownMapping.put(keyCode, action);
    }

    public void removeMapping (int keyCode) {
        this.keyDownMapping.remove(keyCode);
    }*/

}
