/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of
 * the Mojo Monkey Coding, jME, jMonkey Engine, nor the names of its contributors may be used to endorse or promote
 * products derived frosetGasPedalm this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jmex.physics.vehicle;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.system.JmeException;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.joints.Hinge2Joint;

/**
 * What sets a <code>Car</code> apart from other <code>Vehicle</code> s is that it steers with its front wheels
 * (number: 0, 1), and can be driven by using both conventional and four-wheeled drive. This leads to that the <b>number
 * of wheels must be four </b>. It also has a handbrake.
 * <p>
 * By default the steering wheel and thus the front wheels can be turned in a 40 degree angle.
 * </p>
 * <p>
 * TODO: gear box and better brake handling.
 * </p>
 *
 * @author Per Thulin
 * @see com.jmex.physics.vehicle.Vehicle
 */
public class Car extends Vehicle
{
	private Vector3f startPosition = new Vector3f();
	private Quaternion startRotation = new Quaternion();

	private float speedFactor = 2;

	private float averageRPM = 0;
	private float intermediateAverageRPM = 0;
	private float averageRPMCounter = 0;

	/** Our gearbox **/
	private GearBox gearBox;

	/** our clutch **/
	private Clutch clutch;

	private int currentGear = 0;

	private float lastRPM = 0;

	private boolean isAccelerating = false;

	private boolean isBraking = false;

	/** Signals if this car is four-wheeled driven. */
	private boolean fourWheeled;

	/** Signals if this car is front driven. */
	private boolean frontDriven;

	/** Signals if this cars handbrake is down or not. */
	private boolean handbrake;

	/**
	 * Creates a new <code>Car</code> with the given chassis geometry and mass. Also one of the parameters is an array
	 * of car wheels. They are positioned using the three last parameters.
	 *
	 * @param chassis
	 *            The DynamicPhysicsObject that makes out the chassis of the vehicle.
	 * @param wheels
	 *            The wheels of the car. Each wheel is connected to the chassis via an <code>Hinge2Joint</code>
	 * @param suspention
	 *            A lower value stiffens the suspention.
	 * @param fourWheeled
	 *            Signals if this Car is to be four-wheeled driven.
	 * @param frontAxleCenter
	 *            The center of the front wheel axle (relative to the center of the car chassis).
	 * @param endAxleCenter
	 *            The center of the back wheel axle (relative to the center of the car chassis).
	 * @param axleLength
	 *            The length of the car axles. They are assumed to be of the same length.
	 */
	public Car(DynamicPhysicsObject chassis, DynamicPhysicsObject[] wheels, float brakePower,
			   float brakeBalance, float suspention, boolean frontDriven, boolean fourWheeled, Vector3f frontAxleCenter,
			   Vector3f endAxleCenter, float axleLength,
			   String rpmcurve, float minrpm, float maxrpm, float minnm, float maxnm)
	{
		super(chassis, wheels, 0/* engine power */, brakePower, brakeBalance, suspention);

		if (wheels.length != 4)
		{
			throw new JmeException("Car: " + chassis.getName() + " must have four wheels!");
		}

		this.fourWheeled = fourWheeled;
		this.frontDriven = frontDriven;

		if(fourWheeled && frontDriven)
		{
			throw new JmeException("Car: " + chassis.getName() + " can only be rear, front or four wheel driven!");
		}

		setSteeringWheelMax(45 * FastMath.DEG_TO_RAD);

		chassis.syncWithGraphical();

		try{
			gearBox = new GearBox(rpmcurve, minrpm, maxrpm, minnm, maxnm);
		}
		catch (IOException e)
		{
			throw new JmeException("buildLookUpTable: " + e.getMessage());
		}

		clutch = new Clutch();
		initWheels(frontAxleCenter, endAxleCenter, axleLength);
	}

	public void setStartPosition(Vector3f sPos)
	{
		startPosition.set(sPos);
	}

	public Vector3f getStartPosition()
	{
		return startPosition;
	}

	public void setStartRotation(Quaternion sRot)
	{
		startRotation.set(sRot);
	}

	public Quaternion getStartRotation()
	{
		return startRotation;
	}

