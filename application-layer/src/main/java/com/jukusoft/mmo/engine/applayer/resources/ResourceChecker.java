package com.jukusoft.mmo.engine.applayer.resources;

import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;
import com.jukusoft.mmo.engine.applayer.utils.JavaFXUtils;
import com.jukusoft.mmo.engine.applayer.utils.Utils;
import org.jutils.jhardware.HardwareInfo;
import org.jutils.jhardware.model.MemoryInfo;

import java.io.File;

public class ResourceChecker {

    public static void check () {
        Utils.printSection("Resource Checker");

        File dir = new File(FilePath.getTempDir());
        long totalSpace = dir.getTotalSpace();
        long freeSpace = dir.getFreeSpace();
        long usableSpace = dir.getUsableSpace();

        Log.i("Hard Drive", "total space: " + (totalSpace / 1024 / 1024 / 1024) + "GB");
        Log.i("Hard Drive", "free space: " + (freeSpace / 1024 / 1024 / 1024) + "GB");
        Log.i("Hard Drive", "usable space: " + (usableSpace / 1024 / 1024 / 1024) + "GB");

        //check required hard drive space
        long freeSpaceMB = usableSpace / 1024 / 1024;
        long requiredSpace = Config.getInt("SystemRequirements", "freeHardDriveSpace");

        if (freeSpaceMB < Config.getInt("SystemRequirements", "freeHardDriveSpace")) {
            //not enough hard drive space to start the game
            Log.w("Hard Drive", "Not enough hard drive space to start the game!");
            Log.w("Hard Drive", requiredSpace + "MB required, but only " + freeSpaceMB + "MB free hard drive space.");

            //show error dialog
            JavaFXUtils.startJavaFX();
            JavaFXUtils.showErrorDialog("Not enough free hard drive space available to start this game!\n\nFree space: " + freeSpaceMB + "MB\nRequired space: " + requiredSpace + "MB");

            Gdx.app.exit();
        }

        //check CPU
        int cores = Runtime.getRuntime().availableProcessors();

        //check RAM (in MB)
        MemoryInfo memoryInfo = HardwareInfo.getMemoryInfo();
        long freeMemory = Long.parseLong(memoryInfo.getFreeMemory()) / 1024;
        long maxMemory = Long.parseLong(memoryInfo.getTotalMemory()) / 1024;
        long availableMemory = Long.parseLong(memoryInfo.getAvailableMemory()) / 1024;

        Log.i("RAM", "free memory: " + freeMemory + " GB");
        Log.i("RAM", "max memory: " + maxMemory + " GB");
        Log.i("RAM", "available memory: " + availableMemory + " GB");
    }

}
