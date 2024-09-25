/*
 * Created on 15.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package camera;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;


/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SphericalMouseLook implements MouseInputAction
{
    private float wheelSpeed;
    private RelativeMouse mouse;
    private CameraHandler camHandler;
    private boolean lockHorizontal, lockVertical, lockWheel;
    private boolean inverseVertical;
    private float moveSpeed;
    private String key;

    //tmp variables
    private float time;
    private float thetaGoal, phiGoal;
    private MouseInput input;
    
    public SphericalMouseLook(Mouse mouse, CameraHandler camHandler)
    {
        this.mouse = (RelativeMouse)mouse;
        this.camHandler = camHandler;
        
        moveSpeed = 0.025f;
        wheelSpeed = 0.01f;
        
        input = mouse.getMouseInput();
    }
    
    public void performAction(InputActionEvent evt)
    {
        time = evt.getTime()*moveSpeed;
        
        thetaGoal = camHandler.getThetaGoal();
        phiGoal = camHandler.getPhiGoal();
        
        if(!lockHorizontal)
        {
            if(inverseVertical)
                thetaGoal+=(input.getXDelta()*time);
            else
                thetaGoal-=(input.getXDelta()*time);
            
            camHandler.setThetaGoal(thetaGoal);
        }

        if(!lockVertical)
        {
            phiGoal+=(input.getYDelta()*time);

            camHandler.setPhiGoal(phiGoal);
        }

        if(!lockWheel)
            camHandler.setGoalDistance(camHandler.getGoalDistance() - input.getWheelDelta()*wheelSpeed);
        
        
        //System.out.println(phiGoal*FastMath.RAD_TO_DEG+"/"+thetaGoal*FastMath.RAD_TO_DEG);
    }
    
    public void setSpeed(float speed)
    {
        moveSpeed = speed;
    }
    
    public void setWheelSpeed(float speed)
    {
        wheelSpeed = speed;
    }
    
    public float getSpeed()
    {
        return moveSpeed;
    }
    
    public float getWheelSpeed()
    {
        return wheelSpeed;
    }
    
    public void setMouse(Mouse mouse)
    {
        this.mouse=(RelativeMouse)mouse;
    }
    
    public void setKey(String key)
    {
        this.key=key;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setLockHorizontal(boolean lock)
    {
        this.lockHorizontal=lock;
    }

    public void setLockVertica(boolean lock)
    {
        this.lockVertical=lock;
    }

    public void setLockWheel(boolean lock)
    {
        this.lockWheel=lock;
    }
    
    public void setInverseVertical(boolean lock)
    {
        this.inverseVertical=lock;
    }
    
    public boolean getLockHorizontal()
    {
        return this.lockHorizontal;
    }
    
    public boolean getLockVertical()
    {
        return this.lockVertical;
    }

    public boolean getLockWheel()
    {
        return this.lockWheel;
    }

	public boolean getInverseVertical()
	{
	    return this.inverseVertical;
	}

}