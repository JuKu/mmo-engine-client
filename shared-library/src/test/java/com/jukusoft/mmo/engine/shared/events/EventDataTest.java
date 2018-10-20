package com.jukusoft.mmo.engine.shared.events;

import com.jukusoft.mmo.engine.shared.memory.DummyEventDataObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventDataTest {

    @Test
    public void testAllowTriggerDefaultValue () {
        assertEquals(false, new DummyEventDataObject().allowTrigger());
    }

    @Test
    public void testGetTimestamp () {
        long startTime = System.currentTimeMillis();

        DummyEventDataObject event = new DummyEventDataObject();
        event.init();

        long endTime = System.currentTimeMillis();

        //check timestamp range
        assertEquals(true, event.getTimestamp() >= startTime);
        assertEquals(true, event.getTimestamp() <= endTime);
    }

    @Test
    public void testGetRefCount () {
        DummyEventDataObject event = new DummyEventDataObject();

        //after creation, reference count should be 1
        assertEquals(1, event.getRefCount());

        event.retain();
        assertEquals(2, event.getRefCount());

        event.release();
        assertEquals(1, event.getRefCount());

        event.release();
        assertEquals(0, event.getRefCount());

        event.retain(2);
        assertEquals(2, event.getRefCount());
    }

}
