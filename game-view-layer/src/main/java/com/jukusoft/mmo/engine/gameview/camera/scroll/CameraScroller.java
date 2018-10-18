package com.jukusoft.mmo.engine.gameview.camera.scroll;

import com.badlogic.gdx.Gdx;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.gameview.camera.CameraHelper;

/**
* responsible to scroll camera, if mouse is near border
*/
public class CameraScroller {

    protected static final float DISTANCE_BETWEEN_BORDER = 200;
    protected static float SCROLLING_SPEED = 500;

    //flag, if camera scrolling is enabled, will be disabled if camera is fixed or moved from story
    protected boolean enabled = true;

    //game time to get delta time
    protected GameTime time = GameTime.getInstance();

    public CameraScroller () {
        //
    }

    public void scroll (CameraHelper camera) {
        if (!enabled) {
            //we dont need to scroll on fixed or story-moved camera
            return;
        }

        int width = camera.getViewportWidth();
        int height = camera.getViewportHeight();

        //get mouse position first
        int mouseX = Gdx.input.getX();
        int mouseY = height - Gdx.input.getY();//bottom up

        float scrollSpeed = this.time.getDelta() * SCROLLING_SPEED;

        if (mouseX < DISTANCE_BETWEEN_BORDER) {
            //calculate scroll speed depends on distance to border
            float distance = DISTANCE_BETWEEN_BORDER - mouseX;
            float speedX = (distance / DISTANCE_BETWEEN_BORDER) * scrollSpeed;

            camera.translate(-speedX, 0, 0);
        } else if (mouseX > (width - DISTANCE_BETWEEN_BORDER)) {
            float distance = (DISTANCE_BETWEEN_BORDER - (width - mouseX));
            float speedX = (distance / DISTANCE_BETWEEN_BORDER) * scrollSpeed;

            camera.translate(speedX, 0, 0);
        }

        if (mouseY < DISTANCE_BETWEEN_BORDER) {
            //calculate scroll speed depends on distance to border
            float distance = DISTANCE_BETWEEN_BORDER - mouseY;
            float speedY = (distance / DISTANCE_BETWEEN_BORDER) * scrollSpeed;

            camera.translate(0, -speedY, 0);
        } else if (mouseY > (height - DISTANCE_BETWEEN_BORDER)) {
            float distance = (DISTANCE_BETWEEN_BORDER - (height - mouseY));
            float speedY = (distance / DISTANCE_BETWEEN_BORDER) * scrollSpeed;

            camera.translate(0, speedY, 0);
        }
    }

}
