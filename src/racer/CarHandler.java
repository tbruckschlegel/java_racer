/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of
 * the Mojo Monkey Coding, jME, jMonkey Engine, nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * 
 */

import inputActions.CarAccelerate;
import inputActions.CarBrake;
import inputActions.CarClutch;
import inputActions.CarReset;
import inputActions.CarShiftDown;
import inputActions.CarShiftUp;
import inputActions.CarTurnLeft;
import inputActions.CarTurnRight;
import inputActions.Toggle;
import camera.CameraHandler;
import camera.SphericalMouseLook;

import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.RelativeMouse;
import com.jme.system.DisplaySystem;
import com.jmex.physics.vehicle.Car;
    
/**
 * @author Ahmed
 */
public class CarHandler extends InputHandler
{

    private Car car;

    private CameraHandler cameraHandler;

    private SphericalMouseLook mouseLook;

    private KeyInput key;

    public CarHandler(Car car, CameraHandler cameraHandler)
    {
        String renderer = DisplaySystem.getDisplaySystem().getRendererType().toString();
        InputSystem.createInputSystem(renderer);

        this.cameraHandler = cameraHandler;
       
        this.car = car;

        initKeyBindings();
        
        //initMouse();
        initActions();
    }

    private void initKeyBindings()
    {
        //key = InputSystem.getKeyInput();

        keyboard = KeyBindingManager.getKeyBindingManager();
        keyboard.setKeyInput(InputSystem.getKeyInput());
        keyboard.set("toggle", KeyInput.KEY_T);

        keyboard.set("shiftup", KeyInput.KEY_A);
        keyboard.set("shiftdown", KeyInput.KEY_Y);
        
        keyboard.set("gas", KeyInput.KEY_UP);
        keyboard.set("clutch", KeyInput.KEY_LSHIFT);
        keyboard.set("left", KeyInput.KEY_LEFT);
        keyboard.set("right", KeyInput.KEY_RIGHT);
        keyboard.set("brake", KeyInput.KEY_DOWN);
        keyboard.set("reset", KeyInput.KEY_BACK);
        
        setKeyBindingManager(keyboard);
        

    }

    private void initMouse()
    {
        mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());

        setMouse(mouse);

        mouseLook = new SphericalMouseLook(mouse, cameraHandler);

        addAction(mouseLook);
    }

    private void initActions()
    {
        Toggle toggle = new Toggle();
        toggle.setKey("toggle");
        addAction(toggle);

        CarShiftUp shiftup = new CarShiftUp(car);
        shiftup.setKey("shiftup");
        addAction(shiftup);
        CarShiftDown shiftdown = new CarShiftDown(car);
        shiftdown.setKey("shiftdown");
        addAction(shiftdown);
        
        CarAccelerate accel = new CarAccelerate(car);
        accel.setKey("gas");
        addAction(accel);
        CarClutch clutch = new CarClutch(car);
        clutch.setKey("clutch");
        addAction(clutch);
        CarTurnLeft left = new CarTurnLeft(car);
        left.setKey("left");
        addAction(left);
        CarTurnRight right = new CarTurnRight(car);
        right.setKey("right");
        addAction(right);
        CarBrake brake = new CarBrake(car);
        brake.setKey("brake");
        addAction(brake);
        CarReset reset = new CarReset(car);
        reset.setKey("reset");
        addAction(reset);

    }

    public void update(float tpf)
    {
        super.update(tpf);
        this.setKeySpeed(tpf);

        cameraHandler.update(tpf);

        // keyboard reset
        if (keyboard.isValidCommand("gas") == false && keyboard.isValidCommand("brake") == false
                && keyboard.isValidCommand("reverse") == false)
        {
            car.setNeutrall();
        }
        if (keyboard.isValidCommand("left") == false && keyboard.isValidCommand("right") == false)
        {
            car.setSteeringWheel(0f);
        }

    }
}
