package com.jukusoft.mmo.engine.applayer.utils;

public class FilePath {

    protected FilePath () {
        //
    }

    public static String parse (String path) {
        path = path.replace("{user.home}",System.getProperty("user.home"));
        path = path.replace("{file.seperator}", System.getProperty("file.separator"));
        path = path.replace("{user.dir}", System.getProperty("user.dir"));
        path = path.replace("{user.name}", System.getProperty("user.name"));

        return path;
    }

}
