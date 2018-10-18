package com.jukusoft.mmo.engine.shared.pool;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
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
        return org.mini2Dx.gdx.utils.Pools.get(cls).obtain();
    }

    public static <T> void free (T obj) {
        Pool<T> pool = (Pool<T>) org.mini2Dx.gdx.utils.Pools.get(obj.getClass());
        pool.free(obj);
    }

}
