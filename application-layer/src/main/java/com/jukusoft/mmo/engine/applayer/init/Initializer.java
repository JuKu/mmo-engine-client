package com.jukusoft.mmo.engine.applayer.init;

import com.jukusoft.mmo.engine.applayer.base.BaseApp;
import com.jukusoft.mmo.engine.applayer.resources.ResourceChecker;

public class Initializer implements Runnable {

    protected final BaseApp app;

    public Initializer (BaseApp app) {
        this.app = app;
    }

    @Override
    public void run() {
        //check resources
        ResourceChecker.check();

        //TODO: add code here
    }

}
