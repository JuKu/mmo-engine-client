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

    @Test (expected = NullPointerException.class)
    public void testRemoveNullListener () {
        EventManager manager = new EventManager("test", false);
        manager.removeListener(1, null);
    }

    @Test
    public void testRemoveNotExistentListener () {
        EventManager manager = new EventManager("test", false);
        manager.removeListener(1, (event) -> {
            //don't do anything here
        });
    }

    @Test
    public void testAddAndRemoveListener () {
        EventManager manager = new EventManager("test", false);

        //there should not listener array exists yet
        assertNull(manager.listenerMap.get(2));

        //register listener
        EventListener listener = (eventData -> {
            //don't do anything here
        });
        manager.addListener(2, listener);

        //check, if listener is registered correctly
        assertNotNull(manager.listenerMap.get(2));
        assertEquals(1, manager.listenerMap.get(2).size);
        assertEquals(listener, manager.listenerMap.get(2).get(0));

        //remove other listener typeID
        manager.removeListener(3, listener);

        //check again, if listener is registered correctly
        assertNotNull(manager.listenerMap.get(2));
        assertEquals(1, manager.listenerMap.get(2).size);
        assertEquals(listener, manager.listenerMap.get(2).get(0));

        //remove other listener typeID
        manager.removeListener(2, listener);

        //check, if listener was removed
        assertNotNull(manager.listenerMap.get(2));
        assertEquals(0, manager.listenerMap.get(2).size);
    }

}
