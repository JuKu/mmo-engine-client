package com.jukusoft.mmo.engine.applayer.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Platform {

    protected static Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public static void runOnUIThread (Runnable runnable) {
        queue.add(runnable);
    }

    public static void executeQueue () {
        while (!queue.isEmpty()) {
            Runnable runnable = queue.poll();

            if (runnable != null) {
                runnable.run();
            }
        }
    }

    protected static void clearQueue () {
        queue.clear();
    }

    protected static int getQueueSize () {
        return queue.size();
    }

}
