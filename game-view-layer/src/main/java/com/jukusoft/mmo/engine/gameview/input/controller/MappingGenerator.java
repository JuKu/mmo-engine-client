package com.jukusoft.mmo.engine.gameview.input.controller;

import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
import com.jukusoft.mmo.engine.applayer.utils.PlatformUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MappingGenerator {

    protected MappingGenerator () {
        //
    }

    public static void generateDefaultMapping (File file, String controllerName) throws IOException {
        if (file.exists()) {
            throw new IllegalStateException("file already exists: " + file.getAbsolutePath());
        }

        Log.i("Controller", "generate new mapping for controller: " + controllerName);

        StringBuilder sb = new StringBuilder();

        for (PlatformUtils.OS_TYPE type : PlatformUtils.OS_TYPE.values()) {
            //to lower case, except first character
            String osName = type.name().replace("_OS", "");
            osName = osName.substring(0, 1).toUpperCase() + osName.substring(1).toLowerCase();

            String sectionName = "Mapping" + osName;

            //add section
            sb.append("[" + sectionName + "]" + System.lineSeparator());

            //buttons
            sb.append("A=0" + System.lineSeparator());
            sb.append("B=1" + System.lineSeparator());
            sb.append("X=2" + System.lineSeparator());
            sb.append("Y=3" + System.lineSeparator());
            sb.append("BACK=6" + System.lineSeparator());
            sb.append("START=7" + System.lineSeparator());

            //povMoved
            sb.append("DPAD_UP=0" + System.lineSeparator());
            sb.append("DPAD_DOWN=0" + System.lineSeparator());
            sb.append("DPAD_LEFT=0" + System.lineSeparator());
            sb.append("DPAD_RIGHT=0" + System.lineSeparator());

            //bumper and trigger as buttons
            sb.append("L_BUMPER=4" + System.lineSeparator());
            sb.append("R_BUMPER=5" + System.lineSeparator());

            //axis moved
            sb.append("L_TRIGGER=4" + System.lineSeparator());
            sb.append("R_TRIGGER=5" + System.lineSeparator());

            //sticks (axisMoved)
            sb.append("L_STICK_VERTICAL_AXIS=0" + System.lineSeparator());
            sb.append("L_STICK_HORIZONTAL_AXIS=1" + System.lineSeparator());
            sb.append("R_STICK_VERTICAL_AXIS=2" + System.lineSeparator());
            sb.append("R_STICK_HORIZONTAL_AXIS=3" + System.lineSeparator());

            //blank line
            sb.append(System.lineSeparator());
        }

        FileUtils.writeFile(file.getAbsolutePath(), sb.toString(), StandardCharsets.UTF_8);
    }

}
