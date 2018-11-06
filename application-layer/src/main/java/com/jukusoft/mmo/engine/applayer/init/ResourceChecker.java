package com.jukusoft.mmo.engine.applayer.init;

import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.*;
import com.jukusoft.mmo.engine.shared.utils.FilePath;
import com.jukusoft.mmo.engine.shared.utils.Utils;

import java.io.File;

public class ResourceChecker {

    protected static final String HARD_DRIVE_TAG = "Hard Drive";
    protected static final String SR_SECTION = "SystemRequirements";

    protected ResourceChecker () {
        //
    }

    public static void check () {
        Utils.printSection("Check system resources");

        File dir = new File(FilePath.getTempDir());
        long totalSpace = dir.getTotalSpace();
        long freeSpace = dir.getFreeSpace();
        long usableSpace = dir.getUsableSpace();

        Log.i(HARD_DRIVE_TAG, "total space: " + (totalSpace / 1024 / 1024 / 1024) + "GB");
        Log.i(HARD_DRIVE_TAG, "free space: " + (freeSpace / 1024 / 1024 / 1024) + "GB");
        Log.i(HARD_DRIVE_TAG, "usable space: " + (usableSpace / 1024 / 1024 / 1024) + "GB");

        //check required hard drive space
        long freeSpaceMB = usableSpace / 1024 / 1024;
        long requiredSpace = Config.getInt(SR_SECTION, "freeHardDriveSpace");

        if (freeSpaceMB < Config.getInt(SR_SECTION, "freeHardDriveSpace")) {
            //not enough hard drive space to start the game
            Log.w(HARD_DRIVE_TAG, "Not enough hard drive space to start the game!");
            Log.w(HARD_DRIVE_TAG, requiredSpace + "MB required, but only " + freeSpaceMB + "MB free hard drive space.");

            //show error dialog
            JavaFXUtils.startJavaFX();
            JavaFXUtils.showErrorDialog("Not enough free hard drive space available to start this game!\n\nFree space: " + freeSpaceMB + "MB\nRequired space: " + requiredSpace + "MB");

            Gdx.app.exit();
        }

        //check CPU
        int cores = Runtime.getRuntime().availableProcessors();
        int requiredCores = Config.getInt(SR_SECTION, "minCores");

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
