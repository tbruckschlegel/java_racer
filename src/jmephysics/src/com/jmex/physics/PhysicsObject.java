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
package com.jmex.physics;

import com.jme.scene.Spatial;

/**
 * <p>
 * A <code>PhysicsObject</code> is a wrapper around a jME <code>Spatial</code>
 * (the graphical representation) and its physical ODE corresponding.
 * </p>
 * <p>
 * If a <code>Node</code> is passed into the constructor, it will use its
 * world bound. And if an arbitrary mesh is supplied which ODE doesn't know of,
 * it will use its bound which is either a Sphere, Box or an OrientedBoundingBox
 * </p>
 * <p>
 * NOTE: <b>The <code>PhysicsWorld</code> has to have been created before you
 * create a <code>PhysicsObject</code> </b>.
 * </p>
 * <p>
 * To add a <code>PhysicsObject</code> to the <code>PhysicsWorld</code>,
 * simply call <code>PhysicsWorld.addObject(PhysicsObject)</code>.
 * </p>
 * <p>
 * To define special actions that should get performed when in contact with
 * another <code>PhysicsObject</code>, you can set a
 * <code>ContactAction</code> using the
 * <code>PhysicsObject.setContactAction(ContactAction)</code> method. When
 * created, a default <code>ContactAction</code> will be set.
 * </p>
 * <p>
 * The user should never use this class. They should use the subclasses instead
 * </p>
 * 
 * @author Ahmed Al-Hindawi
 * @author Per Thulin
 * @see com.jmex.physics.StaticPhysicsObject
 * @see com.jmex.physics.DynamicPhysicsObject
 */
public abstract class PhysicsObject {

	/** The graphical part of this PhysicsObject. */
	final protected Spatial jmeObject;

	/**
	 * Sets up the default <code>ContactAction</code>. Derived classes will
	 * have to call <code>PhysicsWorld.createPhysicalEntity</code> themselves,
	 * because <code>DynamicPhysicsObject</code> will have to set up it's mass
	 * before making the call.
	 * 
	 * @param graphics
	 *            The graphical representation.
	 */
	protected PhysicsObject(Spatial graphics) {
        if ( graphics == null )
        {
            throw new NullPointerException("graphics may not be null");
        }
		jmeObject = graphics;
	}

	/**
	 * Returns the graphical geometry that is associated with this
	 * <code>PhysicsObject</code>.
	 * 
	 * @return
	 */
	final public Spatial getSpatial() {
		return jmeObject;
	}

	/**
	 * Returns the name of this object (the name of the graphical and physical
	 * geometry).
	 * 
	 * @return
	 */
	public String getName() {
		return jmeObject.getName();
	}

	/**
	 * Returns a string comprising of the objects name and its class whether its
	 * dynamic or static
	 */
	public String toString() {
		String dynamicObj = "com.jmex.physics.DynamicPhysicsObject";
		String staticObj = "com.jmex.physics.StaticPhysicsObject";

		if (isStatic()) {
			return jmeObject.getName() + " (" + staticObj + ")";
		} else {
			return jmeObject.getName() + " (" + dynamicObj + ")";
		}
	}

	/**
	 * Returns whether or not this object is static.
	 * 
	 * @return
	 */
	public abstract boolean isStatic();

	/**
	 * <b>This method should be called after having manually changed the
	 * rotation/translation of the jME Geometry. </b>
     * Note that changing scale after creation of the physics object is not synchronoized!
	 */
	public abstract void syncWithGraphical();
	
	/**
	 * Returns whether or not the graphical representation has the same rotation
	 * and position as its physical counterpart.
	 * <p>
	 * It should be noted that this method creates a
	 * <code>com.jme.math.Vector3f</code>, and a
	 * <code>com.jme.math.Quaternion</code>, so it's not something you want
	 * to be called from your game loop or anything like that.
	 * 
	 * @return Whether or not the graphical representation is synchronized with
	 *         its physical counterpart.
	 */
	public abstract boolean isSynced();

    public abstract void removeFromSimulation( PhysicsWorld physicsWorld );

    public abstract void addToSimulation( PhysicsWorld physicsWorld );
}