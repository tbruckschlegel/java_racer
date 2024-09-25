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
package com.jmex.physics.effects;

import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsObject;
import com.jmex.physics.PhysicsWorld;

/**
 * <code>ExplosionManager</code> contains a static method to simulate an explosion with a given
 * <b>force</b> and <b>radius</b> at a given <b>coordinate</b>.
 * 
 * @author Per Thulin
 */
public class ExplosionManager {

	/** Temp variable to flatline memory usage. */
	private static final Vector3f distance = new Vector3f();
	
	/** Temp variable to flatline memory usage. */
	private static final Vector3f direction = new Vector3f();
	
	/** Temp variable to flatline memory usage. */
	private static final Vector3f forceToApply = new Vector3f();
	
	/**
	 * A static method to simulate an explosion with a given
	 * <b>force</b> and <b>radius</b> at a given <b>coordinate</b>.
	 * 
	 * @param position
	 *     The explosion centre.
	 * @param force 
	 *     The force of which an object right in the explosion centre
	 *     will be affected by. The applied force attenuates relative
	 *     to the distance between the object and the explosion centre.
	 * @param radius
	 *     The explosion radius. Objects outside this radius will 
	 *     not get affected.
	 */
	public static void createExplosion(Vector3f position, float force, float radius) {
		// Loop through all the objects in the physics world and apply an
		// explosion force.
		ArrayList objects = PhysicsWorld.getInstance().getObjects();		
		for (int i = objects.size() - 1; i >= 0; i--) {
			PhysicsObject obj = (PhysicsObject) objects.get(i);
			
			// Escape 1: if the object is static.
			if (obj.isStatic())
				continue;
			
			// Calculate the distance between the object and the explosion centre. 
			obj.getSpatial().getWorldTranslation().subtract(position, distance);
			
			// Calculate the direction vector between the explosion centre
			// and the object.
			direction.set(distance);
			direction.normalizeLocal();
			
			distance.x = FastMath.abs(distance.x);
			distance.y = FastMath.abs(distance.y);
			distance.z = FastMath.abs(distance.z);
			
			// Escape 2: if the object is outside of the explosion radius. Maybe
			// this is a little unnecessary, but will save computations in a
			// scene with many objects spread out.
			if (distance.x > radius || distance.y > radius || distance.z > radius)
				continue;
			
			// Calculate the force to apply. The force should attenuate
			// relative to the distance between the object and the
			// explosion centre.
			forceToApply.x = (1 - (distance.x/radius)) * force;
			forceToApply.y = (1 - (distance.y/radius)) * force;
			forceToApply.z = (1 - (distance.z/radius)) * force;
			forceToApply.multLocal(direction);
			
			// Apply the force.
			((DynamicPhysicsObject) obj).addForce(forceToApply);
		}
	}
	
}
