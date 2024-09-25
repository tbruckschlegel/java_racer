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
package com.jmex.physics.ragdoll;

import com.jme.system.JmeException;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.joints.Joint;
import com.jmex.physics.ragdoll.Skeleton;

/**
 * A <code>Humanoid</code> contains 11 limbs connected through 10 joints.
 * 
 * @author Per Thulin
 */
public class Humanoid extends Skeleton {
	
	/**
	 * <p>
	 * 0 = head<br>
	 * 1 = torso<br>
	 * 2 = pelvis<br>
	 * </p>
	 * 
	 * <p>
	 * 3 = right upper arm<br>
	 * 4 = right forearm<br>
	 * </p>
	 * 
	 * <p>
	 * 5 = left upper arm<br>
	 * 6 = left forearm<br>
	 * </p>
	 * 
	 * <p>
	 * 7 = right thigh<br>
	 * 8 = right shinbone<br>
	 * </p>
	 * 
	 * <p>
	 * 9 = left thigh<br>
	 * 10 = left shinbone<br>
	 * </p>
	 */
	public Humanoid(DynamicPhysicsObject[] bones) {
		if (bones.length != 11) {
			throw new JmeException("Humanoid must have 11 limbs!");
		}
		
		this.limbs = bones;
		initJoints();
	}
	
	/**
	 * <p>
	 * Initialises the Joints connecting the limbs together.
	 * </p>
	 * 
	 * <p>
	 * 0 = neck (BallJoint)<br>
	 * 1 = waist (BallJoint)<br>
	 * </p>
	 * 
	 * <p>
	 * 2 = right shoulder (BallJoint)<br>
	 * 3 = right elbow (BallJoint)<br>
	 * </p>
	 * 
	 * <p>
	 * 4 = left shoulder (BallJoint)<br>
	 * 5 = left elbow (BallJoint)<br>
	 * </p>
	 * 
	 * <p>
	 * 6 = pelvis <-> right femur (BallJoint)<br>
	 * 7 = right knee (HingeJoint)<br>
	 * </p>
	 * 
	 * <p>
	 * 8 = pelvis <-> left femur (BallJoint)<br>
	 * 9 = left knee (HingeJoint)<br>
	 */
	private void initJoints() {
		joints = new Joint[10];
	}
	
}
