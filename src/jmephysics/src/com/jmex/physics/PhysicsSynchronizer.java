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

import java.util.ArrayList;

import org.odejava.Body;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;

import com.jme.scene.Spatial;

/**
 * <code>PhysicsSynchronizer</code> is a <code>PhysicsUpdateAction</code> that 
 * maintains a list of <code>DynamicPhysicsObject</code>s. It makes sure that
 * the graphical and physical representation of each object is in sync.
 * 
 * @author Per Thulin
 */
class PhysicsSynchronizer extends PhysicsUpdateAction {

	/** Contains the <code>DynamicPhysicsObject</code>s to synchronize. */
	private ArrayList objects;
	
	/** Temp variables to flatline memory usage. */
	private static final Vector3f odePos = new Vector3f();
	
	/** Temp variables to flatline memory usage. */
	private static final Quaternion odeRot = new Quaternion();
	
	/** Temp variables to flatline memory usage. */
	private static final Quaternion inverseWorldRotation = new Quaternion();

	/**
	 * Constructor.
	 */
	public PhysicsSynchronizer() {
		objects = new ArrayList();
	}
	
	/**
	 * Sets the world translation of a spatial according to a supplied body.
	 * This is done by going "the other way around"; figuring
	 * out what its local translation has to be in order for the world
	 * translation to be the same as the one of the body.
	 * 
	 * @param spat The Spatial to synchronize.
     */
	private void setWorldTranslation( Spatial spat ) {
		Vector3f tmpPos = spat.getLocalTranslation();
		if (spat.getParent() != null) {
			tmpPos.set(odePos).subtractLocal(spat.getParent().getWorldTranslation());
			tmpPos.divideLocal(spat.getParent().getWorldScale());
			inverseWorldRotation.set(spat.getParent().getWorldRotation()).inverseLocal()
					.multLocal(tmpPos);
		} else {
			tmpPos.set(odePos);
		}
	}
	
	/**
	 * Sets the world rotation of a spatial according to a supplied body.
	 * This is done by going "the other way around"; figuring
	 * out what its local rotation has to be in order for the world
	 * rotation to be the same as the one of the body.
	 * 
	 * @param spat The Spatial to synchronize.
     */
	private void setWorldRotation( Spatial spat ) {
		Quaternion tmpQuat = spat.getLocalRotation();
		if (spat.getParent() != null) {
			inverseWorldRotation.set(spat.getParent().getWorldRotation()).inverseLocal().mult(
					odeRot, tmpQuat);
		} else {
			tmpQuat.set(odeRot);
		}
	}
	
	/**
	 * Adds an object to synchronize. This method is called from the PhysicsWorld
	 * when an object is added.
	 * 
	 * @param obj
	 */
	public void addObject(DynamicPhysicsObject obj) {
		objects.add(obj);
	}
	
	/**
	 * Removes an object to synchronize. This method is called from the PhysicsWorld
	 * when an object is removed.
	 * 
	 * @param obj
	 */
	public void removeObject(DynamicPhysicsObject obj) {
		objects.remove(obj);
	}
	
	/**
	 * Removes all objects from the list.
	 */
	public void removeAllObjects() {
		objects.clear();
	}

	/* (non-Javadoc)
	 * @see com.jmex.physics.PhysicsUpdateAction#beforeStep()
	 */
	public void beforeStep( PhysicsWorld world ) {
	}
	
	/* (non-Javadoc)
	 * @see com.jmex.physics.PhysicsUpdateAction#afterStep()
	 */
	public void afterStep( PhysicsWorld world ) {
	}

	/* (non-Javadoc)
	 * @see com.jmex.physics.PhysicsUpdateAction#afterStep()
	 */
	public void afterUpdate( PhysicsWorld world ) {
		for (int i = objects.size() - 1; i >= 0; i--) {
			DynamicPhysicsObject obj = (DynamicPhysicsObject) objects.get(i);
			Body odeEntity = obj.getPhysicalEntity();
			Spatial jmeEntity = obj.getSpatial();
			
			odeEntity.getPosition(odePos);
			odeEntity.getQuaternion(odeRot);
		
			setWorldTranslation(jmeEntity );
			setWorldRotation(jmeEntity );
		}
	}
	
}