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

import org.odejava.JointHinge2;

import com.jme.math.Vector3f;
import com.jme.system.JmeException;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * @author Ahmed
 */
public class Hinge2Joint extends Joint {

	/**
	 * Constructor for Hinge2Joint. Takes the name of the joint and the objects
	 * to connect
	 * 
	 * @param name
	 * @param obj1
	 * @param obj2
	 */
	public Hinge2Joint(String name,
			DynamicPhysicsObject obj1,
			DynamicPhysicsObject obj2) {
		super(name, JT_HINGE2, obj1, obj2);
	}

	/**
	 * Set the anchor location.
	 * 
	 * @param position
	 *            A vector holding the anchor location
	 */
	public void setAnchor(Vector3f position) {
		((JointHinge2) odeJoint).setAnchor(position);
	}

	/**
	 * Get the anchor location of the joint, relative to body 1. Because the
	 * bodies of the two attached objects move, the anchor location will become
	 * relative to each object, hence the need for two methods
	 * 
	 * @return A new vector object containing the location
	 */
	public Vector3f getAnchorRelativeToObj1() {
		return ((JointHinge2) odeJoint).getAnchor();
	}

	/**
	 * Get the anchor location of the joint, relative to body 2. A new Vector3f
	 * instance will be created for each request. This is identical to calling
	 * <code>getAnchor2(null)</code>.
	 * 
	 * @return A new vector object containing the location
	 */
	public Vector3f getAnchorRelativeToObj2() {
		return ((JointHinge2) odeJoint).getAnchor2();
	}

	/**
	 * Set the axis 1 vector to a new value. If this was a car, this would be
	 * the normalised direction of the suspension.
	 * 
	 * @param axis
	 */
	public void setAxis1(Vector3f axis) {
		((JointHinge2) odeJoint).setAxis1(axis);
	}

	/**
	 * Set the axis 2 vector. If this was the car, this would be the axel of the
	 * car that holds the tyre to the chassis.
	 * 
	 * @param axis
	 */
	public void setAxis2(Vector3f axis) {
		((JointHinge2) odeJoint).setAxis2(axis);
	}

	/**
	 * Get the axis 1 direction vector.
	 * 
	 * @return A new vector object containing the location
	 */
	public Vector3f getAxis1() {
		return ((JointHinge2) odeJoint).getAxis1();
	}

	/**
	 * Get the axis 2 direction vector
	 * 
	 * @return A new vector object containing the location
	 */
	public Vector3f getAxis2() {
		return ((JointHinge2) odeJoint).getAxis2();
	}

	/**
	 * Get the angle of rotation around axis 1. Since this is the only axis
	 * which allows rotation around it, no other method
	 * 
	 * @return
	 */
	public float getAngleAroundAxis1() {
		return ((JointHinge2) odeJoint).getAngle1();
	}

	/**
	 * Set the angular speed that you would like axis 1 to achieve. This is not
	 * an instantaneous change, it is just a request that the system attempt to
	 * achieve this. Typically the torque of the motor on this axis will be
	 * used, but other factors, such as external forces will come into play.
	 * 
	 * @param speed
	 *            The speed that should be achieved
	 */
	public void setDesiredAngularVelocityAroundAxis1(float speed) {
		((JointHinge2) odeJoint).setDesiredAngularVelocity1(speed);
	}

	/**
	 * Get the requested angular speed currently set axis 1. This requests the
	 * desired speed, not the actual speed currently calculated by the physics
	 * model.
	 * 
	 * @return The current speed requested
	 */
	public float getDesiredAngularVelocityAroundAxis1() {
		return ((JointHinge2) odeJoint).getDesiredAngularVelocity1();
	}

	/**
	 * Set the angular speed that you would like axis 2 to achieve. This is not
	 * an instantaneous change, it is just a request that the system attempt to
	 * achieve this. Typically the torque of the motor on this axis will be
	 * used, but other factors, such as external forces will come into play.
	 * 
	 * @param speed
	 *            The speed that should be achieved
	 */
	public void setDesiredAngularVelocityAroundAxis2(float speed) {
		((JointHinge2) odeJoint).setDesiredAngularVelocity2(speed);
	}

