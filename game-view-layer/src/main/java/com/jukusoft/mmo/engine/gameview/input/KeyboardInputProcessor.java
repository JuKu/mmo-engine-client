package com.jukusoft.mmo.engine.gameview.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.utils.FilePath;
import com.jukusoft.mmo.engine.gameview.input.binding.KeyBinding;
import com.jukusoft.mmo.engine.gameview.input.binding.KeyBindingAdapter;
import com.jukusoft.mmo.engine.shared.client.events.input.TakeScreenshotEvent;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

import java.io.File;

public class KeyboardInputProcessor extends InputAdapter {

    protected boolean enabled = true;
    protected InputMapper inputMapper = null;

    //vector with player movement direction (x, y) and speed (z)
    protected final Vector3 direction;

    protected final KeyBinding[] bindings;

    public KeyboardInputProcessor (Vector3 direction) {
        //find configuration file for keyboard bindings
        File keyboardBindingsFile = new File(FilePath.parse(Config.get("Input", "keyboardMappings")));

        //load keyboard bindings
        this.inputMapper = new InputMapper();

        //register templates first
        this.registerTemplates(this.inputMapper);

        //load mappings (key bindings)
        this.inputMapper.load(keyboardBindingsFile);

        //get bindings
        this.bindings = this.inputMapper.getBindings();

        //set reference
        this.direction = direction;
    }

    protected void registerTemplates (InputMapper inputMapper) {
        //fire event to
        inputMapper.registerTemplate("TAKE_SCREENSHOT", new KeyBindingAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                    return false;
                }

                //fire take screenshot event
                Events.queueEvent(Pools.get(TakeScreenshotEvent.class));

                return true;
            }
        });

        //move left
        inputMapper.registerTemplate("MOVE_LEFT", new KeyBindingAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                direction.x = -1;
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (direction.x < 0) {
                    direction.x = 0;
                }

                return true;
            }
        });

        //move right
        inputMapper.registerTemplate("MOVE_RIGHT", new KeyBindingAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                direction.x = 1;
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (direction.x > 0) {
                    direction.x = 0;
                }

                return true;
            }
        });

        //move top
        inputMapper.registerTemplate("MOVE_TOP", new KeyBindingAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                direction.y = 1;
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (direction.y > 0) {
                    direction.y = 0;
                }

                return true;
            }
        });

        //move bottom
        inputMapper.registerTemplate("MOVE_DOWN", new KeyBindingAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                direction.y = -1;
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (direction.y < 0) {
                    direction.y = 0;
                }

                return true;
            }
        });
    }

    /**
    * disable input processor, so no input events were handled, e.q. if game is paused
    */
    public void disable () {
        this.enabled = false;
    }

    /**
     * disable input processor, so input events were handled, e.q. if game isn't paused anymore
     */
    public void enable () {
        this.enabled = true;
    }

    @Override
    public boolean keyDown (int keycode) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        if (keycode > this.bindings.length) {
            //this key isn't supported
            return false;
        }

        //find key binding
        KeyBinding binding = this.bindings[keycode];

        if (binding != null) {
            binding.keyDown(keycode);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        if (keycode > this.bindings.length) {
            //this key isn't supported
            return false;
        }

        //find key binding
        KeyBinding binding = this.bindings[keycode];

        if (binding != null) {
            binding.keyUp(keycode);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        if (!this.enabled) {
            //game is paused
            return false;
        }

        //convert character to int, to find binding
        int keycode = Character.toLowerCase(character);
        keycode = keycode - 68;

        if (keycode > this.bindings.length) {
            //this key isn't supported
            return false;
        }

        //find key binding
        KeyBinding binding = this.bindings[keycode];

        if (binding != null) {
            binding.keyTyped(character);
            return true;
        }

        return false;
    }

}
