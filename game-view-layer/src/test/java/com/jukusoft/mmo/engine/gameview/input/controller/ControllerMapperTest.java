package com.jukusoft.mmo.engine.gameview.input.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector3;
import com.jukusoft.mmo.engine.shared.config.Config;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ControllerMapperTest {

    @BeforeClass
    public static void beforeClass () throws IOException {
        Config.load(new File("../data/config/controller.cfg"));
    }

    @AfterClass
    public static void afterClass () {
        Config.clear();
    }

    @Test
    public void testConstructor () throws IOException {
        new ControllerMapper(new Vector3(0, 0, 0), new File("../data/input/mappings/xbox_controller.ini"));
    }

    @Test (expected = FileNotFoundException.class)
    public void testConstructorWithNotExistentFile () throws IOException {
        new ControllerMapper(new Vector3(0, 0, 0), new File("not-existent-file.ini"));
    }

    @Test
    public void testAxisMoved () throws IOException {
        Vector3 vec = new Vector3(0, 0, 0);

        ControllerMapper mapper = new ControllerMapper(vec, new File("../data/input/mappings/xbox_controller.ini"));
        assertEquals(0, vec.x, 0.0001f);
        assertEquals(0, vec.y, 0.0001f);

        //move axis
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_HORIZONTAL_AXIS, 0.01f);
        assertEquals(0.01f, vec.x, 0.0001f);
        assertEquals(0, vec.y, 0.0001f);

        //move axis
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_HORIZONTAL_AXIS, -1f);
        assertEquals(-1, vec.x, 0.0001f);
        assertEquals(0, vec.y, 0.0001f);

        //move axis
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_HORIZONTAL_AXIS, 1f);
        assertEquals(1, vec.x, 0.0001f);
        assertEquals(0, vec.y, 0.0001f);

        //controller null value
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_HORIZONTAL_AXIS, 0.00001f);
        assertEquals(0, vec.x, 0.000000001f);
        assertEquals(0, vec.y, 0.000000001f);

        //move axis (horizontal)
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_VERTICAL_AXIS, 0.01f);
        assertEquals(0, vec.x, 0.0001f);
        assertEquals(0.01f, vec.y, 0.0001f);

        //move axis
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_VERTICAL_AXIS, -1f);
        assertEquals(0, vec.x, 0.0001f);
        assertEquals(-1, vec.y, 0.0001f);

        //move axis
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_VERTICAL_AXIS, 1f);
        assertEquals(0, vec.x, 0.0001f);
        assertEquals(1, vec.y, 0.0001f);

        //controller null value
        mapper.axisMoved(Mockito.mock(Controller.class), mapper.L_STICK_VERTICAL_AXIS, 0.00001f);
        assertEquals(0, vec.x, 0.000000001f);
        assertEquals(0, vec.y, 0.000000001f);
    }

    @Test
    public void testDispose () throws IOException {
        ControllerMapper mapper = new ControllerMapper(new Vector3(0, 0, 0), new File("../data/input/mappings/xbox_controller.ini"));
        mapper.dispose();
    }

}