	/**
	 * Get the requested angular speed currently set axis 2. This requests the
	 * desired speed, not the actual speed currently calculated by the physics
	 * model.
	 * 
	 * @return The current speed requested
	 */
	public float getDesiredAngularVelocityAroundAxis2() {
		return ((JointHinge2) odeJoint).getDesiredAngularVelocity2();
	}

	/**
	 * Set the amount of torque on axis 1 that can be applied by the motor, in
	 * order to reach the desired angular velocity. Value must always be greater
	 * than zero for it to have an effect. A value of zero disables this motor.
	 * Torque is defined in Newton-metres.
	 * 
	 * @param torque
	 *            The amount of torque to use in Nm
	 */
	public void setMaxTorqueOnAxis1(float torque) {

		if (torque < 0) {
			throw new JmeException(
					"Maximum Torque on axis 1 cannot be less than 0 on joint: "
							+ getName());
		} else {
			((JointHinge2) odeJoint).setMaxTorque1(torque);
		}
	}

	/**
	 * Get the amount of the torque currently set for the motor on axis 1.
	 * 
	 * @return The current torque in Nm
	 */
	public float getMaxTorqueOnAxis1() {
		return ((JointHinge2) odeJoint).getMaxTorque1();
	}

	/**
	 * Set the amount of torque on axis 2 that can be applied by the motor, in
	 * order to reach the desired angular velocity. Value must always be greater
	 * than zero for it to have an effect. A value of zero disables this motor.
	 * Torque is defined in Newton-metres.
	 * 
	 * @param torque
	 *            The amount of torque to use in Nm
	 */
	public void setMaxTorqueOnAxis2(float torque) {

		if (torque < 0) {
			throw new JmeException(
					"Maxium torque on axis 2 cannot be less than 0 in joint: "
							+ getName());
		} else {
			((JointHinge2) odeJoint).setMaxTorque2(torque);
		}
	}

	/**
	 * Get the amount of the torque currently set for the motor on axis 2.
	 * 
	 * @return The current torque in Nm
	 */
	public float getMaxTorqueOnAxis2() {
		return ((JointHinge2) odeJoint).getMaxTorque2();
	}

	/**
	 * Set the amount of constant force to mix into the system on axis 1 when
	 * the bodies are not at a stop. This value has no effect when the bodies
	 * are at one of the two stops for this axis.
	 * 
	 * @param force
	 *            The amount of force to use
	 */
	public void setConstantForceMixForAxis1(float force) {
		((JointHinge2) odeJoint).setConstantForceMix(force);
	}

	/**
	 * Get the amount of the constant force mix parameter currently set for axis
	 * 1 positions between the two stops.
	 * 
	 * @return The current constant force mix
	 */
	public float getConstantForceMixForAxis1() {
		return ((JointHinge2) odeJoint).getConstantForceMix();
	}

	/**
	 * Set the amount of constant force to mix into the system on axis 2 when
	 * the bodies are not at a stop. This value has no effect when the bodies
	 * are at one of the two stops for this axis.
	 * 
	 * @param force
	 *            The amount of force to use
	 */
	public void setConstantForceMixForAxis2(float force) {
		((JointHinge2) odeJoint).setConstantForceMix2(force);
	}

	/**
	 * Get the amount of the constant force mix parameter currently set for axis
	 * 2 positions between the two stops.
	 * 
	 * @return The current constant force mix
	 */
	public float getConstantForceMixForAxis2() {
		return ((JointHinge2) odeJoint).getConstantForceMix2();
	}

	/**
	 * Set the amount of suspension error reduction. This value should be
	 * between 0 and 1. 0 is no reduction at all, 1 is full correction in a
	 * single step. Suspension values only apply to axis 1.
	 * 
	 * @param erp
	 *            The amount of error reduction to use
	 */
	public void setSuspensionERP(float erp) {
		((JointHinge2) odeJoint).setSuspensionERP(erp);
	}

	/**
	 * Get the amount of the suspension error reduction parameter currently set.
	 * This value will be between 0 and 1. 0 is no correction at all, 1 is full
	 * correction in a single step.Suspension values only apply to axis 1.
	 * 
	 * @return A value between 0 and 1
	 */
	public float getSuspensionERP() {
		return ((JointHinge2) odeJoint).getSuspensionERP();
	}

