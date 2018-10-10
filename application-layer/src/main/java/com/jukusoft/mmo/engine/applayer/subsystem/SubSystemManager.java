package com.jukusoft.mmo.engine.applayer.subsystem;

public interface SubSystemManager {

    public void addSubSystem (SubSystem system, boolean useExtraThread);

    public void removeSubSystem (SubSystem system);

}
