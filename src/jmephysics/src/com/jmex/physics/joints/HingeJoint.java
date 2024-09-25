/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jmex.physics.joints;

import org.odejava.JointHinge;
import org.odejava.ode.OdeConstants;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * @author Ahmed
 */
public class HingeJoint extends Joint {

	/**
	 * Constructor for HingeJoint. Takes a name, and the bodies that should be
	 * attached to each other.
	 * 
	 * @param name
	 * @param obj1
	 * @param obj2
	 */
	public HingeJoint(String name,
			DynamicPhysicsObject obj1,
			DynamicPhysicsObject obj2) {
		super(name, JT_HINGE, obj1, obj2);
	}

	/**
	 * Set the anchor point at which the hinge is
	 * 
	 * @param anchor
	 */
	public void setAnchor(Vector3f anchor) {
		((JointHinge) odeJoint).setAnchor(anchor.x, anchor.y, anchor.z);
	}

	/**
	 * Get the anchor point relative to object 1. Because the objects move, the
	 * anchor point is not going to stay the same. Its going to be different in
	 * relation to object one and object two. Hence the two methods.
	 * 
	 * @return
	 */
	public Vector3f getAnchorRelativeToObj1() {
		return ((JointHinge) odeJoint).getAnchor();
	}

	/**
	 * Get the anchor point relative to object 2. Because the objects move, the
	 * anchor point is not going to stay the same. Its going to be different in
	 * relation to object one and object two. Hence the two methods.
	 * 
	 * @return
	 */
	public Vector3f getAnchorRelativeToObj2() {
		return ((JointHinge) odeJoint).getAnchor2();
	}

	/**
	 * Set the axis at which both the objects move around
	 * 
	 * @param axis
	 */
	public void setAxis(Vector3f axis) {
		odeJoint.setAxis1(axis.x, axis.y, axis.z);
	}

	/**
	 * Get teh axis at which both the objects move around
	 * 
	 * @return
	 */
	public Vector3f getAxis() {
		return ((JointHinge) odeJoint).getAxis();
	}

	/**
	 * Get the angle between the two objects
	 * 
	 * @return
	 */
	public float getAngle() {
		return ((JointHinge) odeJoint).getAngle();
	}

	/**
	 * Set the amount of constant force to mix into the system when the bodies
	 * are not at a stop. This value has no effect when the bodies are at one of
	 * the two stops.
	 * 
	 * @param force
	 *            The amount of force to use
	 */
	public void setConstantForceMix(float force) {
		((JointHinge) odeJoint).setConstantForceMix(force);
	}

	/**
	 * Get the amount of the constant force mix parameter currently set for
	 * positions between the two stops.
	 * 
	 * @return The current constant force mix
	 */
	public float getConstantForceMix() {
		return ((JointHinge) odeJoint).getConstantForceMix();
	}

	/**
	 * Set the minimum angle that this joint is permitted to rotate to. Angles
	 * are specified relative to the initial position that the joint was created
	 * in. The angle value is limited to the range +/- &pi;. If the the provided
	 * angle is out of this range, an exception is thrown.
	 * <p>
	 * Note that if the maximum angle provided is less than the minimum angle at
	 * the point of evaluation, ODE ignores all limits.
	 * <p>
	 * A value of Float.NEGATIVE_INFINITY can be used to disable the maximum
	 * stop.
	 * 
	 * @param angle
	 *            The minimum stop angle in radians [-&pi;,+&pi;] or
	 *            Float.NEGATIVE_INFINITY
	 */
	public void setMinAngleStop(float angle) {
		((JointHinge) odeJoint).setMinAngleStop(angle);
	}

	/**
	 * Fetch the currently set maximum angle stop from this joint.
	 * 
	 * @return A angle in radians in the range [-&pi;,+&pi;] or
	 *         Float.NEGATIVE_INFINITY
	 */
	public float getMinAngleStop() {
		return ((JointHinge) odeJoint).getMinAngleStop();
	}

	/**
	 * Set the maximum angle that this joint is permitted to rotate to. Angles
	 * are specified relative to the initial position that the joint was created
	 * in. The angle value is limited to the range +/- &pi;. If the the provided
	 * angle is out of this range, an exception is thrown.
	 * <p>
	 * Note that if the maximum angle provided is less than the minimum angle at
	 * the point of evaluation, ODE ignores all limits.
	 * <p>
	 * A value of Float.POSITIVE_INFINITY can be used to disable the maximum
	 * stop.
	 * 
	 * @param angle
	 *            The maximum stop angle in radians [-&pi;,+&pi;] or
	 *            Float.POSITIVE_INFINITY
	 */
	public void setMaxAngleStop(float angle) {
		((JointHinge) odeJoint).setMaxAngleStop(angle);
	}

	/**
	 * Fetch the currently set maximum angle stop from this joint.
	 * 
	 * @return A angle in radians in the range [-&pi;,+&pi;] or
	 *         Float.POSITIVE_INFINITY
	 */
	public float getMaxAngleStop() {
		return ((JointHinge) odeJoint).getMaxAngleStop();
	}

	/**
	 * Set the bounciness of the stops. This is a value in the range [0,1]
	 * defining how hitting the stop will effect the return travel of the two
	 * bodies. A value of 0 means there is no bounce and the bodies will not
	 * bounce back. A value of 1 means the full contact velocity at the stop
	 * will be reflected back in the opposite direction.
	 * 
	 * @param bounce
	 *            The bounciness factor in the range [0,1]
	 */
	public void setStopBounce(float bounce) {
		((JointHinge) odeJoint).setStopBounce(bounce);
	}

	/**
	 * Fetch the current bounce factor for the hinge stop.
	 * 
	 * @return The bounce factor as a value in the range [0,1]
	 */
	public float getStopBounce() {
		return ((JointHinge) odeJoint).getStopBounce();
	}

	/**
	 * Set the amount of stop bounce error reduction. This value should be
	 * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
	 * single step.
	 * 
	 * @param erp
	 *            The amount of error reduction to use
	 */
	public void setStopERP(float erp) {
		((JointHinge) odeJoint).setStopERP(erp);
	}

	/**
	 * Get the amount of the stop error reduction parameter currently set. This
	 * value will be between 0 and 1. 0 is no bounce at all, 1 is full bounce.
	 * 
	 * @return A value between 0 and 1
	 */
	public float getStopERP() {
		return ((JointHinge) odeJoint).getStopERP();
	}

	/**
	 * Set the amount of stop constant force to mix into the system when the
	 * bodies reach a stop. This value has no effect when the bodies are not at
	 * the stops. Together with the ERP value, this can be used to get spongy or
	 * soft stops. Note that this is inteded for unpowered joints, it does not
	 * work as expected on powered joints.
	 * 
	 * @param force
	 *            The amount of force to use
	 */
	public void setStopCFM(float force) {
		((JointHinge) odeJoint).setStopCFM(force);
	}

	/**
	 * Get the amount of the stop constant force mix parameter currently set.
	 * 
	 * @return The current constant force mix at the stops
	 */
	public float getStopCFM() {
		return ((JointHinge) odeJoint).getStopCFM();
	}

    public void setDesiredAngularVelocity(float vel) {
        odeJoint.setParam(OdeConstants.dParamVel, vel);
    }

    public void setMaxTorque(float f) {
        odeJoint.setParam(OdeConstants.dParamFMax, f);
    }
}