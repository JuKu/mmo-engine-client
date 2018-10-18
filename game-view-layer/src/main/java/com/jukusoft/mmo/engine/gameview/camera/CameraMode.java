package com.jukusoft.mmo.engine.gameview.camera;

/**
 * Created by Justin on 09.09.2017.
 */
public enum CameraMode {

    //player is every time in middle of screen
    DIRECT_CAMERA,

    //smooth camera
    SMOOTH_CAMERA,

    //camera is fixed and isnt moving
    FIXED_CAMERA,

    //player can move camera with mouse position
    MOUSE_CAMERA,

    //player can scroll with mouse (on borders), but there is an maximum distance between player can camera
    SCROLL_CAMERA_WITH_MAX_DISTANCE

}
