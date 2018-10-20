package com.jukusoft.mmo.engine.shared.events;

import com.jukusoft.mmo.engine.shared.memory.DummyEventDataObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class EventsTest {

    @BeforeClass
    public static void beforeClass () {
        Events.init();
    }

    @AfterClass
    public static void afterClass () {
        Events.managers = null;
    }

    @Test
    public void testConstructor () {
        new Events();
    }

    @Test
    public void testInit () {
        Events.managers = null;
        Events.init();
    }

    @Test (expected = NullPointerException.class)
    public void addNullListener () {
        Events.addListener(1, 1, null);
    }

    @Test
    public void testUpdate () {
        AtomicInteger count = new AtomicInteger(0);

        EventListener listener = eventData -> {
            count.incrementAndGet();
        };

        Events.addListener(Events.UI_THREAD, 1, listener);

        Events.queueEvent(new DummyEventDataObject());
        Events.queueEvent(new DummyEventDataObject());

        Events.update(Events.UI_THREAD, 200);

        assertEquals(2, count.get());

        Events.removeListener(Events.UI_THREAD, 1, listener);
    }

}
