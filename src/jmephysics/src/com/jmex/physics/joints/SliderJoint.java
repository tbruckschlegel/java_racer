package com.jmex.physics.joints;

import org.odejava.JointSlider;
import org.odejava.ode.OdeConstants;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * @author Patrick
 */
public class SliderJoint extends Joint {

	public SliderJoint(String name,
			DynamicPhysicsObject obj1,
			DynamicPhysicsObject obj2) {
		super(name, JT_SLIDER, obj1, obj2);
	}

	public void setAxis1(Vector3f axis) {
		odeJoint.setAxis1(axis.x, axis.y, axis.z);
	}

	public Vector3f getAxis1() {
		return ((JointSlider) odeJoint).getAxis( null );
	}

	public float getPosition() {
		return ((JointSlider) odeJoint).getPosition();
	}

    public float getPositionRate() {
        return ((JointSlider) odeJoint).getPositionRate();
    }

    public void setMaximumPosition(float distance) {
    	((JointSlider) odeJoint).setMaximumPosition(distance);
    }

    public float getMaximumPosition() {
        return ((JointSlider) odeJoint).getMaximumPosition();
    }

    public void setMinimumPosition(float distance) {
    	((JointSlider) odeJoint).setMinimumPosition(distance);
    }

    public float getMinimumPosition() {
        return ((JointSlider) odeJoint).getMinimumPosition();
    }

    public void setConstantForceMix(float force) {
    	((JointSlider) odeJoint).setConstantForceMix(force);
    }

    public float getConstantForceMix() {
        return ((JointSlider) odeJoint).getConstantForceMix();
    }
    
    /** TODO IllegalArgumentException */
    public void setStopBounce(float bounce) {
    	((JointSlider) odeJoint).setStopBounce(bounce);
    }

    public float getStopBounce() {
        return ((JointSlider) odeJoint).getStopBounce();
    }
    
    public void setMaxTorque(float force) {
    	odeJoint.setParam(OdeConstants.dParamFMax, force);
    }
    
    public void setDesiredVelocity(float vel) {
    	odeJoint.setParam(OdeConstants.dParamVel, vel);
    }

    public void setStopERP(float erp) {
    	((JointSlider) odeJoint).setStopERP(erp);
    }

    public float getStopERP() {
        return ((JointSlider) odeJoint).getStopERP();
    }

    public void setStopCFM(float force) {
    	((JointSlider) odeJoint).setStopCFM(force);
    }

    public float getStopCFM() {
        return ((JointSlider) odeJoint).getStopCFM();
    }
}
