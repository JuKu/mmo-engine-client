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
                if (!this.file.createNewFile()) {
                    throw new IllegalStateException("Cannot create new log file '" + file.getAbsolutePath() + "'! Maybe wrong file permissions?");
                }
            } catch (IOException | IllegalStateException e) {
                Log.w("LogWriter", "Exception while creating new log file: ", e);
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
                        //dont do anything
                    }
                } else {
                    fop.write(str.getBytes());

                    if (printToConsole) {
                        System.out.println(str);
                    }
                }
            }

            fop.flush();
        } catch (FileNotFoundException e) {
            Log.w("LogWriter", "Couldn't found log file: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            Log.w("LogWriter", "IOException while write to log file: " + file.getAbsolutePath(), e);
        }
    }

}
