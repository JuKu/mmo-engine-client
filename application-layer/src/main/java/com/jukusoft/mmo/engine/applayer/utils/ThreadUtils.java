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

            //runnable was executed, so we don't need to block main thread any longer
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

    public static void executeOnUIThreadAndWait (Runnable runnable) {
        AtomicBoolean b = new AtomicBoolean(false);

        com.jukusoft.mmo.engine.applayer.utils.Platform.runOnUIThread(() -> {
            runnable.run();

            b.set(true);
        });

        while (!b.get()) {
            try {
                Thread.currentThread().sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
