package com.jukusoft.mmo.engine.applayer.logger;

import com.jukusoft.mmo.engine.applayer.config.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogWriter implements Runnable {

    protected final ConcurrentLinkedQueue<String> loggingQueue;

    protected final File file;

    //config
    protected final boolean printToConsole;

    protected LogWriter (final File file, final ConcurrentLinkedQueue<String> loggingQueue) {
        this.file = file;
        this.loggingQueue = loggingQueue;
        printToConsole = Config.getBool("Logger", "printToConsole");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try (FileOutputStream fop = new FileOutputStream(this.file)) {
            while (!Thread.interrupted()) {
                //write queue to file

                //get and remove element from queue
                String str = loggingQueue.poll();

                if (str == null) {
                    //sleep
                    try {
                        fop.flush();
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                fop.write(str.getBytes());

                if (printToConsole) {
                    System.out.println(str);
                }
            }

            fop.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
