package com.jukusoft.mmo.engine.gameview.camera.manager;

import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;

/**
 * Created by Justin on 09.09.2017.
 */
public interface CameraManager {

    /**
    * get main camera
     *
     * @return main camera
    */
    public CameraHelper getMainCamera();

    /**
    * get ui camera
     *
     * @return ui camera
    */
    public CameraHelper getUICamera();

    /**
    * get custom camera, if camera doesnt exists, camera will be created.
    */
    public CameraHelper getCustomCamera(int index);

    /**
    * count number of custom cameras
     *
     * @return number of custom cameras
    */
    public int countCustomCameras();

    /**
    * get number of max custom cameras
     *
     * @return number of custom cameras
    */
    public int maxCustomCameras();

    /**
    * update cameras
     *
     * @param time game time
    */
    public void update(GameTime time);

}
