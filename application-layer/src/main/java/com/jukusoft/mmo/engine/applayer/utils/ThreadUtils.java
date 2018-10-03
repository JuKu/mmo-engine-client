package com.jukusoft.mmo.engine.applayer.utils;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadUtils {

    protected ThreadUtils () {
        //
    }

    public static void executeInJavaFXThreadAndWait (Runnable runnable) {
        AtomicBoolean b = new AtomicBoolean(false);

        Platform.runLater(() -> {
            runnable.run();

            b.set(true);
        });

        while (!b.get()) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
