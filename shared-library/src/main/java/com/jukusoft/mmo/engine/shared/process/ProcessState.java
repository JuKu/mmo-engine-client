package com.jukusoft.mmo.engine.shared.process;

public enum ProcessState {

    UNINITIALIZED,

    /**
    * initialized but paused
    */
    PAUSED,

    RUNNING,

    SUCCEEDED,

    FAILED,

    ABORDED,

    REMOVED

}
