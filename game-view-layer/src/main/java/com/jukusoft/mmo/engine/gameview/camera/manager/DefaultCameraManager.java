package com.jukusoft.mmo.engine.gameview.camera.manager;

import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;
import com.jukusoft.mmo.engine.gameview.camera.CameraMode;

/**
 * Created by Justin on 09.09.2017.
 */
public class DefaultCameraManager implements CameraManager {

    //main game camera
    protected CameraHelper mainCamera = null;

    //ui camera
    protected CameraHelper uiCamera = null;

    //custom cameras
    protected static final int MAX_CUSTOM_CAMERAS = 10;
    protected CameraHelper[] customCameras = new CameraHelper[MAX_CUSTOM_CAMERAS];

    public DefaultCameraManager (int viewportWidth, int viewportHeight) {
        //createDefaultConfig new main game camera
        this.mainCamera = new CameraHelper(viewportWidth, viewportHeight);

        //createDefaultConfig new ui camera
        this.uiCamera = new CameraHelper(viewportWidth, viewportHeight);

        //set fixed camera mode for UI camera, because UI camera isnt moving
        this.uiCamera.setMode(CameraMode.FIXED_CAMERA);
    }

    @Override
    public CameraHelper getMainCamera() {
        return this.mainCamera;
    }

    @Override
    public CameraHelper getUICamera() {
        return this.uiCamera;
    }

    @Override
    public CameraHelper getCustomCamera(int index) {
        if (this.customCameras[index] == null) {
            //createDefaultConfig new custom camera and set viewport of current window dimension
            this.customCameras[index] = new CameraHelper(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        return this.customCameras[index];
    }

    @Override
    public int countCustomCameras() {
        int cameras = 0;

        //iterate through all custom camera slots and check, if custom camera exists
        for (int i = 0; i < MAX_CUSTOM_CAMERAS; i++) {
            //check, if custom camera exists
            if (this.customCameras[i] != null) {
                //increase counter
                cameras++;
            }
        }

        return cameras;
    }

    @Override
    public int maxCustomCameras() {
        return MAX_CUSTOM_CAMERAS;
    }

    @Override
    public void update(GameTime time) {
        //update main camera
        this.mainCamera.update(time);

        //update ui camera
        this.uiCamera.update(time);

        //iterate through all custom camera slots
        for (int i = 0; i < MAX_CUSTOM_CAMERAS; i++) {
            //check, if custom camera exists
            if (this.customCameras[i] != null) {
                //update custom camera
                this.customCameras[i].update(time);
            }
        }
    }

}
