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
package com.jmex.physics.vehicle;

import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsWorld;
import com.jmex.physics.joints.Hinge2Joint;

/**
 * A <code>Vehicle</code> contains a chassis with wheels. It also contains 
 * parameters for engine power and suspention. Its max speed is by default
 * enginePower/100.
 * 
 * <p>
 * You control its movement by using its steering wheel and gas pedal.
 * </p>
 * 
 * <p>
 * Derived classes will need to define how gas and steering should be handled.
 * They will also need to initialise the <code>Hinge2Joint</code>s that connect
 * the wheels to the chassis.
 * </p>
 * 
 * <p>
 * TODO: Let the user better handle the Vehicle as a whole. E.g. when user moves or 
 * rotates the chassis, the wheels and anchor points should follow.
 * </p>
 * 
 * @see com.jmex.physics.joints.Hinge2Joint
 * @see com.jmex.physics.vehicle.Car
 * 
 * @author Per Thulin
 */
public abstract class Vehicle {
	
	/** The max/min angle of the steering wheel (in radians). */
	protected float steeringWheelMax;
	
	/** The PhysicsObject that makes out the chassis. */
	protected DynamicPhysicsObject chassis;
	
	/** The PhysicsObjects that makes out the wheels of the vehicle. */
	protected DynamicPhysicsObject[] wheels;
	
	/** The hinges that connect the wheels to the chassis. */
	protected Hinge2Joint[] joints;
	
	/** The power of the engine. */
	protected float enginePower;

	/** The power of the brake. */
	protected float brakePower;

	/** Brake balance. */
	protected float brakeBalance;
	
	/** By default it's enginePower/100. */
	protected float maxSpeed;
	
	/** The suspention constant. */
	protected float suspention;
	
	/** The amount/percentage that the brake pedal is pressed. */
	protected float brakePedal;

	/** The amount/percentage that the gas pedal is pressed. */
	protected float gasPedal;
	
	/** The percentage of the steer limit that the steering wheel is turned. */
	protected float steeringWheel;
	
	/**
	 * Creates a new <code>Vehicle</code> with the given chassis and wheels.
	 * 
	 * @param chassis
	 *          The DynamicPhysicsObject that makes out the chassis of the vehicle.
	 * @param wheels
	 *          The wheels of the vehicle. Each wheel is connected to the chassis via an <code>Hinge2Joint</code>
	 * @param enginePower
	 *          The power of the engine. A heavy vehicle needs a powerful engine.
	 * @param brakePower
	 *          The power of the brakes. A heavy vehicle needs a powerful brakes.
	 * @param enginePower
	 *          The power of the engine. A heavy vehicle needs a powerful engine.
	 * @param suspention
	 *          A lower value stiffens the suspention.
	 */
	public Vehicle(DynamicPhysicsObject chassis, DynamicPhysicsObject[] wheels, 
			float enginePower, float brakePower, float brakeBalance, float suspention) {
		
		this.chassis = chassis;
		this.wheels = wheels;
		
		setEnginePower(enginePower);
		setBrakePower(brakePower);
		setBrakeBalance(brakeBalance);
		setSuspention(suspention);
		
		maxSpeed = enginePower/10.0f;
	}
	
    /**
     * @see com.jmex.physics.vehicle.Vehicle#setNeutral()
     */
    public abstract void setNeutrall();

    /**
	 * Sets how many percents of the maximum angle the steering wheel is rotated in.
	 * A value of 1 means that it's fully rotated to the right, and a value of 
	 * -1 means that it's fully rotated
	 * to the left.
	 * 
	 * @param percent A value that can range from -1 to 1.
	 * @see com.jmex.physics.vehicle.Vehicle#addToSteeringWheel(float)
	 * @see com.jmex.physics.vehicle.Vehicle#setSteeringWheelMax(float)
	 */
    public abstract void setSteeringWheel(float percent);
	
	/**
	 * Sets how hard the gas pedal is being stepped on (e.g. how many percent
	 * of the maximum speed that is being targetted). A value of 1 means
	 * that it's fully down, and a value of -1 means that it's in full reverse
	 * (hehe yeah I know, that doesn't make much sense). And well, yes, you
	 * guessed right: a value of 0 means that it isn't pressed at all. 
	 * 
	 * @param percent A value that can range from -1 to 1.
	 * @see com.jmex.physics.vehicle.Vehicle#addToGasPedal(float)
	 * @see com.jmex.physics.vehicle.Vehicle#setMaxSpeed(float)
	 */

    public abstract void setGasPedal(float percent);
	
	/**
	 * Adds a given amount of rotation to the steering wheel
	 * 
	 * @param percent The percent to add.
	 */
	public abstract void addToSteeringWheel(float percent);
	
	/**
	 * Presses a given amount on the gas pedal.
	 * 
	 * @param percent The percent to add.
	 */
	public abstract void addToGasPedal(float percent);
	
	/**
	 * Detatches a given wheel from the car chassis. Could be useful for damage
	 * simulation and such.
	 * 
	 * @param wheelNr The wheel number. E.g. 0 means the front left wheel.
	 */
	public void detachWheel(int wheelNr) {
		joints[wheelNr].delete();
	}
	
