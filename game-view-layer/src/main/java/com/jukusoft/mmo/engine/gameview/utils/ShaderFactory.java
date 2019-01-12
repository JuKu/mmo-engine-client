package com.jukusoft.mmo.engine.gameview.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.jukusoft.mmo.engine.shared.utils.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Justin on 09.02.2017.
 */
public class ShaderFactory {

    public static ShaderProgram createShader (final String vertexShaderPath, final String fragmentShaderPath)
        throws IOException {
        //read shader programs to string
        final String vertexShader = FileUtils.readFile(vertexShaderPath, StandardCharsets.ISO_8859_1);
        final String fragmentShader = FileUtils.readFile(fragmentShaderPath, StandardCharsets.ISO_8859_1);

        //create new shader
        ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        //check, if water shader was compiled
        if (!shaderProgram.isCompiled()) {
            //log debug information
            System.out.println("vertex shader:\n\n" + vertexShader);

            System.out.println("\n\nfragment shader:\n" + fragmentShader + "\n");

            throw new RuntimeException("Could not compile shader: " + shaderProgram.getLog());
        }

        return shaderProgram;
    }

}
