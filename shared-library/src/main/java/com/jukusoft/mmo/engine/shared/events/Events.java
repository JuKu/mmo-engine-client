package com.jukusoft.mmo.engine.shared.events;

public class Events {

    protected static final int NUM_THREADS = 2;
    protected static EventManager[] managers;

    //thread IDs
    public static final int UI_THREAD = 0;
    public static final int LOGIC_THREAD = 1;

    protected Events () {
        //
    }

    public static void init () {
        managers = new EventManager[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            managers[i] = new EventManager("event manager #" + i + ".", false);
        }
    }

    public static void queueEvent (EventData event) {
        //increment reference counter
        event.retain(NUM_THREADS - 1);

        //add event to queues
        for (int i = 0; i < NUM_THREADS; i++) {
            managers[i].queueEvent(event);
        }
    }

    public static void triggerEvent (EventData event) {
        //increment reference counter
        event.retain(NUM_THREADS - 1);

        //call all event managers
        for (int i = 0; i < NUM_THREADS; i++) {
            managers[i].triggerEvent(event);
        }
    }

    public static void update (int threadID, int maxMillis) {
        if (threadID >= NUM_THREADS) {
            throw new IllegalArgumentException("threadID cannot >= number of threads, but threadID: " + threadID + " >= " + NUM_THREADS);
        }

        //process events
        managers[threadID].update(maxMillis);
    }

}
