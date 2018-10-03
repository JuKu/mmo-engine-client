package com.jukusoft.mmo.engine.applayer.config;

import com.badlogic.gdx.files.FileHandle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    @Test
    public void testConstructor () {
        new Config();
    }

    @Test (expected = IllegalStateException.class)
    public void testLoadNotExistentFile () throws IOException {
        Config.load(new File("not-existent-file"));
    }

    @Test
    public void testLoad () throws IOException {
        Config.load(new File("../config/junit-logger.cfg"));

        assertEquals(true, Config.getBool("Logger", "enabled"));
        assertEquals(true, Config.getBool("Logger", "printToConsole"));
        assertEquals("VERBOSE", Config.get("Logger", "level"));
        assertEquals(10, Config.getInt("Logger", "testInt"));
        assertEquals(1.2f, Config.getFloat("Logger", "testFloat"), 0.0001f);
    }

    @Test
    public void testLoadFileHandle () throws IOException {
        Config.load(new FileHandle("../config/junit-logger.cfg"));
    }

    @Test (expected = IllegalStateException.class)
    public void testLoadFileWithDir () throws IOException {
        Config.load(new FileHandle("./"));
    }

    @Test (expected = IllegalStateException.class)
    public void testGetNotExistentOption () {
        Config.get("test", "not-existent-key");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testLoadDirWithFile () throws IOException {
        Config.loadDir(new File("./config/game.cfg"));
    }

    @Test
    public void testLoadDir () throws IOException {
        Config.loadDir(new File("../config/"));
    }

}
