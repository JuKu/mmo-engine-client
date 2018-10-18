package com.jukusoft.mmo.engine.main;

import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystemManager;
import com.jukusoft.mmo.engine.gameview.HumanView;
import com.jukusoft.mmo.engine.gameview.InputLayer;
import com.jukusoft.mmo.engine.logic.GameLogicLayer;

public class BaseGameEngine extends BaseApp {

    @Override
    protected void addSubSystems(SubSystemManager manager) {
        //add input manager
        manager.addSubSystem(new InputLayer(), false);

        //add game-logic-layer
        manager.addSubSystem(new GameLogicLayer(), true);

        //add game-view-layer
        manager.addSubSystem(new HumanView(), false);
    }

}