	/**
	 * Sets the location of the wheels, and connects them to the car chassis via <code>Hinge2Joint</code>s. The
	 * location depends on the axle centers and length.
	 */
	private void initWheels(Vector3f frontAxle, Vector3f endAxle, float axleLength)
	{
		Vector3f wheelbase = new Vector3f(chassis.getSpatial().getLocalTranslation());

		wheels[0].getSpatial().getLocalTranslation().set(wheelbase);
		wheels[0].getSpatial().getLocalTranslation().addLocal(frontAxle);
		wheels[0].getSpatial().getLocalTranslation().z -= axleLength / 2;
		wheels[0].syncWithGraphical();
		wheels[0].setEnabled(true);

		wheels[1].getSpatial().getLocalTranslation().set(wheelbase);
		wheels[1].getSpatial().getLocalTranslation().addLocal(frontAxle);
		wheels[1].getSpatial().getLocalTranslation().z += axleLength / 2;
		wheels[1].syncWithGraphical();
		wheels[1].setEnabled(true);

		wheels[2].getSpatial().getLocalTranslation().set(wheelbase);
		wheels[2].getSpatial().getLocalTranslation().addLocal(endAxle);
		wheels[2].getSpatial().getLocalTranslation().z -= axleLength / 2;
		wheels[2].syncWithGraphical();
		wheels[2].setEnabled(true);

		wheels[2].setFiniteRotationMode(1);
		wheels[2].setFiniteRotationAxis(0, 0, 0);

		wheels[3].getSpatial().getLocalTranslation().set(wheelbase);
		wheels[3].getSpatial().getLocalTranslation().addLocal(endAxle);
		wheels[3].getSpatial().getLocalTranslation().z += axleLength / 2;
		wheels[3].syncWithGraphical();
		wheels[3].setEnabled(true);

		wheels[3].setFiniteRotationMode(1);
		wheels[3].setFiniteRotationAxis(0, 0, 0);

		// Create a Hinge2Joint for each wheel. The anchor points are going to
		// be the same as the corresponding wheel location.
		joints = new Hinge2Joint[wheels.length];
		for (int i = 0; i < joints.length; i++)
		{
			joints[i] = new Hinge2Joint("Joint for: " + wheels[i].getName(), chassis, wheels[i]);

			joints[i].attach();
			joints[i].setAnchor((Vector3f) wheels[i].getSpatial().getLocalTranslation().clone());

			joints[i].setAxis1(new Vector3f(0, 1, 0));
			joints[i].setAxis2(new Vector3f(0, 0, 1f));

			// Configure the suspention.
			joints[i].setSuspensionCFM(suspention);
			joints[i].setSuspensionERP(0.1f);//.9f);//0.0000125f);

			// Configure the engine.
			if (!fourWheeled)
			{
				if (!frontDriven)
				{
					if (i > 1)
					{
						joints[i].setMaxTorqueOnAxis2(enginePower);
						joints[i].setMaxTorqueOnAxis1(0);
					}
					else
					{
						joints[i].setMaxTorqueOnAxis2(0);
						joints[i].setMaxTorqueOnAxis1(0);            //(1.0f + 0.0125f * this.getChassis().getLinearVelocity().length());

					}
				}
				else
				{
					if (i > 1)
					{
						joints[i].setMaxTorqueOnAxis2(0);
						joints[i].setMaxTorqueOnAxis1(0);
					}
					else
					{
						joints[i].setMaxTorqueOnAxis2(enginePower);
						joints[i].setMaxTorqueOnAxis1(0);
					}
				}
			}
			else
			{
				joints[i].setMaxTorqueOnAxis2(enginePower);
				joints[i].setMaxTorqueOnAxis1(0);
			}

			// Align the wheels. Since the front wheels are going to turn, this
			// only affects the back wheels really.
			joints[i].setMinAngleStop(0);
			joints[i].setMaxAngleStop(0);

		}

	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#setSteeringWheel(float)
	 */
	public void setSteeringWheel(float percent)
	{
		if (percent > 1)
			percent = 1;
		else
		if (percent < -1)
			percent = -1;

		steeringWheel = percent;
		if (percent == 0)
		{
			joints[0].setMinAngleStop(0);
			joints[0].setMaxAngleStop(0);
			joints[1].setMinAngleStop(0);
			joints[1].setMaxAngleStop(0);
		}
		else
		{
			float newMax = getSteeringWheelMax() / (1.0f+0.125f*this.getChassis().getLinearVelocity().length());
			//System.out.println(getSteeringWheelMax()+"/"+newMax+"/"+(1.0f+0.125f*this.getChassis().getLinearVelocity().length()));
            /*
            System.out.println(FastMath.log(0.1f));
            System.out.println(FastMath.log(0.5f));
            System.out.println(FastMath.log(1.0f));
            System.out.println(FastMath.sqrt(this.getChassis().getLinearVelocity().length()));
            // make the max. steering angle speed dependant

            steeringWheel = steeringWheel * 10.0f/FastMath.sqrt(this.getChassis().getLinearVelocity().length());
            if(steeringWheel<-1.0f)
                steeringWheel=-1.0f;
            else
                if(steeringWheel>1.0f)
                    steeringWheel=1.0f;

            System.out.println("steering: "+steeringWheel);
            */
			float min = newMax * steeringWheel - newMax * 0.1f;
			float max = newMax * steeringWheel + newMax * 0.1f;

			joints[0].setMinAngleStop(min);
			joints[0].setMaxAngleStop(max);
			joints[1].setMinAngleStop(min);
			joints[1].setMaxAngleStop(max);
		}
	}

	//http://www.bgsoflex.com/airdragchart.html
	float dragCoefficient = 0.32f; //golf 4

	float tyreRollFriction = 0.008f;

	float tyreKineticFriction = 0.8f;

	float carFrontalArea = 2.1f; //golf 4

	float airDensity = 1.29f; //kg/m3

	public float calculateDrag_and_RollFriction()
	{
		float speed = this.getChassis().getLinearVelocity().length();

		float Fdrag = 0.005f*0.5f * dragCoefficient * carFrontalArea * airDensity * speed * speed * (speed*0.85f);
		float Frr = 5*0.005f*9.81f * tyreRollFriction * 10*this.getChassis().getMass();

		return Fdrag + Frr;
	}
//30s f\FCr 1000m (99,9)


	public float differentialRatio = 3.67f;

	private float transmissionEfficency = 0.7f; //70%

	public float wheelRadius = 0.32f;//*0.32f;//29f; // 29cm/m 185/65/14 inch

	// http://www.chris-longhurst/carbibles/tyre_bible.html

	Vector3f Fdrive = new Vector3f();
	Vector3f carDirection = new Vector3f();
	public Vector3f calculateFdrive(float currentEngineTorque, float gearRatio)
	{
		carDirection.set(this.getChassis().getSpatial().getLocalRotation().getRotationColumn(2));

		carDirection.normalizeLocal();
		//475*2.66*3.42*0.7/0.33
		//System.out.println("carDir: "+carDirection.toString()+"currentEngineTorque: "+currentEngineTorque);
		float tmp = currentEngineTorque * gearRatio * differentialRatio * transmissionEfficency / wheelRadius;

		//System.out.println(currentEngineTorque +"*"+ gearRatio +"*"+ differentialRatio +"*"+ transmissionEfficency +"/"+ wheelRadius);
		Fdrive.set(carDirection.x * tmp, carDirection.y * tmp, carDirection.z * tmp);

		//System.out.println("Fdrive : " + Fdrive.length());

		return Fdrive;
	}

	public float acceleration(float currentEngineTorque, float gearRatio)
	{
		float F = calculateFdrive(currentEngineTorque, gearRatio).length();
		//System.out.println("mass: "+this.getMass());
		return F / this.getMass();
	}

	public float getCurrentRPM()
	{
		return getRPM(currentGear);
	}

	public float getMinRPM()
	{
		return gearBox.getMinRPM();
	}

	public float getMaxRPM()
	{
		return gearBox.getMaxRPM();
	}

	Vector3f tmp = new Vector3f();
	public float getRPM(int gear)
	{
		//if (gear == 0)
		//  clutch.openCutch();


		// back calculation from wheel speed
		tmp.set(this.getWheels()[frontDriven?1:3].getAngularVelocity());

		float speed = tmp.length();

		tmp.set(this.getWheels()[frontDriven?0:2].getAngularVelocity());

		float speed2 = tmp.length();

		// choose the fastest rotating rear wheel
		speed = ((speed < speed2) ? speed2 : speed);

		float rpmFromWheel = 0.5f*speed * gearBox.getGearRatio(gear) * differentialRatio * 60.0f / 2 * FastMath.PI;
		float rpmFromGas = 0.5f*(gearBox.getMaxRPM() * getGasPedal()) < gearBox.getMinRPM() ? gearBox.getMinRPM() : gearBox.getMaxRPM() * getGasPedal();

		float rpm = ( (currentGear==0?0:clutch.getPosition()) * rpmFromWheel) + (1.0f - (currentGear==0?0:clutch.getPosition()) ) * rpmFromGas;
		/*
		 * System.out.println("clutch pos.: " + clutch.getPosition()); System.out.println("rpm wheel: " + rpmFromWheel);
		 * System.out.println("rpm gas: " + rpmFromGas); System.out.println("calc. rpm: " + rpm);
		 */
		// engine stall hack
		if (rpm < gearBox.getMinRPM())
			rpm = gearBox.getMinRPM();

		if (rpm > gearBox.getMaxRPM())
		{
			rpm = gearBox.getMaxRPM();

			float ang = speedFactor * (rpm / gearBox.getGearRatio(gear) / differentialRatio / 60.0f * 2 / FastMath.PI);
			if (fourWheeled)
			{

				joints[0].setDesiredAngularVelocityAroundAxis2(ang);
				joints[1].setDesiredAngularVelocityAroundAxis2(ang);
				joints[2].setDesiredAngularVelocityAroundAxis2(ang);
				joints[3].setDesiredAngularVelocityAroundAxis2(ang);
			}
			else
			{
				if (!frontDriven)
				{
					joints[2].setDesiredAngularVelocityAroundAxis2(ang);
					joints[3].setDesiredAngularVelocityAroundAxis2(ang);
				}
				else
				{
					joints[0].setDesiredAngularVelocityAroundAxis2(ang);
					joints[1].setDesiredAngularVelocityAroundAxis2(ang);

				}
			}
		}

		lastRPM = rpm;

		//System.out.println("rpm: " + rpm);

		return rpm;
	}

	public Vector3f calculateEngineTorque(float rpm, int gear)
	{
		return calculateFdrive(gearBox.getTorque((int)rpm) * getGasPedal(), gearBox.getGearRatio(gear));
	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#setNeutral()
	 */
	public void setNeutrall()
	{
		gasPedal = brakePedal = 0f;
		isBraking = isAccelerating = false;
	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#setBrakePedal(float)
	 */
	public void setBrakePedal(float percent)
	{
		if (percent > 1)
			percent = 1;
		else
		if (percent < -1)
			percent = -1;

		brakePedal = percent;
		isAccelerating = false;
		isBraking = true;

		joints[0].setMaxTorqueOnAxis2(brakePower);
		joints[1].setMaxTorqueOnAxis2(brakePower);

		joints[2].setMaxTorqueOnAxis2(brakePower * brakeBalance);
		joints[3].setMaxTorqueOnAxis2(brakePower * brakeBalance);

		joints[0].setDesiredAngularVelocityAroundAxis2(0);
		joints[1].setDesiredAngularVelocityAroundAxis2(0);
		joints[2].setDesiredAngularVelocityAroundAxis2(0);
		joints[3].setDesiredAngularVelocityAroundAxis2(0);

		//updatePowerOnWheels();

	}

	public float getCurrentGearRatio()
	{
		return gearBox.getGearRatio(currentGear);
	}

	public int getCurrentGear()
	{
		return currentGear;
	}

	public void setCurrentGear(int gear)
	{
		currentGear = gear;
	}

	public void shiftUp()
	{
		if (currentGear < 6 && clutch.isOpen())
		{
			currentGear++;
			//clutch.openCutch();
		}
	}

	public void shiftDown()
	{
		if (currentGear > -1 && clutch.isOpen())
		{
			currentGear--;
			//clutch.openCutch();
		}
	}

	// updates any time specfic interal task
	public void update(float tpf)
	{
		clutch.update(tpf);
		//System.out.println("clutch: "+clutch.getPosition());
		updatePowerOnWheels();
	}

	public float getClutchPosition()
	{
		return clutch.getPosition();

	}

	public void updatePowerOnWheels()
	{
        /*
        if(averageRPMCounter<8f)
        {
            averageRPMCounter++;
            intermediateAverageRPM+=getRPM(currentGear);
        }
        else
        {
            averageRPM=intermediateAverageRPM/8f;
            averageRPMCounter=0;
            intermediateAverageRPM=0;

        }
        */
		//System.out.println("ca: "+chassis.getAngularVelocity().length()+" cl:
		// "+chassis.getLinearVelocity().length());
		//System.out.println("cf: "+chassis.getForce().length());
		//System.out.println("ct: "+chassis.getTorque().length());
		//System.out.println("f w0: "+wheels[0].getForce()+"w1: "+wheels[1].getForce()+"w2: "+wheels[2].getForce()+"w3:
		// "+wheels[3].getForce());
		//System.out.println("t w0: "+wheels[0].getTorque()+"w1: "+wheels[1].getTorque()+"w2:
		// "+wheels[2].getTorque()+"w3: "+wheels[3].getTorque());

		//System.out.println("va w0: "+wheels[0].getAngularVelocity().length()+"w1:
		// "+wheels[1].getAngularVelocity().length()+"w2: "+wheels[2].getAngularVelocity().length()+"w3:
		// "+wheels[3].getAngularVelocity().length());

		float currentTorque = 0;
		float currentTorque2 = 0;
		if (isBraking)
		{
			currentTorque = (brakePower - calculateEngineTorque(getRPM(currentGear), currentGear).length())
					+ calculateDrag_and_RollFriction();

			currentTorque2 = (brakePower * brakeBalance - calculateEngineTorque(getRPM(currentGear), currentGear)
					.length())
					+ calculateDrag_and_RollFriction();
		}
		else
		{
			currentTorque = (currentGear==0?0:clutch.getPosition()) * calculateEngineTorque(getRPM(currentGear), currentGear).length()
					- calculateDrag_and_RollFriction();
		}

		//System.out.println("engine: "+calculateEngineTorque(getRPM(currentGear), currentGear).length());
		//System.out.println("drag: "+calculateDrag_and_RollFriction());

		if (isBraking)
		{
			//System.out.println("braking...");
			currentTorque = FastMath.abs(currentTorque);
			currentTorque2 = FastMath.abs(currentTorque2);

			joints[0].setDesiredAngularVelocityAroundAxis2(0);
			joints[1].setDesiredAngularVelocityAroundAxis2(0);
			joints[2].setDesiredAngularVelocityAroundAxis2(0);
			joints[3].setDesiredAngularVelocityAroundAxis2(0);

			joints[0].setMaxTorqueOnAxis2(currentTorque);
			joints[1].setMaxTorqueOnAxis2(currentTorque);
			joints[2].setMaxTorqueOnAxis2(currentTorque2);
			joints[3].setMaxTorqueOnAxis2(currentTorque2);

			joints[0].setMaxTorqueOnAxis1(0);
			joints[1].setMaxTorqueOnAxis1(0);
			joints[2].setMaxTorqueOnAxis1(0);
			joints[3].setMaxTorqueOnAxis1(0);
			//System.out.println("drag/rr currentTorque :"+currentTorque);
			//currentTorque=0;
		}
		else
		{
			//System.out.println("currentTorque: "+currentTorque);
			if (currentTorque < 0f) // just rolling no gas or brake
			{
				currentTorque = FastMath.abs(currentTorque); // air and roll friction

				joints[0].setDesiredAngularVelocityAroundAxis2(0);
				joints[1].setDesiredAngularVelocityAroundAxis2(0);
				joints[2].setDesiredAngularVelocityAroundAxis2(0);
				joints[3].setDesiredAngularVelocityAroundAxis2(0);

				joints[0].setMaxTorqueOnAxis2(currentTorque);
				joints[1].setMaxTorqueOnAxis2(currentTorque);
				joints[2].setMaxTorqueOnAxis2(currentTorque);
				joints[3].setMaxTorqueOnAxis2(currentTorque);

			}
			else
			{
				//BHP = 2*pi*'M*n - RPM*Torque/5252

				//P = RPM(M) * N
				// gas

				//System.out.println("accelerating...");

				if (!fourWheeled)
				{
					if (!frontDriven)
					{
						byte differential = 0; // both wheels get the power
						float epsilon = 1.25f;//2.0f;//0.125f;
/*
                        //offenes differential
                        if (wheels[2].getAngularVelocity().length() > wheels[3].getAngularVelocity().length() + epsilon)
                            differential = 1; // left wheel gets the power
                        else
                            if (wheels[3].getAngularVelocity().length() > wheels[2].getAngularVelocity().length()
                                    + epsilon)
                                differential = 2; // right wheel gets the power
*/
						//System.out.println("diff: "+differential );
						joints[0].setMaxTorqueOnAxis2(0);
						joints[1].setMaxTorqueOnAxis2(0);
						joints[2].setMaxTorqueOnAxis2((differential == 1 || differential == 0) ? currentTorque : 0);
						joints[3].setMaxTorqueOnAxis2((differential == 2 || differential == 0) ? currentTorque : 0);

						joints[0].setMaxTorqueOnAxis1(0);
						joints[1].setMaxTorqueOnAxis1(0);
						joints[2].setMaxTorqueOnAxis1(0);
						joints[3].setMaxTorqueOnAxis1(0);
					}
					else
					{
						byte differential = 0; // both wheels get the power
						float epsilon = 1.25f;
                        /*
                        //offenes differential
                        if (wheels[0].getAngularVelocity().length() > wheels[1].getAngularVelocity().length() + epsilon)
                            differential = 1; // left wheel gets the power
                        else
                            if (wheels[1].getAngularVelocity().length() > wheels[0].getAngularVelocity().length()
                                    + epsilon)
                                differential = 2; // right wheel gets the power
                            */
						//System.out.println("diff: "+differential );
						joints[2].setMaxTorqueOnAxis2(0);
						joints[3].setMaxTorqueOnAxis2(0);
						joints[0].setMaxTorqueOnAxis2((differential == 1 || differential == 0) ? currentTorque : 0);
						joints[1].setMaxTorqueOnAxis2((differential == 2 || differential == 0) ? currentTorque : 0);

						joints[0].setMaxTorqueOnAxis1(0);
						joints[1].setMaxTorqueOnAxis1(0);
						joints[2].setMaxTorqueOnAxis1(0);
						joints[3].setMaxTorqueOnAxis1(0);
					}

				}
				else
				{
					byte differential = 0; // both wheels get the power
					float epsilon = 1.25f;
/*
                    //offenes differential
                    if (wheels[2].getAngularVelocity().length() > wheels[3].getAngularVelocity().length() + epsilon)
                        differential = 1; // left wheel gets the power
                    else
                        if (wheels[3].getAngularVelocity().length() > wheels[2].getAngularVelocity().length()
                                + epsilon)
                            differential = 2; // right wheel gets the power
*/
					joints[0].setMaxTorqueOnAxis2((differential == 1 || differential == 0) ? currentTorque*0.5f : 0);
					joints[1].setMaxTorqueOnAxis2((differential == 2 || differential == 0) ? currentTorque*0.5f : 0);
					joints[2].setMaxTorqueOnAxis2((differential == 1 || differential == 0) ? currentTorque*0.5f : 0);
					joints[3].setMaxTorqueOnAxis2((differential == 2 || differential == 0) ? currentTorque*0.5f : 0);

					joints[0].setMaxTorqueOnAxis1(0);
					joints[1].setMaxTorqueOnAxis1(0);
					joints[2].setMaxTorqueOnAxis1(0);
					joints[3].setMaxTorqueOnAxis1(0);
				}

			}
		}

		//car speed by 2 pi times the wheel radius

		//Vector3f tmp = new Vector3f(this.getWheels()[0].getAngularVelocity());//.getLinearVelocity()) ;
		//System.out.println("front:"+tmp.length()+"/"+tmp.lengthSquared());
		//System.out.println("tyre: "+this.getWheels()[0].getLinearVelocity().length()/(FastMath.PI*2.0f*wheelRadius));
		//System.out.println("car: "+this.chassis.getLinearVelocity().length()/(FastMath.PI*2.0f*wheelRadius));

	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#setGasPedal(float)
	 */
	public void setGasPedal(float percent)
	{
		if(currentGear==0)
			return;

		if (percent > 1)
			percent = 1;
		else
		if (percent < -1)
			percent = -1;

		gasPedal = percent;
		isAccelerating = true;
		isBraking = false;

		float ang = speedFactor * (gearBox.getMaxRPM() / gearBox.getGearRatio(currentGear) / differentialRatio / 60.0f * 2 / FastMath.PI);

		if(fourWheeled)
		{
			joints[0].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
			joints[1].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
			joints[2].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
			joints[3].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
		}
		else
		{
			if(!frontDriven)
			{
				joints[2].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
				joints[3].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
			}
			else
			{
				joints[0].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
				joints[1].setDesiredAngularVelocityAroundAxis2(currentGear == -1 ? -ang : ang);
			}
		}
		//updatePowerOnWheels();
	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#getGasPedal()
	 */
	public float getGasPedal()
	{
		return gasPedal;
	}

	/**percent
	 * @see com.jmex.physics.vehicle.Vehicle#addToClutch(float)
	 */
	public void addToClutch(float percent)
	{
		clutch.addToClutch(percent);
	}

	/**percent
	 * @see com.jmex.physics.vehicle.Vehicle#addToSteeringWheel(float)
	 */
	@Override
	public void addToSteeringWheel(float percent)
	{

		float oldSteeringWheel=steeringWheel;
		float newSteeringWheel=steeringWheel + percent;/// ((1.0f+FastMath.abs(steeringWheel*0.00125f))+0.00125f*chassis.getLinearVelocity().length());
		//float test=steeringWheel + percent;

		//System.out.println("oldSteeringWheel: "+oldSteeringWheel);
		//System.out.println("newSteeringWheel: "+newSteeringWheel);
		//System.out.println("test: "+test);

		if(FastMath.abs(oldSteeringWheel)>FastMath.abs(newSteeringWheel))
			steeringWheel=0f; // fast re-centering of the steering wheel

		setSteeringWheel(newSteeringWheel);
	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#addToGasPedal(float)
	 */
	@Override
	public void addToGasPedal(float percent)
	{
		setGasPedal(gasPedal + percent);
	}

	/**
	 * @see com.jmex.physics.vehicle.Vehicle#addToBrakePedal(float)
	 */
	@Override
	public void addToBrakePedal(float percent)
	{
		setBrakePedal(brakePedal + percent);
	}

	/**
	 * Sets whether or not this <code>Car</code> should be four-wheeled driven or not.
	 *
	 * @param fourWheeled
	 *            True if this <code>Car</code> should be four-wheeled driven.
	 */
	public void setFourWheeled(boolean fourWheeled)
	{
		this.fourWheeled = fourWheeled;
		this.frontDriven = false;
	}

	/**
	 * Returns whether or not this <code>Car</code> is four-wheeled driven.
	 *
	 * @return Whether this <code>Car</code> is four wheeled driven or not.
	 */
	public boolean isFourWheeled()
	{
		return fourWheeled;
	}

	/**
	 * Sets whether or not this <code>Car</code> should be front driven or not.
	 *
	 * @param fourWheeled
	 *            True if this <code>Car</code> should be front driven.
	 */
	public void setFrontDriven(boolean frontDriven)
	{
		this.frontDriven = true;
		this.fourWheeled = false;
	}

	/**
	 * Returns whether or not this <code>Car</code> is front driven.
	 *
	 * @return Whether this <code>Car</code> is front driven or not.
	 */
	public boolean isFrontDriven()
	{
		return frontDriven;
	}
	/**
	 * Sets whether or not the handbrake is on.
	 *
	 * @param handbrake
	 *            True if the handbrake is on.
	 */
	public void setHandBrakeOn(boolean handbrake)
	{
		this.handbrake = handbrake;
		if (handbrake)
			applyHandBrake();
	}

	/**
	 * Returns whether or not the handbake is on.
	 *
	 * @return True if the handbrake is on.
	 */
	public boolean isHandbrakeOn()
	{
		return handbrake;
	}

	/**
	 * Calling this method will attempt to lock the rear wheels.
	 */
	private void applyHandBrake()
	{
		joints[0].setMaxTorqueOnAxis2(brakePower);
		joints[1].setMaxTorqueOnAxis2(brakePower);

		joints[0].setDesiredAngularVelocityAroundAxis2(0);
		joints[1].setDesiredAngularVelocityAroundAxis2(0);
	}


	public float getSkiddingValue(int wheel)
	{
		float diff = FastMath.abs(this.getWheels()[wheel].getAngularVelocity().length()*this.wheelRadius) - (this.getChassis().getLinearVelocity().length());

		return diff;
	}

	public boolean isSkidding(int wheel)
	{
		float diff = FastMath.abs(this.getWheels()[wheel].getAngularVelocity().length()*this.wheelRadius) - (this.getChassis().getLinearVelocity().length()*1.25f);

		if(diff>0.00001f)
			return true;

		return false;
	}
}