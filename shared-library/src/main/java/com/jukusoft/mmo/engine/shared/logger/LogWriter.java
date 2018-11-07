package com.jukusoft.mmo.engine.shared.logger;

import com.jukusoft.mmo.engine.shared.config.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogWriter implements Runnable {

    protected final ConcurrentLinkedQueue<String> loggingQueue;

    protected final File file;

    protected static final String LOG_WRITER_TAG = "LogWriter";

    //config
    protected final boolean printToConsole;
    protected final boolean writeToFile;

    protected static LogListener logListener = null;

    protected LogWriter (final File file, final ConcurrentLinkedQueue<String> loggingQueue) {
        this.file = file;
        this.loggingQueue = loggingQueue;
        printToConsole = Config.getBool("Logger", "printToConsole");
        writeToFile = Config.getBool("Logger", "writeToFile");

        if (!this.file.exists()) {
            try {
                if (!this.file.createNewFile()) {
                    throw new IllegalStateException("Cannot create new log file '" + file.getAbsolutePath() + "'! Maybe wrong file permissions?");
                }
            } catch (IOException | IllegalStateException e) {
                Log.w(LOG_WRITER_TAG, "Exception while creating new log file: ", e);
            }
        }

        //set dummy log listener, if not listener was set
        if (logListener == null) {
            logListener = new LogListener() {
                @Override
                public void log(String str) {
                    //do anything here
                }
            };
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
                    fop.flush();

                    //sleep
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        //dont do anything
                    }
                } else {
                    str += System.lineSeparator();

                    fop.write(str.getBytes());
                    logListener.log(str);

                    if (printToConsole) {
                        System.out.println(str.substring(0, str.length() - System.lineSeparator().length()));
                    }
                }
            }

            fop.flush();

            if (!this.loggingQueue.isEmpty()) {
                System.err.println("write " + loggingQueue.size() + " remaining log entries to file...");

                //copy queue, so added logs doesn't have any effect anymore
                ConcurrentLinkedQueue<String> queueCopy = new ConcurrentLinkedQueue<>(this.loggingQueue);

                //write remaining logs
                for (int i = 0; i < queueCopy.size(); i++) {
                    //get and remove element from queue
                    String str = loggingQueue.poll();

                    if (str == null) {
                        fop.flush();
                        break;
                    } else {
                        str += System.lineSeparator();

                        fop.write(str.getBytes());
                        logListener.log(str);

                        if (printToConsole) {
                            System.out.println(str.substring(0, str.length() - System.lineSeparator().length()));
                        }
                    }
                }

                fop.flush();
                System.err.println("logs are written successfully! Shutdown LogWriter thread now.");
            }
        } catch (FileNotFoundException e) {
            Log.w(LOG_WRITER_TAG, "Couldn't found log file: " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            Log.w(LOG_WRITER_TAG, "IOException while write to log file: " + file.getAbsolutePath(), e);
        }
    }

    public static void attachListener (LogListener listener) {
        LogWriter.logListener = listener;
    }

}
