package com.jukusoft.mmo.engine.applayer.utils;

public class FilePath {

    protected static String dataDir = "";
    protected static String configDirs = "";
    protected static String tempDir = "";

    protected FilePath () {
        //
    }

    public static String getDataDir() {
        return dataDir;
    }

    public static void setDataDir (String dataDir) {
        FilePath.dataDir = dataDir;
    }

    public static String getConfigDirs() {
        return configDirs;
    }

    public static void setConfigDirs(String configDirs) {
        FilePath.configDirs = configDirs;
    }

    public static String getTempDir() {
        return tempDir;
    }

    public static void setTempDir(String tempDir) {
        FilePath.tempDir = tempDir;
    }

    public static String parse (String path) {
        path = path.replace("{user.home}",System.getProperty("user.home") + "/");
        path = path.replace("{user.dir}", System.getProperty("user.dir") + "/");
        path = path.replace("{user.name}", System.getProperty("user.name") + "/");
        path = path.replace("{app.data}", PlatformUtils.getAppDataDir());
        path = path.replace("{java.io.tmpdir}", System.getProperty("java.io.tmpdir"));
        path = path.replace("{data.dir}", getDataDir());
        path = path.replace("{temp.dir}", getTempDir());

        //correct file / path seperators to use with libGDX
        path = path.replace("{file.seperator}", System.getProperty("file.separator"));
        path = path.replace("\\", "/");

        path = path.replace("/./", "/");

        return path;
    }

}
