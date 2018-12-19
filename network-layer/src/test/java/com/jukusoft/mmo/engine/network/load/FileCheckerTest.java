package com.jukusoft.mmo.engine.network.load;

import com.jukusoft.mmo.engine.shared.config.Cache;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FileCheckerTest {

    @Test
    public void testConstructor () {
        new FileChecker();
    }

    @Test
    public void testValidateFiles () throws IOException, InterruptedException {
        //init cach first
        Cache.init("../data/junit/file-checker-cache");

        Config.load(new File("../config/junit-logger2.cfg"));
        Log.init();

        Map<String,String> requiredFiles = new HashMap<>();
        requiredFiles.put("test.txt", "1234");
        requiredFiles.put("test1.txt", "1234");
        requiredFiles.put("test2.txt", "1234");
        requiredFiles.put("dir1/dir1test.txt", "ca2a0cf5b143e25b63fa7e2ef2ac54e7");
        requiredFiles.put("dir1/test1.txt", "1234");
        requiredFiles.put("dir1/test3.txt", "1234");

        List<String> invalideFiles = FileChecker.validateFiles(1, 1, requiredFiles);

        for (String file : invalideFiles) {
            System.err.println("invalide file: " + file);
        }

        System.err.println("shutdown logger now...");
        Log.shutdown();



        assertEquals(true, invalideFiles.contains("test.txt"));
        assertEquals(true, invalideFiles.contains("test1.txt"));
        assertEquals(true, invalideFiles.contains("test2.txt"));
        assertEquals(false, invalideFiles.contains("dir1/dir1test.txt"));
        assertEquals(true, invalideFiles.contains("dir1/test1.txt"));
        assertEquals(true, invalideFiles.contains("dir1/test3.txt"));

        assertEquals(5, invalideFiles.size());

        //clear log file
        new File("../temp-empty/logs/junit-log.txt").delete();
    }

}
