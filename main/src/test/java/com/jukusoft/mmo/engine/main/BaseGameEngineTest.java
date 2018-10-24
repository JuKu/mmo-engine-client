package com.jukusoft.mmo.engine.main;

import com.jukusoft.mmo.engine.applayer.subsystem.SubSystemManager;
import org.junit.Test;
import org.mockito.Mockito;

public class BaseGameEngineTest {

    @Test
    public void testConstructor () {
        new BaseGameEngine(null);
    }

    @Test
    public void testAddSubSystems () {
        BaseGameEngine engine = new BaseGameEngine(null);
        engine.addSubSystems(Mockito.mock(SubSystemManager.class));
    }

}
