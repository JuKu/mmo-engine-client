package com.jukusoft.mmo.engine.gameview.screens.impl;

import com.jukusoft.mmo.engine.shared.utils.Platform;
import com.jukusoft.mmo.engine.gameview.GameUnitTest;
import com.jukusoft.mmo.engine.gameview.screens.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultScreenManagerTest extends GameUnitTest {

    @Test
    public void testConstructor () {
        new DefaultScreenManager();
    }

    @Test (expected = NullPointerException.class)
    public void testAddNullScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen(null, new DummyScreen());
    }

    @Test (expected = NullPointerException.class)
    public void testAddNullScreen1 () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddEmptyScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("", new DummyScreen());
    }

    @Test (expected = IllegalStateException.class)
    public void testAddExistentScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.addScreen("dummy_screen", new DummyScreen());
    }

    @Test
    public void testAddScreen () {
        DefaultScreenManager manager = this.createDefaultScreenManager();

        assertEquals(0, manager.screens.size);
        assertEquals(0, manager.cachedScreenList.size());
        assertEquals(0, manager.activeScreens.size());

        manager.addScreen("dummy_screen", new DummyScreen());

        assertEquals(1, manager.screens.size);
        assertEquals(1, manager.cachedScreenList.size());
        assertEquals(0, manager.activeScreens.size());

        assertEquals(1, manager.listScreens().size());
        assertEquals(0, manager.listActiveScreens().size());

    }

    @Test
    public void testAddScreen1 () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new OtherDummyScreen());
    }

    @Test (expected = NullPointerException.class)
    public void testRemoveNullScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.removeScreen(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveEmptyScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.removeScreen("");
    }

    @Test
    public void testRemoveScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();

        manager.removeScreen("not-existent-screen");

        assertNull(manager.getScreenByName("not-existent-screen"));

        manager.addScreen("dummy_screen", new DummyScreen());

        assertNotNull(manager.getScreenByName("dummy_screen"));

        manager.removeScreen("dummy_screen");

        assertEquals(true, manager.getScreenByName("dummy_screen") == null);
    }

    @Test (expected = NullPointerException.class)
    public void testPushNullScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.push(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPushEmptyScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.push("");
    }

    @Test (expected = ScreenNotFoundException.class)
    public void testPushNotExistentScreen () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.push("not-existent-screen");
    }

    @Test
    public void testPush () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");
    }

    @Test
    public void testPop () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");

        assertNotNull(manager.pop());

        //pop again
        assertNull(manager.pop());

        assertEquals(1, manager.listScreens().size());
        assertEquals(0, manager.listActiveScreens().size());
    }

    @Test
    public void testPop1 () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");

        assertNotNull(manager.pop());

        Platform.executeQueue();

        //pop again
        assertNull(manager.pop());

        assertEquals(1, manager.listScreens().size());
        assertEquals(0, manager.listActiveScreens().size());
    }

    @Test
    public void testLeaveAllAndEnter () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.leaveAllAndEnter("dummy_screen");
    }

    @Test
    public void testLeaveAllAndEnter1 () {
        IScreen screen1 = new DummyScreen();
        IScreen screen2 = new DummyScreen();

        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", screen1);
        manager.addScreen("screen2", screen2);

        assertEquals(false, manager.listActiveScreens().contains(screen1));
        assertEquals(false, manager.listActiveScreens().contains(screen2));

        manager.push("screen2");

        assertEquals(false, manager.listActiveScreens().contains(screen1));
        assertEquals(true, manager.listActiveScreens().contains(screen2));

        manager.leaveAllAndEnter("dummy_screen");

        assertEquals(true, manager.listActiveScreens().contains(screen1));
        assertEquals(false, manager.listActiveScreens().contains(screen2));
    }

    @Test
    public void testResize () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new IScreen() {
            @Override
            public void onStart(ScreenManager<IScreen> screenManager) {
                //
            }

            @Override
            public void onStop() {
                //
            }

            @Override
            public void onResume() {
                //
            }

            @Override
            public void onPause() {
                //
            }

            @Override
            public void onResize(int width, int height) {
                //
            }

            @Override
            public void update(ScreenManager<IScreen> screenManager) {
                //
            }

            @Override
            public void draw() {
                //
            }
        });
        manager.push("dummy_screen");

        manager.resize(1280, 720);
    }

    @Test
    public void testUpdate () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");

        manager.update();
    }

    @Test
    public void testDraw () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");

        manager.draw();
    }

    @Test
    public void testDispose () {
        ScreenManager<IScreen> manager = this.createScreenManager();
        manager.addScreen("dummy_screen", new DummyScreen());
        manager.push("dummy_screen");

        manager.dispose();
    }

    protected ScreenManager<IScreen> createScreenManager () {
        return new DefaultScreenManager();
    }

    protected DefaultScreenManager createDefaultScreenManager () {
        return new DefaultScreenManager();
    }

}
