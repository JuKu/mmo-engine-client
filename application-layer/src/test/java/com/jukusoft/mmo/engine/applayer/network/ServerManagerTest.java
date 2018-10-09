package com.jukusoft.mmo.engine.applayer.network;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ServerManagerTest {

    @Test
    public void testConstructor () {
        new ServerManager();
    }

    @Test
    public void testGetInstance () {
        ServerManager.getInstance().list.clear();
        assertEquals(0, ServerManager.getInstance().listServers().size());
    }

    @Test (expected = IllegalStateException.class)
    public void testLoadNotExistentFileFromConfig () throws IOException {
        ServerManager.getInstance().loadFromConfig(new File("not-existent-file.json"));
        assertEquals(true, ServerManager.getInstance().listServers().size() > 0);
    }

    @Test
    public void testLoadFromConfig () throws IOException {
        ServerManager.getInstance().loadFromConfig(new File("../data/config/servers.json"));
        assertEquals(true, ServerManager.getInstance().listServers().size() > 0);
    }

}
