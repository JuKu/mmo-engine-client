package com.jukusoft.mmo.engine.applayer.utils;

import java.util.Locale;

/**
 * Created by Justin on 21.09.2017.
 */
public class PlatformUtils {

    /**
    * private constructor
    */
    private PlatformUtils() {
        //
    }

    public enum OS_TYPE {
        WINDOWS, LINUX, MAC_OS, UNKNOWN
    }

    //name of operating system
    protected static final String OS_NAME;

    //OS type
    protected static final OS_TYPE type;

    static {
        OS_NAME = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if (OS_NAME.contains("win")) {
            type = OS_TYPE.WINDOWS;
        } else if (OS_NAME.contains("nux")) {
            type = OS_TYPE.LINUX;
        } else if (OS_NAME.contains("mac") || OS_NAME.contains("darwin")) {
            type = OS_TYPE.MAC_OS;
        } else {
            type = OS_TYPE.UNKNOWN;
        }
    }

    public static  OS_TYPE getType () {
        return type;
    }

    public static boolean isWindows () {
        return type == OS_TYPE.WINDOWS;
    }

    public static boolean isLinux () {
        return type == OS_TYPE.LINUX;
    }

    public static boolean isMacOS () {
        return type == OS_TYPE.MAC_OS;
    }

    public static boolean isUnknown () {
        return type == OS_TYPE.UNKNOWN;
    }

    public static String getAppDataDir () {
        return PlatformUtils.isWindows() ? System.getenv("APPDATA") + "/" : "./";
    }

}
