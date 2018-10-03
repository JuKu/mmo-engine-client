package com.jukusoft.mmo.engine.applayer.utils;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadUtils {

    public static void executeInJavaFXThreadAndWait (Runnable runnable) {
        AtomicBoolean b = new AtomicBoolean(false);
        Thread currentThread = Thread.currentThread();

        Platform.runLater(() -> {
            runnable.run();

            b.set(true);

            //wake up thread
            //currentThread.notify();
        });

        while (!b.get()) {
            try {
                currentThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
