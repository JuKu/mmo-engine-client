package com.jukusoft.mmo.engine.applayer.script.lua;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;

public class LuaUtils {

    protected LuaUtils () {
        //
    }

    public static Table array (Object... elements) {
        Table table = new DefaultTable();

        for (int i = 0; i < elements.length; i++) {
            table.rawset(i + 1l, elements[i]);
        }

        return table;
    }

}
