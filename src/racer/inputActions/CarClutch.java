// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: CarClutch.java

package inputActions;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jmex.physics.vehicle.Car;

public class CarClutch extends KeyInputAction
{

    public CarClutch(Car car)
    {        
        this.car = car;
    }

    public void performAction(InputActionEvent event)
    {
        car.addToClutch(30*-0.5f*this.getSpeed());
    }

    private Car car;
}
