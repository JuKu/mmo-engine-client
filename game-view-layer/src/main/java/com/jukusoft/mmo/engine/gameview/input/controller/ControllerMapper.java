package com.jukusoft.mmo.engine.gameview.input.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.PlatformUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ControllerMapper extends ControllerAdapter {

    protected static final String LOG_TAG = "ControllerMapper";
    protected static final String CONTROLLER_SECTION = "Controller";

    /**
    * mappings
    */
    protected final int BUTTON_A;
    protected final int BUTTON_B;
    protected final int BUTTON_X;
    protected final int BUTTON_Y;
    protected final int BUTTON_BACK;
    protected final int BUTTON_START;

    protected final int DPAD_UP;
    protected final int DPAD_DOWN;
    protected final int DPAD_LEFT;
    protected final int DPAD_RIGHT;

    protected final int L_BUMPER;
    protected final int R_BUMPER;
    protected final int L_TRIGGER;
    protected final int R_TRIGGER;

    protected final int L_STICK_VERTICAL_AXIS;
    protected final int L_STICK_HORIZONTAL_AXIS;
    protected final int R_STICK_VERTICAL_AXIS;
    protected final int R_STICK_HORIZONTAL_AXIS;

    /**
    * options
    */
    protected final float invertLStickVerticalAxis;
    protected final float invertLStickHorizontalAxis;
    protected final float invertRStickVerticalAxis;
    protected final float invertRStickHorizontalAxis;

    protected final Vector3 playerMoveDirection;

    /**
    * default constructor
    */
    public ControllerMapper (Vector3 playerMoveDirection, File file) throws IOException {
        this.playerMoveDirection = playerMoveDirection;
        Profile.Section section = this.getSection(file);

        Log.d(LOG_TAG, "set bindings...");

        //do initialize mapping in constructor, because so we can use final variables, which are performanter
        this.BUTTON_A = getInt("A", section);
        this.BUTTON_B = getInt("B", section);
        this.BUTTON_X = getInt("X", section);
        this.BUTTON_Y = getInt("Y", section);
        this.BUTTON_BACK = getInt("BACK", section);
        this.BUTTON_START = getInt("START", section);

        //DPAD
        this.DPAD_UP = getInt("DPAD_UP", section);
        this.DPAD_DOWN = getInt("DPAD_DOWN", section);
        this.DPAD_LEFT = getInt("DPAD_LEFT", section);
        this.DPAD_RIGHT = getInt("DPAD_RIGHT", section);

        //bumper and trigger
        this.L_BUMPER = getInt("L_BUMPER", section);
        this.R_BUMPER = getInt("R_BUMPER", section);
        this.L_TRIGGER = getInt("L_TRIGGER", section);
        this.R_TRIGGER = getInt("R_TRIGGER", section);

        //sticks
        this.L_STICK_VERTICAL_AXIS = getInt("L_STICK_VERTICAL_AXIS", section);
        this.L_STICK_HORIZONTAL_AXIS = getInt("L_STICK_HORIZONTAL_AXIS", section);
        this.R_STICK_VERTICAL_AXIS = getInt("R_STICK_VERTICAL_AXIS", section);
        this.R_STICK_HORIZONTAL_AXIS = getInt("R_STICK_HORIZONTAL_AXIS", section);

        //get options
        this.invertLStickVerticalAxis = Config.getBool(CONTROLLER_SECTION, "invertLStickVerticalAxis") ? -1 : 1;
        this.invertLStickHorizontalAxis = Config.getBool(CONTROLLER_SECTION, "invertLStickHorizontalAxis") ? -1 : 1;
        this.invertRStickVerticalAxis = Config.getBool(CONTROLLER_SECTION, "invertRStickVerticalAxis") ? -1 : 1;
        this.invertRStickHorizontalAxis = Config.getBool(CONTROLLER_SECTION, "invertRStickHorizontalAxis") ? -1 : 1;
    }

    /**
    * load mappings
    */
    protected Profile.Section getSection (File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("controller mapping file doesn't exists: " + file.getAbsolutePath());
        }

        Ini ini = new Ini(file);

        //get os to find correct section
        PlatformUtils.OS_TYPE type = PlatformUtils.getType();

        //to lower case, except first character
        String osName = type.name().replace("_OS", "");
        osName = osName.substring(0, 1).toUpperCase() + osName.substring(1).toLowerCase();

        String sectionName = "Mapping" + osName;

        Log.v(LOG_TAG, "load section: " + sectionName);
        return ini.get(sectionName);
    }

    private int getInt (String key, Profile.Section section) {
        return Integer.parseInt(section.get(key));
    }

    @Override
    public boolean axisMoved (Controller controller, int axisIndex, float value) {
        //correct value, because controller doesn't return 0
        if (Math.abs(MathUtils.roundPositive(value)) == 0) {
            value = 0;
        }

        if (axisIndex == L_STICK_VERTICAL_AXIS) {
            playerMoveDirection.x = value * this.invertLStickVerticalAxis;
        } else if (axisIndex == L_STICK_HORIZONTAL_AXIS) {
            playerMoveDirection.y = value * this.invertLStickHorizontalAxis;
        }

        return false;
    }

    /**
    * cleanUp memory
    */
    public void dispose () {
        //
    }

}
