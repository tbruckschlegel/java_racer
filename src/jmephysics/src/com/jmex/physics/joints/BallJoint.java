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

import org.odejava.JointBall;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * @author Ahmed
 */
public class BallJoint extends Joint {

	/**
	 * Create the joint and allocate the memory. The joint is not put into
	 * action until a call to "attach" is made.
	 * 
	 * @param obj1
	 * @param obj2
	 */
	public BallJoint(String name, DynamicPhysicsObject obj1, DynamicPhysicsObject obj2) {
		super(name, JT_BALL, obj1, obj2);
	}

	/**
	 * Set the anchor point. Ie. the point where the ball and socket lies
	 * 
	 * @param anchor
	 */
	public void setAnchor(Vector3f anchor) {
		((JointBall) odeJoint).setAnchor(anchor);
	}

	/**
	 * Returns the anchor point set relative to object 1 at any given time.
	 * Because the objects move, hence the need for two seperate calls to
	 * getAnchor as the point becomes relative to the bodies as they move.
	 * 
	 * @return
	 */
	public Vector3f getAnchorRelativeToObj1() {
		return ((JointBall) odeJoint).getAnchor();
	}

	/**
	 * Returns the anchor point set relative to object 2 at any given time.
	 * Because the objects move, hence the need for two seperate calls to
	 * getAnchor as the point becomes relative to the bodies as they move.
	 * 
	 * @return
	 */
	public Vector3f getAnchorRelativeToObj2() {
		return ((JointBall) odeJoint).getAnchor2();
	}

}