// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: CarReset.java

package inputActions;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jmex.physics.vehicle.Car;

public class CarReset extends KeyInputAction
{

    public CarReset(Car car)
    {
        this.car = car;
    }

    public void performAction(InputActionEvent event)
    {

    }

    private Car car;
}
