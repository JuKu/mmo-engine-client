package com.jukusoft.mmo.engine.shared.events;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EventManagerTest {

    @Test
    public void testConstructor () {
        EventManager.instance = null;

        new EventManager("test", false);
        assertNull(EventManager.instance);
        assertNull(EventManager.getInstance());
    }

    @Test
    public void testSetGlobalConstructor () {
        EventManager.instance = null;

        new EventManager("test", true);
        assertNotNull(EventManager.instance);
        assertNotNull(EventManager.getInstance());

        //reset event manager
        EventManager.instance = null;
    }

    @Test
    public void testGetInstance () {
        EventManager manager = new EventManager("test", true);

        EventManager manager1 = EventManager.getInstance();
        EventManager manager2 = EventManager.getInstance();
        assertEquals(manager, manager1);
        assertEquals(manager1, manager2);

        //reset event manager
        EventManager.instance = null;
    }

    @Test (expected = NullPointerException.class)
    public void testAddNullListener () {
        EventManager manager = new EventManager("test", false);
        manager.addListener(1, null);
    }

    @Test
    public void testAddListener () {
        EventManager manager = new EventManager("test", false);
        manager.addListener(1, eventData -> {
            //don't do anything here
        });
    }

    public void removeListener () {
        //
    }

}
