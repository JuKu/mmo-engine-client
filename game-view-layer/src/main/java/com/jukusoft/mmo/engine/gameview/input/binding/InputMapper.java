package com.jukusoft.mmo.engine.gameview.input.binding;

import com.badlogic.gdx.Input;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.gameview.input.binding.KeyBinding;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class InputMapper {

    protected static final String LOG_TAG = "InputMapper";

    protected KeyBinding[] bindings = null;

    //map with key bindings
    protected ObjectObjectMap<String,KeyBinding> templates = new ObjectObjectHashMap<>();

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

        Log.v(LOG_TAG, "fields in class Input.Keys found: " + cls.getDeclaredFields().length);
        int highestValue = 0;

        //first detect highest value to allocate array size
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                int keyCode = field.getInt(null);

                if (highestValue < keyCode) {
                    highestValue = keyCode;
                }
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "IllegalAccessException while getting highest keyCode: ", e);
            } catch (IllegalArgumentException e) {
                //we can ignore this exception, because it occurs if we access a non-static variable (which we doesn't need)
            }
        }

        Log.v(LOG_TAG, "highest value in class Input.Keys: " + highestValue);

        //allocate array for all key bindings
        this.bindings = new KeyBinding[highestValue];

        //iterate through keys
        for (Map.Entry<String,String> entry : section.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            //find variable and get value
            Field field = null;

            try {
                field = cls.getDeclaredField(key);

                field.setAccessible(true);
                int keyCode = field.getInt(null);

                //check, if template is registered for this value
                KeyBinding template = this.templates.get(value);

                if (template == null) {
                    Log.w(LOG_TAG, "no template registered for action type '" + value + "'!");
                    continue;
                }

                //register template to keyCode
                Log.v(LOG_TAG, "register keyboard binding " + key + " (" + keyCode + ") --> " + template.getClass().getName());
                this.bindings[keyCode] = template;
            } catch (NoSuchFieldException e) {
                Log.e(LOG_TAG, "Illegal keyboard binding '" + key + "', this variable doesn't exists in libGDX class Input.Keys!", e);
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "IllegalAccessException while get keyboard binding '" + key + "': ", e);
            }
        }
    }

    public void registerTemplate (String actionType, KeyBinding binding) {
        this.templates.put(actionType, binding);
    }

    public KeyBinding[] getBindings () {
        return bindings;
    }

}
