package com.jukusoft.mmo.engine.applayer.utils;

public class FilePath {

    protected static String dataDir = "";

    protected FilePath () {
        //
    }

    public static void setDataDir (String dataDir) {
        FilePath.dataDir = dataDir;
    }

    public static String parse (String path) {
        path = path.replace("{user.home}",System.getProperty("user.home") + "/");
        path = path.replace("{user.dir}", System.getProperty("user.dir") + "/");
        path = path.replace("{user.name}", System.getProperty("user.name") + "/");

        //correct file / path seperators to use with libGDX
        path = path.replace("{file.seperator}", System.getProperty("file.separator"));
        path = path.replace("\\", "/");

        path = path.replace("/./", "/");

        return path;
    }

}
