package com.jukusoft.mmo.engine.gameview.input;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;

import java.io.File;

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
