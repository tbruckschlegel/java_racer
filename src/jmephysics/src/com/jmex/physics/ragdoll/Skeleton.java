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

import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsWorld;
import com.jmex.physics.joints.Joint;

/**
 * <code>Skeleton</code> is what all types of ragdolls descend from. It contains
 * a collection of limbs, and another one with joints connecting them. Both 
 * these should be defined by derived classes.
 * 
 * @author Per Thulin
 */
public abstract class Skeleton {

	/** The limbs of the ragdoll (legs, arms etc). */
	protected DynamicPhysicsObject[] limbs;
	
	/** The joints connecting them. */
	protected Joint[] joints;
	
	/**
	 * Returns all the limbs of this Skeleton.
	 * 
	 * @return All the limbs of this Skeleton.
	 * @see com.jmex.physics.ragdoll.Skeleton#addToWorld()
	 */
	public DynamicPhysicsObject[] getLimbs() {
		return limbs;
	}
	
	/**
	 * Detaches a given joint. Could be useful for damage simulation and such.
	 * E.g. you could let the ContactAction of a <code>Humanoid</code>'s head detach
	 * the neck (joint nr 0), when in contact with a bullet... >:D
	 * 
	 * @param jointNr The joint nr. Specific for each derived class.
	 */
	public void detachJoint(int jointNr) {
		joints[jointNr].delete();
	}
	
	/**
	 * Just a convenient way to add all the limbs of this ragdoll to the
	 * <code>PhysicsWorld</code>.
	 *
	 * @see com.jmex.physics.ragdoll.Skeleton#getLimbs()
	 */
	public void addToWorld() {
		for (int i = 0; i < limbs.length; i++) {
			PhysicsWorld.getInstance().addObject(limbs[i]);	
		}
	}
	
}
