package com.jukusoft.mmo.engine.shared.process;

public abstract class Process {

    private ProcessState state = ProcessState.UNINITIALIZED;

    protected void onInit () {
        //
    }

    protected abstract void onUpdate (float delta);

    protected void onSuccess () {
        //
    }

    protected void onFail () {
        //
    }

    protected void onAbord () {
        //
    }

    protected final void fail () {
        this.state = ProcessState.FAILED;
        this.onFail();
    }

    protected final void succeed () {
        this.state = ProcessState.SUCCEEDED;
        this.onSuccess();
    }

    protected final void abord () {
        this.state = ProcessState.ABORDED;
        this.onAbord();
    }

    public boolean isInitialized () {
        return this.state != ProcessState.UNINITIALIZED;
    }

    public ProcessState getState () {
        return this.state;
    }

}
