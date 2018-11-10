package com.jukusoft.mmo.engine.shared.config;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.jukusoft.mmo.engine.shared.logger.Log;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Config {

    //configuration values
    protected static final ObjectObjectMap<String,String> values = new ObjectObjectHashMap<>();

    public static boolean forceExit = true;

    protected Config () {
        //
    }

    public static void load (File file) throws IOException {
        Objects.requireNonNull(file, "config file cannot be null.");

        Log.i("Config", "Load Config: " + file.getAbsolutePath().replace("\\", "/"));

        if (!file.exists()) {
            throw new IllegalStateException("config file '" + file.getAbsolutePath() + "' doesn't exists!");
        }

        if (!file.isFile()) {
            throw new IllegalStateException("config file '" + file.getAbsolutePath() + "' isn't a file!");
        }

        //skip files with ".example." and "travis" in filename
        if (file.getName().contains(".example.") || file.getName().contains("travis")) {
            Log.v("Config", "skip example config file: " + file.getAbsolutePath());
            return;
        }

        Ini ini = new Ini(file);

        //import all sections
        for (Map.Entry<String, Profile.Section> entry: ini.entrySet()) {
            String key = entry.getKey();
            Profile.Section section = entry.getValue();

            for (Map.Entry<String, String> option : section.entrySet()) {
                values.put(key + "." + option.getKey(),option.getValue());
            }
        }
    }

    public static void loadDir (File dir) throws IOException {
        if (!dir.exists()) {
            throw new IllegalStateException("config directory doesn't exists: " + dir.getAbsolutePath());
        }

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("dir isn't a directory!");
        }

        File[] files = dir.listFiles();

        for (File file : files) {
            //check, if file is a file
            if (!file.isFile()) {
                //its a directory, so skip this file instance
                continue;
            }

            //check file ending
            if (!file.getName().endsWith(".cfg") && !file.getName().endsWith(".ini")) {
                //its not a config file, so skip this file instance
                Log.v("Config", "skip file: " + file.getAbsolutePath());
                continue;
            }

            Config.load(file);
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

    public static String getOrDefault (String section, String key, String defaultValue) {
        String option = section + "." + key;

        //first check, if key exists
        if (!values.containsKey(option)) {
            return defaultValue;
        }

        return values.get(option);
    }

    public static boolean getBool (String section, String key) {
        return Boolean.parseBoolean(get(section, key));
    }

    public static int getInt (String section, String key) {
        return Integer.parseInt(get(section, key));
    }

    public static float getFloat (String section, String key) {
        return Float.parseFloat(get(section, key));
    }

    public static void set (String section, String key, String value) {
        values.put(section + "." + key, value);
    }

}