	/**
	 * Set the amount of suspension constant force to mix into the system when
	 * the bodies are travelling between the stops on axis 1. This value has no
	 * effect when the bodies are at stops. Suspension values only apply to axis
	 * 1.
	 * 
	 * @param force
	 *            The amount of force to use
	 */
	public void setSuspensionCFM(float force) {
		((JointHinge2) odeJoint).setSuspensionCFM(force);
	}

	/**
	 * Get the amount of the suspension constant force mix parameter currently
	 * set. Suspension values only apply to axis 1.
	 * 
	 * @return The current constant force mix at the stops
	 */
	public float getSuspensionCFM() {
		return ((JointHinge2) odeJoint).getSuspensionCFM();
	}

	/**
	 * Set the minimum angle that this joint is permitted to rotate to around
	 * axis 1. Angles are specified relative to the initial position that the
	 * joint was created in. The angle value is limited to the range +/- &pi;.
	 * If the the provided angle is out of this range, an exception is thrown.
	 * <p>
	 * Note that if the maximum angle provided is less than the minimum angle at
	 * the point of evaluation, ODE ignores all limits.
	 * <p>
	 * A value of Float.NEGATIVE_INFINITY can be used to disable the minimum
	 * stop.
	 * 
	 * @param angle
	 *            The minimum stop angle in radians [-&pi;,+&pi;] or
	 *            Float.NEGATIVE_INFINITY
	 */
	public void setMinAngleStop(float angle) {
		((JointHinge2) odeJoint).setMinAngleStop(angle);
	}

	/**
	 * Fetch the currently set maximum angle stop for axis 1from this joint.
	 * 
	 * @return A angle in radians in the range [-&pi;,+&pi;] or
	 *         Float.NEGATIVE_INFINITY
	 */
	public float getMinAngleStop() {
		return ((JointHinge2) odeJoint).getMinAngleStop();
	}

	/**
	 * Set the maximum angle that this joint is permitted to rotate to around
	 * axis 1. Angles are specified relative to the initial position that the
	 * joint was created in. The angle value is limited to the range +/- &pi;.
	 * If the the provided angle is out of this range, an exception is thrown.
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
		((JointHinge2) odeJoint).setMaxAngleStop(angle);
	}

	/**
	 * Fetch the currently set maximum angle stop for axis 1 from this joint.
	 * 
	 * @return A angle in radians in the range [-&pi;,+&pi;] or
	 *         Float.POSITIVE_INFINITY
	 */
	public float getMaxAngleStop() {
		return ((JointHinge2) odeJoint).getMaxAngleStop();
	}

	/**
	 * Set the bounciness of the stops for axis 1. This is a value in the range
	 * [0,1] defining how hitting the stop will effect the return travel of the
	 * two bodies. A value of 0 means there is no bounce and the bodies will not
	 * bounce back. A value of 1 means the full contact velocity at the stop
	 * will be reflected back in the opposite direction.
	 * 
	 * @param bounce
	 *            The bounciness factor in the range [0,1]
	 * @throws IllegalArgumentException
	 *             The bounce factor is out of range
	 */
	public void setStopBounce(float bounce) {
		((JointHinge2) odeJoint).setStopBounce(bounce);
	}

	/**
	 * Fetch the current bounce factor for the stop on axis 1.
	 * 
	 * @return The bounce factor as a value in the range [0,1]
	 */
	public float getStopBounce() {
		return ((JointHinge2) odeJoint).getStopBounce();
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
		((JointHinge2) odeJoint).setStopERP(erp);
	}

	/**
	 * Get the amount of the stop error reduction parameter currently set. This
	 * value will be between 0 and 1. 0 is no bounce at all, 1 is full bounce.
	 * 
	 * @return A value between 0 and 1
	 */
	public float getStopERP() {
		return ((JointHinge2) odeJoint).getStopERP();
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
		((JointHinge2) odeJoint).setStopCFM(force);
	}

	/**
	 * Get the amount of the stop constant force mix parameter currently set.
	 * 
	 * @return The current constant force mix at the stops
	 */
	public float getStopCFM() {
		return ((JointHinge2) odeJoint).getStopCFM();
	}

}