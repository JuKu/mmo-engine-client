package com.jukusoft.mmo.engine.shared.memory;

import com.jukusoft.mmo.engine.shared.events.EventData;
import com.jukusoft.mmo.engine.shared.logger.Log;
import org.mini2Dx.gdx.utils.Pool;

/**
 * Object pools to reuse memory to avoid memory allocation
 *
 * @see Pool
 */
public class Pools {

    protected Pools () {
        //
    }

    public static <T> T get (Class<T> cls) {
        T obj = org.mini2Dx.gdx.utils.Pools.get(cls).obtain();

        if (obj == null) {
            try {
                obj = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Log.w("Memory", "Exception while instantiating a new Pool object: ", e);
            }
        }

        if (obj instanceof EventData) {
            //initialize event
            ((EventData) obj).init();
        }

        return obj;
    }

    public static <T> void free (T obj) {
        Pool<T> pool = (Pool<T>) org.mini2Dx.gdx.utils.Pools.get(obj.getClass());
        pool.free(obj);
    }

}
