package com.jukusoft.mmo.engine.applayer.config;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Cache {

    protected static Cache instance = null;

    protected final String CACHE_PATH;

    protected Cache(String path) throws IOException {
        if (path == null) {
            throw new NullPointerException("Couldn't found config Paths.tempDir!");
        }

        if (!new File(path).exists()) {
            throw new IllegalStateException("cache directory doesn't exists: " + new File(path).getAbsolutePath());
        }

        if (!new File(path).isDirectory()) {
            throw new IllegalStateException("cache directory isn't a directory: " + new File(path).getAbsolutePath());
        }

        if (!path.endsWith("/")) {
            path += "/";
        }

        Log.d("Cache", "used cache directory: " + path);

        CACHE_PATH = path;

        //create directory, if neccessary
        FileUtils.createWritableDirIfAbsent(CACHE_PATH);
        FileUtils.createWritableDirIfAbsent(CACHE_PATH + "assets/");
        FileUtils.createWritableDirIfAbsent(CACHE_PATH + "security/");
        FileUtils.createWritableDirIfAbsent(CACHE_PATH + "maps/");
    }

    /**
    * initialize cache
    */
    public static void init (String path) throws IOException {
        instance = new Cache(path);
    }

    public static Cache getInstance () {
        if (instance == null) {
            throw new IllegalStateException("You havent initialized cache with Cache::init() before.");
        }

        return instance;
    }

    public String getPath () {
        return CACHE_PATH;
    }

    public void createDirIfAbsent (String dirName) {
        FileUtils.createWritableDirIfAbsent(CACHE_PATH + dirName + "/");
    }

    public String getCachePath (String dirName) {
        return CACHE_PATH + dirName + "/";
    }

    public void clear () throws IOException {
        FileUtils.recursiveDeleteDirectory(new File(CACHE_PATH));
    }

}
