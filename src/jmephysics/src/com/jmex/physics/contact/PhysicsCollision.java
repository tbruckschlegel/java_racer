/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the Mojo Monkey Coding, jME,
 * jMonkey Engine, nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * 
 */
package com.jmex.physics.contact;

import com.jmex.physics.PhysicsObject;
import com.jme.scene.Spatial;
import com.jme.math.Vector3f;

/**
 * A class representing a collision between two <code>PhysicsObjects</code>.
 * Its a simple method of storage.
 * 
 * @author Ahmed
 */
public class PhysicsCollision {

	// the two physics objects that are colliding
	PhysicsObject sourcePhysicsObject;

	PhysicsObject targetPhysicsObject;

   //The colliding spatials
   Spatial sourceSpatial;
   Spatial targetSpatial;

	/**
	 * A constructor for <code>PhysicsCollision</code>.
	 * 
	 * @param a
	 * @param b
    * @param sa first Spatial that collided
    * @param sb second Spatial that collided
    * @param actualBounceVelocity the velocity with which the two objects hit (in direction 'into' object 1)
	 */
	public PhysicsCollision(PhysicsObject a, PhysicsObject b, Spatial sa, Spatial sb, Vector3f actualBounceVelocity ) {
		sourcePhysicsObject = a;
		targetPhysicsObject = b;
      sourceSpatial = sa;
      targetSpatial = sb;
      this.actualBounceVelocity = actualBounceVelocity;
	}

	/**
	 * Obtain the <code>PhysicsObject</code> that collided with another
	 * <code>PhysicsObject</code>
	 * 
	 * @return
	 */
	public PhysicsObject getSourcePhysicsObject() {
		return sourcePhysicsObject;
	}

	/**
	 * Obtain the <code>PhysicsObject</code> that has been collided with
	 * 
	 * @return
	 */
	public PhysicsObject getTargetPhysicsObject() {
		return targetPhysicsObject;
	}

   //todo: (to make it reusable setters could be package accessible some time)

   /**
    * @return first spatial that has caused the collision
    */
   public Spatial getSourceSpatial()
   {
      return sourceSpatial;
   }

   /**
    * @return second spatial that has caused the collision
    */
   public Spatial getTargetSpatial()
   {
      return targetSpatial;
   }

   final Vector3f actualBounceVelocity;

   /**
    * @return the velocity with which the two objects hit (in direction 'into' object 1)
    */
   public Vector3f getActualBounceVelocity()
   {
      return actualBounceVelocity;
   }
}
