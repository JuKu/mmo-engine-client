package com.jukusoft.mmo.engine.shared.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void testConstructor () {
        new FileUtils();
    }

    @Test (expected = FileNotFoundException.class)
    public void testListFilesNotExistentDir () throws IOException {
        FileUtils.listFiles(new File("not-existent-file.txt"), (file, relFilePath) -> {
            //don't do anything here
        });
    }

    @Test (expected = IllegalArgumentException.class)
    public void testListFilesFileInsteadOfDir () throws IOException {
        FileUtils.listFiles(new File("../data/junit/test.txt"), (file, relFilePath) -> {
            //don't do anything here
        });
    }

    @Test
    public void testListFiles () throws IOException {
        List<String> relPaths = FileUtils.listFiles(new File("../data/junit/list-files"));

        assertEquals(4, relPaths.size());

        assertEquals(true, relPaths.contains("test2.txt"));
        assertEquals(true, relPaths.contains("dir1/test1.txt"));
        assertEquals(true, relPaths.contains("dir1/test3.txt"));
        assertEquals(true, relPaths.contains("dir1/dir2/dir2test.txt"));
    }

    @Test
    public void testRemoveDoubleDotInDir () {
        assertEquals("/test/test2/", FileUtils.removeDoubleDotInDir("/test/test3/../test2/"));
        assertEquals("C:/Users/test/testdir/.game/terrain.png", FileUtils.removeDoubleDotInDir("C:/Users/test/testdir/.game/cache/../terrain.png"));
    }

}
