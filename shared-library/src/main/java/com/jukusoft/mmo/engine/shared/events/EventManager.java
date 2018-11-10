package com.jukusoft.mmo.engine.shared.events;

import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import org.mini2Dx.gdx.utils.Array;
import org.mini2Dx.gdx.utils.AtomicQueue;
import org.mini2Dx.gdx.utils.IntMap;

import java.util.Objects;

public class EventManager {

    //general options
    protected static final int NUM_QUEUES = 2;
    protected static final int QUEUE_CAPACITY = 50;

    //name of event manager
    protected final String name;

    //singleton instance
    protected static EventManager instance = null;

    //queues
    protected AtomicQueue<EventData>[] eventQueue = new AtomicQueue[NUM_QUEUES];
    protected volatile int activeQueue = 0;

    //listeners
    protected IntMap<Array<EventListener>> listenerMap = new IntMap<>(50);

    /**
    * default constructor
     *
     * @param name name of event manager (for debugging purposes)
     * @param setAsGlobal set as global, if true, instance is set as global singleton instance
    */
    public EventManager (String name, boolean setAsGlobal) {
        Objects.requireNonNull(name);
        this.name = name;

        if (setAsGlobal) {
            //set global singleton instance
            EventManager.instance = this;
        }

        //initialize queues
        for (int i = 0; i < NUM_QUEUES; i++) {
            this.eventQueue[i] = new AtomicQueue<>(QUEUE_CAPACITY);
        }
    }

    /**
    * process events and call listeners (without time limit)
    */
    public void update () {
        this.update(Integer.MAX_VALUE);
    }

    /**
     * process events and call listeners
     *
     * @param maxMillis max milliseconds, how long update() process can take
     */
    public void update (int maxMillis) {
        long startTime = System.currentTimeMillis();

        //first, copy queue
        AtomicQueue<EventData> queue = this.eventQueue[this.activeQueue];

        //set active queue (swap queue)
        this.activeQueue = (this.activeQueue + 1) % NUM_QUEUES;

        EventData event;
        long endTime = 0;
        long diffTime = 0;

        //process all events
        while ((event = queue.poll()) != null) {
            this.handleEvent(event);

            endTime = System.currentTimeMillis();
            diffTime = endTime - startTime;

            if (diffTime >= maxMillis) {
                int remainingEvents = 0;

                //don't process this elements yet, add them to active queue and process them in next tick
                while ((event = queue.poll()) != null) {
                    this.eventQueue[this.activeQueue].put(event);
                    remainingEvents++;
                }

                Log.d("Events", "[" + this.name + "] Couldn't process all events in one update() call, remaining events: " + remainingEvents);
                break;
            }
        }
    }

    /**
    * add event to queue and process it on next gameloop tick
     *
     * @param event game event
    */
    public void queueEvent (EventData event) {
        //add event to queue
        this.eventQueue[this.activeQueue].put(event);
    }

    /**
     * fire event immediately without using the queue
     * Important! Maybe this isn't thread safe!
     *
     * @param event game event
     */
    public void triggerEvent (EventData event) {
        if (!event.allowTrigger()) {
            throw new IllegalStateException("It isn't allowed to trigger this event type!");
        }

        //trigger event
        this.handleEvent(event);
    }

    /**
    * handle event and call all listeners
     *
     * @param event event to handle
    */
    protected void handleEvent (EventData event) {
        //find listener
        Array<EventListener> listeners = this.listenerMap.get(event.getEventType());

        if (listeners != null) {
            //call listeners
            for (EventListener listener : listeners) {
                listener.handleEvent(event);
            }
        }

        event.release();

        if (event.getRefCount() <= 0) {
            //add event back to memory pool, so it can be reused
            Pools.free(event);
        }
    }

    public <T extends EventData> void addListener (int typeID, EventListener<T> listener) {
        Objects.requireNonNull(listener);

        Array<EventListener> list = this.listenerMap.get(typeID);

        if (list == null) {
            list = new Array<>(10);
            this.listenerMap.put(typeID, list);
        }

        //check, if listener already exists in list
        if (list.contains(listener, false)) {
            throw new IllegalStateException("listener for typeID '" + typeID + "' and hash " + listener + " is already registered.");
        }

        list.add(listener);
    }

    public <T extends EventData> void removeListener (int typeID, EventListener<T> listener) {
        Objects.requireNonNull(listener);
        Array<EventListener> list = this.listenerMap.get(typeID);

        if (list != null) {
            list.removeValue(listener, false);
        }
    }

    /**
    * get global singleton instance of event manager
     *
     * @return singleton instance of event manager
    */
    public static final EventManager getInstance() {
        return EventManager.instance;
    }

}
