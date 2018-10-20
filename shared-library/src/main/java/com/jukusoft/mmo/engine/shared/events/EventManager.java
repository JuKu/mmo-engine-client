package com.jukusoft.mmo.engine.shared.events;

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
    protected AtomicQueue<EventData>[] logicEventQueue = new AtomicQueue[NUM_QUEUES];
    protected volatile int activeQueue = 0;

    //listeners
    protected IntMap<Array<EventListener>> listenerMap = new IntMap<>(50);
    protected IntMap<Array<EventListener>> logicListenerMap = new IntMap<>(50);

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

        //set active queue
        this.activeQueue = (this.activeQueue + 1) % NUM_QUEUES;
    }

    /**
    * add event to queue and process it on next gameloop tick
     *
     * @param event game event
    */
    public void queueEvent (EventData event) {
        //increment reference counter
        event.retain(NUM_QUEUES - 1);

        //add event to queues
        this.eventQueue[this.activeQueue].put(event);
        this.logicEventQueue[this.activeQueue].put(event);
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

        //trigger event with ui listeners
        this.handleEvent(event, false);

        //trigger event with logic listeners
        this.handleEvent(event, true);
    }

    /**
    * handle event and call all listeners
     *
     * @param event event to handle
    */
    protected void handleEvent (EventData event, boolean extraThread) {
        //get map for correct thread
        IntMap<Array<EventListener>> map = extraThread == false ? this.listenerMap : this.logicListenerMap;

        //find listener
        Array<EventListener> listeners = map.get(event.getEventType());

        if (listeners == null) {
            //no listener registered for this event
            return;
        }

        //call listeners
        for (EventListener listener : listeners) {
            listener.handleEvent(event);
        }

        event.release();

        if (event.getRefCount() <= 0) {
            //add event back to memory pool, so it can be reused
            Pools.free(event);
        }
    }

    public <T extends EventData> void addListener (int typeID, EventListener<T> listener, boolean logicThread) {
        Objects.requireNonNull(listener);

        Array<EventListener> list = this.listenerMap.get(typeID);

        if (list == null) {
            list = new Array<>(10);
            this.listenerMap.put(typeID, list);
        }

        list.add(listener);
    }

    public <T extends EventData> void removeListener (int typeID, EventListener<T> listener, boolean logicThread) {
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
