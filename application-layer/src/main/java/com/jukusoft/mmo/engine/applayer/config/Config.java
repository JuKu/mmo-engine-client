package com.jukusoft.mmo.engine.applayer.config;

import com.badlogic.gdx.files.FileHandle;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    //configuration values
    protected static final ObjectObjectMap<String,String> values = new ObjectObjectHashMap<>();

    protected Config () {
        //
    }

    public static void load (FileHandle file) throws IOException {
        Objects.requireNonNull(file, "config file cannot be null.");

        Logger.getAnonymousLogger().log(Level.INFO, "Load Config: " + file.path());

        if (!file.exists()) {
            throw new IllegalStateException("config file '" + file.path() + "' doesn't exists!");
        }

        Ini ini = new Ini(file.file());

        //import all sections
        for (Map.Entry<String, Profile.Section> entry: ini.entrySet()) {
            String key = entry.getKey();
            Profile.Section section = entry.getValue();

            for (Map.Entry<String, String> option : section.entrySet()) {
                values.put(key + "." + option.getKey(),option.getValue());
            }
        }
    }

    public static String get (String section, String key) {
        String option = section + "." + key;

        //first check, if key exists
        if (!values.containsKey(option)) {
            throw new IllegalStateException("Config key '" + option + "' doesn't exists!");
        }

        return values.get(option);
    }

    public static boolean getBool (String section, String key) {
        return Boolean.parseBoolean(get(section, key));
    }

    public static int getInt (String section, String key) {
        return Integer.parseInt(get(section, key));
    }

}
