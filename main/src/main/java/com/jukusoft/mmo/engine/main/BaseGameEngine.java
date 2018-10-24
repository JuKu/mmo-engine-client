package com.jukusoft.mmo.engine.main;

import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.subsystem.EventProcessor;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystemManager;
import com.jukusoft.mmo.engine.applayer.version.Version;
import com.jukusoft.mmo.engine.gameview.HumanView;
import com.jukusoft.mmo.engine.gameview.InputLayer;
import com.jukusoft.mmo.engine.logic.GameLogicLayer;
import com.jukusoft.mmo.engine.shared.events.Events;

public class BaseGameEngine extends BaseApp {

    public BaseGameEngine(Version version) {
        super(version);
    }

    @Override
    protected void addSubSystems(SubSystemManager manager) {
        //add input manager
        manager.addSubSystem(new InputLayer(), false);

        //add event processors for UI and logic events
        manager.addSubSystem(new EventProcessor(Events.UI_THREAD, 10), false);
        manager.addSubSystem(new EventProcessor(Events.LOGIC_THREAD, 10), true);

        //add game-logic-layer
        manager.addSubSystem(new GameLogicLayer(), true);

        //add game-view-layer
        manager.addSubSystem(new HumanView(), false);
    }

}
