package com.jukusoft.mmo.engine.gameview.camera;

import com.jukusoft.mmo.engine.applayer.time.GameTime;

/**
 * Created by Justin on 11.02.2017.
 */
public interface CameraModification {

    public void onUpdate(GameTime time, TempCameraParams position, ModificationFinishedListener listener);

    public void dispose();

}