	/**
	 * Gets the wheels of this vehicle.
	 */
	public DynamicPhysicsObject[] getWheels() {
		return wheels;
	}
	
	/**
	 * Gets the amount of gas that is being applied. A value of 1 means that
	 * the gas pedal is fully down, and a value of -1 means that we are
	 * in full reverse.
	 * 
	 * @return The amount of gas that is being applied.
	 */
	public float getGasPedal() {
		return gasPedal;
	}
	
	/**
	 * Gets the position of the steering wheel. A value of 1 means that it's
	 * fully rotated to the right, and a value of -1 means it's fully rotated
	 * to the left.
	 * 
	 * @return The position of the steering wheel.
	 */
	public float getSteeringWheel() {
		return steeringWheel;
	}

	/**
	 * Sets the engine power of this <code>Vehicle</code>.
	 * 
	 * @param enginePower The engine power to set.
	 */
	public void setEnginePower(float enginePower) {
		this.enginePower = enginePower;
	}

	/**
	 * Returns the engine power of this <code>Vehicle</code>.
	 * 
	 * @return The engine power.
	 */
	public float getEnginePower() {
		return enginePower;
	}

	/**
	 * Sets the brake power of this <code>Vehicle</code>.
	 * 
	 * @param brakePower The brake power to set.
	 */
	public void setBrakePower(float brakePower) {
		this.brakePower = brakePower;
	}

	/**
	 * Returns the brake power of this <code>Vehicle</code>.
	 * 
	 * @return The brake power.
	 */
	public float getBrakePower() {
		return brakePower;
	}

	/**
	 * Presses a given amount on the brake pedal.
	 * 
	 * @param percent The percent to add.
	 */
	public abstract void addToBrakePedal(float percent);

	/**
	 * Sets the brake power of this <code>Vehicle</code>.
	 * 
	 * @param brakeBalance The brake balance to set - 2.0 means 100% @param brakePower at front / 200% @param brakePower at rear.
	 */
	public void setBrakeBalance(float brakeBalance) {
		this.brakeBalance = brakeBalance;
	}

	/**
	 * Returns the brake balance of this <code>Vehicle</code>.
	 * 
	 * @return The brake balance.
	 */
	public float getBrakeBalance() {
		return brakeBalance;
	}

	/**
	 * Sets the suspention of this <code>Vehicle</code>. A smaller value
	 * stiffens it.
	 * 
	 * @param suspention The suspention to set.
	 */
	public void setSuspention(float suspention) {
		this.suspention = suspention;
	}

	/**
	 * Returns thesuspention of this <code>Vehicle</code>.
	 * 
	 * @return Returns the suspention.
	 */
	public float getSuspention() {
		return suspention;
	}

	/**
	 * Sets the maximum speed of this vehicle. By default it's enginePower/100.
	 * 
	 * @param maxSpeed The maxSpeed to set.
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Gets the maximum speed of this vehicle. By default it's enginePower/100.
	 * 
	 * @return Returns the maxSpeed.
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}
	
	/**
	 * Sets the maximum angle that the steering wheel can be turned in.
	 * 
	 * @param steeringWheelMax The steeringWheelMax to set <b>(in radians)</b>.
	 */
	public void setSteeringWheelMax(float steeringWheelMax) {
		this.steeringWheelMax = steeringWheelMax;
	}

	/**
	 * Gets the maximum angle that the steering wheel can be turned in.
	 * 
	 * @return Returns the steeringWheelMax <b>(in radians)</b>.
	 */
	public float getSteeringWheelMax() {
		return steeringWheelMax;
	}

	/**
	 * Returns the chassis of this Vehicle.
	 * 
	 * @return Returns the chassis.
	 */
	public DynamicPhysicsObject getChassis() {
		return chassis;
	}
	
	/**
	 * Just a convenient way to add the chassis and wheels to the PhysicsWorld.
	 *
	 * @see com.jmex.physics.vehicle.Vehicle#getChassis()
	 * @see com.jmex.physics.vehicle.Vehicle#getWheels()
	 */
	public void addToWorld() {
		PhysicsWorld.getInstance().addObject(chassis);
		for (int i = 0; i < wheels.length; i++) {
			PhysicsWorld.getInstance().addObject(wheels[i]);	
		}
	}
	
	/**
	 * Just a convenient way to remove the chassis and wheels from the PhysicsWorld.
	 *
	 * @see com.jmex.physics.vehicle.Vehicle#getChassis()
	 * @see com.jmex.physics.vehicle.Vehicle#getWheels()
	 */
	public void removeFromWorld() {
		PhysicsWorld.getInstance().removeObject(chassis);
		for (int i = 0; i < wheels.length; i++) {
			PhysicsWorld.getInstance().removeObject(wheels[i]);	
		}
	}
	
	/**
	 * Calculates the total mass of the chassis and wheels of this
	 * <code>Vehicle</code>.
	 * 
	 * @return The total mass of this <code>Vehicle</code>.
	 */
	public float getMass() {
		float totalMass = chassis.getMass();
		
		for (int i = 0; i < wheels.length; i++) {
			totalMass += wheels[i].getMass();
		}
		
		return totalMass;		
	}
	
}