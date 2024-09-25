// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: CarAccelerate.java

package inputActions;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jmex.physics.vehicle.Car;

public class CarAccelerate extends KeyInputAction
{
    public CarAccelerate(Car car)
    {        
        this.car = car;
    }

    public void performAction(InputActionEvent event)
    {        
        car.addToGasPedal(30*0.05f*this.getSpeed());
    }

    private Car car;
}
