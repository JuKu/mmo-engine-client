package com.jukusoft.mmo.engine.shared.events;

import com.jukusoft.mmo.engine.shared.memory.DummyEventDataObject;
import com.jukusoft.mmo.engine.shared.memory.DummyOtherEventDataObject;
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

    @Test (expected = IllegalArgumentException.class)
    public void testAddListenerWithIllegalThreadID () {
        Events.addListener(10, 1, eventData -> {
            //don't do anything here
        });
    }
    @Test (expected = IllegalArgumentException.class)
    public void testRemoveListenerWithIllegalThreadID () {
        Events.removeListener(10, 1, eventData -> {
            //don't do anything here
        });
    }

    @Test
    public void testTriggerEvent () {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger count1 = new AtomicInteger(0);

        EventListener listener = eventData -> {
            count.incrementAndGet();
        };

        EventListener listener1 = event -> {
            count1.incrementAndGet();
        };

        Events.addListener(Events.UI_THREAD, 2, listener);
        Events.addListener(Events.LOGIC_THREAD, 2, listener1);

        Events.triggerEvent(new DummyOtherEventDataObject());
        Events.triggerEvent(new DummyOtherEventDataObject());

        //check, if ui and logic thread listeners was called
        assertEquals(2, count.get());
        assertEquals(2, count1.get());

        //remove listeners again (cleanup)
        Events.removeListener(Events.UI_THREAD, 1, listener);
        Events.removeListener(Events.LOGIC_THREAD, 1, listener1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUpdateIllegalThreadId () {
        Events.update(10, 10);
    }

    @Test
    public void testUpdate () {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger count1 = new AtomicInteger(0);

        EventListener listener = eventData -> {
            count.incrementAndGet();
        };

        EventListener listener1 = event -> {
            count1.incrementAndGet();
        };

        Events.addListener(Events.UI_THREAD, 1, listener);
        Events.addListener(Events.LOGIC_THREAD, 1, listener1);

        Events.queueEvent(new DummyEventDataObject());
        Events.queueEvent(new DummyEventDataObject());

        Events.update(Events.UI_THREAD, 200);

        assertEquals(2, count.get());

        //check, that only UI thread was processed and listeners in other thread wasn't processed with this update() call
        assertEquals(0, count1.get());

        Events.removeListener(Events.UI_THREAD, 1, listener);
        Events.removeListener(Events.LOGIC_THREAD, 1, listener1);
    }

}
