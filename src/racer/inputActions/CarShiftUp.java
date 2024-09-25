/*
 * Created on 12.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package inputActions;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jmex.physics.vehicle.Car;

/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CarShiftUp extends KeyInputAction
{

    public CarShiftUp(Car car)
    {
        setAllowsRepeats(false);
        this.car = car;
    }

    public void performAction(InputActionEvent event)
    {
        car.shiftUp();
    }

    private Car car;
}
