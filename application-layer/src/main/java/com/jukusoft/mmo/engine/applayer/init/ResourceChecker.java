package com.jukusoft.mmo.engine.applayer.init;

import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.*;
import org.jutils.jhardware.HardwareInfo;
import org.jutils.jhardware.model.MemoryInfo;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceChecker {

    public static void check () {
        Utils.printSection("Check system resources");

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
        int requiredCores = Config.getInt("SystemRequirements", "minCores");

        Log.i("CPU", "cores: " + cores);

        if (cores < requiredCores) {
            Log.w("CPU", "Not enough cores to play the game, value: " + cores + ", required cores: " + requiredCores);

            //show error dialog
            JavaFXUtils.startJavaFX();
            JavaFXUtils.showErrorDialog("Not enough cores to play the game, value: " + cores + ", required cores: " + requiredCores);

            Gdx.app.exit();
        }
    }

}
