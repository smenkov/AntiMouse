package com.sergprog.Robot;


import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.event.InputEvent;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sergprog.Main.settings;


public class KeyCatcher extends Thread implements NativeKeyListener {

    private boolean left = false;
    private boolean up = false;
    private boolean right = false;
    private boolean down = false;

    private boolean WheelUp;
    private boolean WheelDown;

    private boolean run;
    private int daley = Settings.FPS * 100;


    private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private Commander commander;

    private HashSet<Integer> pressedCount;
    private int second_daley = 0;

    public KeyCatcher(Commander commander) {
        this.commander = commander;
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.OFF);
        GlobalScreen.addNativeKeyListener(this);
        pressedCount = new HashSet<>();
        run = true;
        start();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int key = nativeKeyEvent.getKeyCode();
        pressedCount.add(key);
        if (!right && key == settings.LEFT_ID) left = true;
        if (!left && key == settings.RIGHT_ID) right = true;
        if (!down && key == settings.UP_ID) up = true;
        if (!up && key == settings.DOWN_ID) down = true;

        if (key == settings.LKM_ID) commander.mousePress(InputEvent.BUTTON1_MASK);
        if (key == settings.PRESS_WHEEL) commander.mousePress(InputEvent.BUTTON2_MASK);
        if (key == settings.PKM_ID) commander.mousePress(InputEvent.BUTTON3_MASK);

        if (!WheelDown && key == settings.UP_WHEEL) WheelUp = true;
        if (!WheelUp && key == settings.DOWN_WHEEL) WheelDown = true;


    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        int key = nativeKeyEvent.getKeyCode();
        pressedCount.remove(key);
        if (key == settings.LEFT_ID) left = false;
        if (key == settings.RIGHT_ID) right = false;
        if (key == settings.UP_ID) up = false;
        if (key == settings.DOWN_ID) down = false;

        if (key == settings.LKM_ID) commander.mouseRelease(InputEvent.BUTTON1_MASK);
        if (key == settings.PRESS_WHEEL) commander.mouseRelease(InputEvent.BUTTON2_MASK);
        if (key == settings.PKM_ID) commander.mouseRelease(InputEvent.BUTTON3_MASK);

        if (key == settings.UP_WHEEL) WheelUp = false;
        if (key == settings.DOWN_WHEEL) WheelDown = false;

    }

    @Override
    public void run() {
        super.run();
        while (run) {

            if (left) commander.Drag(-1, 0);
            else if (right) commander.Drag(1, 0);
            if (up) commander.Drag(0, -1);
            else if (down) commander.Drag(0, 1);

            if (WheelUp) commander.mouseWheel(+settings.WheelSpeed);
            else if (WheelDown) commander.mouseWheel(-settings.WheelSpeed);


            if (second_daley != 0 && pressedCount.size() != 0) {
                daley = Settings.FPS;
                second_daley = 0;
            }

            if (second_daley > settings.TIME_REALISE*1000/Settings.FPS) daley = Settings.FPS * 100;
            else second_daley++;

            try {
                sleep(daley);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void close() {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeKeyListener(this);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}