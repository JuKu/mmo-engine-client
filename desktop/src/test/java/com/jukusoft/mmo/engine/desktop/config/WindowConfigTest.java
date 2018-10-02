package com.jukusoft.mmo.engine.desktop.config;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.junit.Test;

import java.io.IOException;

public class WindowConfigTest {

    @Test
    public void testConstructor () throws IOException {
        new WindowConfig("../config/junit-window.cfg");
    }

    @Test (expected = NullPointerException.class)
    public void testNullConstructor () throws IOException {
        new WindowConfig(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyConstructor () throws IOException {
        new WindowConfig("");
    }

    @Test (expected = IOException.class)
    public void testNotExistentFileConstructor () throws IOException {
        new WindowConfig("not-existent-file.cfg");
    }

    @Test
    public void testGetInt () throws IOException {
        new WindowConfig("../config/junit-window.cfg").getInt("width");
    }

    @Test
    public void testGetIntOrDefault () throws IOException {
        new WindowConfig("../config/junit-window.cfg").getIntOrDefault("width", 1280);
    }

    @Test (expected = NumberFormatException.class)
    public void testGetBoolean () throws IOException {
        new WindowConfig("../config/junit-window.cfg").getInt("resizeable");
    }

    @Test
    public void testGetBoolean1 () throws IOException {
        new WindowConfig("../config/junit-window.cfg").getBoolean("resizeable");
    }

    @Test
    public void testFillConfig () throws IOException {
        WindowConfig windowConfig = new WindowConfig("../config/junit-window.cfg");

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        windowConfig.fillConfig(config);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testFillConfig2 () throws IOException {
        WindowConfig windowConfig = new WindowConfig("../config/junit-window2.cfg");

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        windowConfig.fillConfig(config);
    }

}
