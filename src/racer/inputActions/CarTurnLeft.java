// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: CarTurnLeft.java

package inputActions;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jmex.physics.vehicle.Car;

public class CarTurnLeft extends KeyInputAction
{

    public CarTurnLeft(Car car)
    {
        this.car = car;
    }

    public void performAction(InputActionEvent event)
    {
        car.addToSteeringWheel(30*-0.04f*this.getSpeed());
    }

    private Car car;
}
