package com.jukusoft.mmo.engine.gameview.camera;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Created by Justin on 10.09.2017.
 */
public class CameraHelperTest {

    //test application
    private static Application application;

    //initialize application with the headless backend before running any tests
    @BeforeClass
    public static void init() {
        System.out.println("init app.");

        application = new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {}
            @Override
            public void resize(int width, int height) {}
            @Override
            public void render() {}
            @Override
            public void pause() {}
            @Override
            public void resume() {}
            @Override
            public void dispose() {}
        });

        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    //clean up application after executing tests
    @AfterClass
    public static void cleanUp() {
        // Exit the application first
        application.exit();
        application = null;
    }

    @Test
    public void testConstructor () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //test viewvport dimension
        assertEquals(width, camera.getViewportWidth());
        assertEquals(height, camera.getViewportHeight());

        //test position
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);

        //test zoom
        assertEquals(1, camera.getZoom(), 0);

        //test mode
        assertEquals(CameraMode.DIRECT_CAMERA, camera.getMode());

        //test target position
        assertEquals(0, camera.getTargetX(), 0);
        assertEquals(0, camera.getTargetY(), 0);

        //test offset
        assertEquals(width / 2, camera.getOffsetX(), 0);
        assertEquals(height / 2, camera.getOffsetY(), 0);

        //test original camera position
        assertEquals(width / 2, camera.getOriginalCamera().position.x, 0);
        assertEquals(height / 2, camera.getOriginalCamera().position.y, 0);
    }

    @Test
    public void testBounds () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //test position and zoom
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);
        assertEquals(1, camera.getZoom(), 0);

        //test bounds
        assertEquals(-Float.MAX_VALUE, camera.minX, 0);
        assertEquals(Float.MAX_VALUE, camera.maxX, 0);
        assertEquals(-Float.MAX_VALUE, camera.minY, 0);
        assertEquals(Float.MAX_VALUE, camera.maxY, 0);

        //set bounds
        camera.setBounds(-10, -20, 30, 40);

        //test bounds
        assertEquals(-10, camera.minX, 0);
        assertEquals(-20, camera.maxX, 0);
        assertEquals(30, camera.minY, 0);
        assertEquals(40, camera.maxY, 0);

        //reset bounds
        camera.resetBounds();

        //test bounds
        assertEquals(-Float.MAX_VALUE, camera.minX, 0);
        assertEquals(Float.MAX_VALUE, camera.maxX, 0);
        assertEquals(-Float.MAX_VALUE, camera.minY, 0);
        assertEquals(Float.MAX_VALUE, camera.maxY, 0);

        //set bounds
        camera.setBounds(0, 1000, 0, 2000);

        //test bounds
        assertEquals(0, camera.minX, 0);
        assertEquals(1000, camera.maxX, 0);
        assertEquals(0, camera.minY, 0);
        assertEquals(2000, camera.maxY, 0);

        //test, if camera can scroll on x axis
        assertEquals(true, camera.canScrollX(0));
        assertEquals(false, camera.canScrollX(-10));
        assertEquals(false, camera.canScrollX(-100));
        assertEquals(true, camera.canScrollX(100));
        assertEquals(true, camera.canScrollX(200));
        assertEquals(false, camera.canScrollX(201));

        //test, if camera can scroll on y axis
        assertEquals(true, camera.canScrollY(0));
        assertEquals(false, camera.canScrollY(-10));
        assertEquals(false, camera.canScrollY(-100));
        assertEquals(true, camera.canScrollY(1001));
        assertEquals(true, camera.canScrollY(1400));
        assertEquals(false, camera.canScrollY(2001));

        //set target position
        camera.setTargetPos(-200, -200);

        //update camera
        camera.update(GameTime.getInstance());

        //check camera position (if position is in bounds)
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);

        //set new target position
        camera.setTargetPos(2000, 2000);

        //update camera
        camera.update(GameTime.getInstance());

        //check camera position (if position is in bounds)
        assertEquals(200, camera.getX(), 0);
        assertEquals(1400, camera.getY(), 0);
    }

    @Test
    public void testUpdate () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //set camera mode
        camera.setMode(CameraMode.DIRECT_CAMERA);

        //test position
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);

        //set target position
        camera.setTargetPos(0, 0);

        //update camera
        camera.update(GameTime.getInstance());

        //check camera position
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);

        //set new target position
        camera.setTargetPos(100, 200);

        //update camera
        camera.update(GameTime.getInstance());

        //check camera position
        assertEquals(100, camera.getX(), 0);
        assertEquals(200, camera.getY(), 0);
    }

    @Test
    public void testResize () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        int newWidth = 1280;
        int newHeight = 912;

        //resize camera
        camera.resize(newWidth, newHeight);

        //test dimension
        assertEquals(newWidth, camera.getViewportWidth());
        assertEquals(newHeight, camera.getViewportHeight());

        //update camera
        camera.update(GameTime.getInstance());

        //test dimension
        assertEquals(newWidth, camera.getViewportWidth());
        assertEquals(newHeight, camera.getViewportHeight());
    }

    @Test
    public void testSetTargetPosition () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //test position
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);

        //set target position
        camera.setTargetPos(10, 20);

        //update camera
        camera.update(GameTime.getInstance());

        //test position
        assertEquals(10, camera.getX(), 0);
        assertEquals(20, camera.getY(), 0);

        //test original camera position
        assertEquals((width / 2 + 10), camera.getOriginalCamera().position.x, 0);
        assertEquals((height / 2 + 20), camera.getOriginalCamera().position.y, 0);
    }

    @Test
    public void testSetTargetMiddlePosition () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //set target middle position
        camera.setTargetMiddlePos(0, 0);

        assertEquals(-400, camera.getTargetX(), 0);
        assertEquals(-300, camera.getTargetY(), 0);
    }

    @Test
    public void testFixedCamera () {
        int width = 800;
        int height = 600;

        //create new camera
        CameraHelper camera = new CameraHelper(width, height);

        //set fixed camera mode
        camera.setMode(CameraMode.FIXED_CAMERA);

        //set target position
        camera.setTargetPos(100, 100);

        //update camera
        camera.update(GameTime.getInstance());

        //check new camera position
        assertEquals(0, camera.getX(), 0);
        assertEquals(0, camera.getY(), 0);
    }

}
