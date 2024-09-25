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

import java.util.List;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.types.PhysicsType;
import org.odejava.Body;
import org.odejava.Geom;
import org.odejava.Space;
import com.jmex.physics.PhysicsDisablingController;
/**
 * <p>
 * <code>DynamicPhysicsObject</code> is a <code>PhysicsObject</code> with
 * dynamical properties, such as mass, velocity, applied forces etc.
 * </p>
 * <p>
 * When a dynamic object gets attached to the <code>PhysicsWorld</code>, a
 * <code>PhysicsSynchronizer</code> will be added to it. It makes sure the jME
 * geometry matches the ODE geometrys position and rotation, thus making the
 * simulation come to life.
 * </p>
 * 
 * @author Ahmed Al-Hindawi
 * @author Per Thulin
 * @see com.jmex.physics.PhysicsWorld
 * @see com.jmex.physics.PhysicsObject
 */
public class DynamicPhysicsObject extends PhysicsObject {
	// The mass of this object.
	private float mass;

	private boolean hasContact = false;

	// the ode body which is just a cast from super.phyObject
	private Body body;

	// the disabling
	private PhysicsDisablingController frstmDisable;


	/**
	 * Standard constructor for dynamic objects. The physical entity will try to
	 * mimic the geometry that the graphics extends. If the graphics doesn't
	 * match a Box, Sphere nor Cylinder it's bounding volume will be used.
	 * <p>
	 * NOTE: <b>The <code>PhysicsWorld</code> has to have been created before
	 * you create a <code>PhysicsObject</code> </b>.
	 * 
	 * @param graphics
	 *            The graphical representation.
	 * @param mass
	 *            The mass of the object.
	 */
	public DynamicPhysicsObject(Spatial graphics, float mass) {
        this( PhysicsWorld.getInstance(), graphics, mass );
    }

    /**
     * Standard constructor for dynamic objects. The physical entity will try to
     * mimic the geometry that the graphics extends. If the graphics doesn't
     * match a Box, Sphere nor Cylinder it's bounding volume will be used.
     * <p>
     * NOTE: <b>The <code>PhysicsWorld</code> has to have been created before
     * you create a <code>PhysicsObject</code> </b>.
     *
     * @param world where is dynamic physics object is created
     * @param graphics
     *            The graphical representation.
     * @param mass
     *            The mass of the object.
     */
    public DynamicPhysicsObject(PhysicsWorld world, Spatial graphics, float mass) {
        super(graphics);
        this.mass = mass;
        body = (Body) world.getFactory().createPhysicalEntity(this);
    }

	/**
	 * Use this constructor if you want the physical entity to mimic another
	 * Geometry than what the Spatial extends. I.e. this can be useful for such
	 * objects as car wheels. The wheel graphics consists of a TriMesh, and its
	 * bounding volume extends Sphere, but you want it to be physically treated
	 * as a Cylinder. Then you should pass the TriMesh as the graphics, and a
	 * Cylinder as the physics.
	 * <p>
	 * NOTE: <b>The <code>PhysicsWorld</code> has to have been created before
	 * you create a <code>PhysicsObject</code> </b>.
	 * 
	 * @param graphics
	 *            The graphical representation.
	 * @param physics
	 *            The physical representation. Should be one of the classes in
	 *            com.jmex.physics.types.
	 * @param mass
	 *            The mass of the object.
	 */
	public DynamicPhysicsObject(Spatial graphics, PhysicsType physics, float mass) {
        this( PhysicsWorld.getInstance(), graphics, physics, mass );
    }

    /**
     * Use this constructor if you want the physical entity to mimic another
     * Geometry than what the Spatial extends. I.e. this can be useful for such
     * objects as car wheels. The wheel graphics consists of a TriMesh, and its
     * bounding volume extends Sphere, but you want it to be physically treated
     * as a Cylinder. Then you should pass the TriMesh as the graphics, and a
     * Cylinder as the physics.
     * <p>
     * NOTE: <b>The <code>PhysicsWorld</code> has to have been created before
     * you create a <code>PhysicsObject</code> </b>.
     *
     * @param world where is dynamic physics object is created
     * @param graphics
     *            The graphical representation.
     * @param physics
     *            The physical representation. Should be one of the classes in
     *            com.jmex.physics.types.
     * @param mass
     *            The mass of the object.
     */
    public DynamicPhysicsObject(PhysicsWorld world, Spatial graphics, PhysicsType physics, float mass) {
        super(graphics);
        this.mass = mass;
        body = (Body) world.getFactory().createPhysicalEntity(this, physics);
    }

	/* non-javadoc - copied from superclass */

	public boolean isStatic() {
		return false;
	}
	/**
	 * Set the finiteRotationAxis to a new value, using a vector.
	 *
	 * @param finiteRotationAxis A vector holding the finiteRotationAxis
	 */
	public void setFiniteRotationAxis(Vector3f finiteRotationAxis) {
		body.setFiniteRotationAxis(finiteRotationAxis);
	}

	/**
	 * Set the finiteRotationAxis to a new value, using individual values.
	 *
	 * @param x The x component of the the rotation axis
	 * @param y The y component of the the rotation axis
	 * @param z The z component of the the rotation axis
	 */
	public void setFiniteRotationAxis(float x, float y, float z) {
		body.setFiniteRotationAxis(x, y, z);
	}

	public void setFiniteRotationMode(int mode) {
		body.setFiniteRotationMode(mode);
	}

	/**
	 * This method should be called after having manually changed the
	 * rotation/translation of the jME Geometry.
     * Note that changing scale after creation of the physics object is not synchronoized!
	 */
	public void syncWithGraphical() {
		jmeObject.updateWorldVectors();
        body.setEnabled( true );
        body.setPosition(jmeObject.getWorldTranslation());
		body.setQuaternion(jmeObject.getWorldRotation());
	}

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
	public boolean isSynced() {
		Quaternion qP = body.getQuaternion();
		Vector3f vP = body.getPosition();

		Quaternion qG = jmeObject.getWorldRotation();
		Vector3f vG = jmeObject.getWorldTranslation();

		return (qP.x == qG.x && qP.y == qG.y && qP.z == qG.z && qP.w == qG.w)
				&& (vP.x == vG.x && vP.y == vG.y && vP.z == vG.z);
	}

	/**
	 * Get the physical entity represented by this
	 * <code>DynamicPhysicsObject</code>
	 * 
	 * @return
	 */
	public Body getPhysicalEntity() {
		return body;
	}

	/**
	 * Returns the mass of the object.
	 * 
	 * @return
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * Set the mass of this object.
	 * 
	 * @param mass
	 */
	public void setMass(float mass) {
		this.mass = mass;
		body.adjustMass(mass);
	}

	/**
	 * Adds a given force to this object.
	 * 
	 * @param force
	 *            The direction of the force. Should be normalized.
	 */
	public void addForce(Vector3f force) {
		body.addForce(force);
        body.setEnabled( true );
	}

    /**
     * Sets a given force to this object.
     * 
     * @param force
     *              The direction of the force. Should be normalized.
     */
    public void setForce(Vector3f force) {
        body.setForce(force);
    }
    
    /**
     * Sets a given force to this object.
     * 
     * @param x
     *              The x force to be applied.
     * @param y
     *              The y force to be applied.
     * @param z
     *              The z force to be applied.
     */
    public void setForce(float x, float y, float z) {
        body.setForce(x, y, z);
    }
    
	/**
	 * Obtain the current force on a body.
	 * 
	 * @return new vector
	 */
	public Vector3f getForce() {
		return body.getForce();
	}

    /**
     * Obtain the current force on a body.
     * @param store where to put the force
     * @return store
     */
    public Vector3f getForce( Vector3f store ) {
        return body.getForce( store );
    }

	/**
	 * Add a force in which the direction of it is in relative space to the
	 * object
	 * 
	 * @param force
	 */
	public void addRelativeForce(Vector3f force) {
		body.addRelForce(force);
        body.setEnabled( true );
	}

	/**
	 * Adds a torque to this object.
	 * 
	 * @param torque
	 *            The torque to be added.
	 */
	public void addTorque(Vector3f torque) {
		body.addTorque(torque);
        body.setEnabled( true );
	}

	/**
	 * Sets the torque of this object.
	 * 
	 * @param torque
	 */
	public void setTorque(Vector3f torque) {
		body.setTorque(torque);
	}
    
    /**
     * Sets the torque of this object.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setTorque(float x, float y, float z) {
        body.setTorque(x, y, z);
    }

	/**
	 * Gets the torque of this object.
	 * 
	 * @return
	 */
	public Vector3f getTorque() {
		Vector3f torque = body.getTorque();
		return torque;
	}

	/**
	 * A memory-usage optimized way of getting to the torque. The passed in
	 * Vector3f will be populated with the values, and then returned.
	 * 
	 * @param store
	 * @return
	 */
	public Vector3f getTorque(Vector3f store) {
		return body.getTorque(store);
	}

	/**
	 * Adds a torque onto that object in relative space.
	 * 
	 * @param vec
	 */
	public void addRelativeTorque(Vector3f vec) {
		body.addRelTorque(vec);
        body.setEnabled( true );
	}

	/**
	 * Sets the linear velocity of this object.
	 * 
	 * @param velocity
	 */
	public void setLinearVelocity(Vector3f velocity) {
		body.setLinearVel(velocity);
        body.setEnabled( true );
	}

    /**
     * Sets the linear velocity of this object.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setLinearVelocity(float x, float y, float z) {
        body.setLinearVel(x, y, z);
        body.setEnabled(true);
    }
    
	/**
	 * Gets the linear velocity of this object.
	 * 
	 * @return
	 */
	public Vector3f getLinearVelocity() {
		return body.getLinearVel();
	}

	/**
	 * A memory-usage optimized way of getting to the linear velocity. The
	 * passed in Vector3f will be populated with the values, and then returned.
	 * 
	 * @param store
	 * @return
	 */
	public Vector3f getLinearVelocity(Vector3f store) {
		body.getLinearVel(store);
        return store;
	}

	/**
	 * Sets the angular velocity of this object.
	 * 
	 * @param velocity
	 */
	public void setAngularVelocity(Vector3f velocity) {
		body.setAngularVel(velocity);
        body.setEnabled( true );
	}

    /**
     * Sets the angular velocity of this object.
     * 
     * @param x
     * @param y
     * @param z
     */
    public void setAngularVelocity(float x, float y, float z) {
        body.setAngularVel(x, y, z);
        body.setEnabled(true);
    }
    
	/**
	 * Gets the angular velocity of this object.
	 * 
	 * @return
	 */
	public Vector3f getAngularVelocity() {
		return body.getAngularVel();
	}

	/**
	 * A memory-usage optimized way of getting to the angular velocity. The
	 * passed in Vector3f will be populated with the values, and then returned.
	 * 
	 * @param store
	 * @return
	 */
	public Vector3f getAngularVelocity(Vector3f store) {
		return body.getAngularVel(store);
	}

	/**
	 * Change the mode of how gravity affects this geometry. A value of 1 will
	 * tell the dynamicEntity to obey the world's local gravity, a value of 0
	 * will ignore it.
	 * 
	 * @param mode
	 *            A value of 1 for global gravity, 0 for no gravity.
	 */
	public void setGravityMode(int mode) {
		body.setGravityMode(mode);
	}

	/**
	 * Get the current mode describing how global gravity will effect this
	 * dynamicEntity. A value of 1 indicates gravity will effect it, a value of
	 * zero indicates it will not be effected by gravity.
	 * 
	 * @return A value of 0 or 1.
	 */
	public int getGravityMode() {
		return body.getGravityMode();
	}

	/**
	 * True if this object should interact with other physics objects.
	 * 
	 * @param enabled
	 *            Whether or not the physics system should affect it.
	 */
	public void setEnabled(boolean enabled) {
		body.setEnabled(enabled);

		// disable geoms, too, to prevent them from causing collisions
		final List geoms = body.getGeoms();
		for (int i = geoms.size() - 1; i >= 0; i--) {
			Geom geom = (Geom) geoms.get(i);
			geom.setEnabled(enabled);
		}
	}

	/**
	 * True if this object interacts with other physics objects.
	 * 
	 * @return Whether or not the physics system affects it.
	 */
	public boolean isEnabled() {
		return body.isEnabled();
	}

	/**
	 * Set whether to enable frustum collision disabling. True will enable the
	 * disabling mechanism.
	 *
	 * @param disable
	 */
	public void setFrustumDisable(boolean disable) {
		if (frstmDisable == null && disable == true) {
			frstmDisable = new PhysicsDisablingController(this);
			frstmDisable.setActive(false);
			jmeObject.addController(frstmDisable);
		}
		if (frstmDisable != null) {
			frstmDisable.setActive(disable);
		}
	}

	/**
	 * Get whether frustum disabling is enabled
	 *
	 * @return
	 */
	public boolean getFrustumDisable() {
		return frstmDisable != null && frstmDisable.isActive();
	}

    /**
     * Resets all force, tourque and velocities.
     * @deprecated bad name - use {@link #resetDynamics()} instead
     */
    public void resetForces() {
        body.resetDynamics();
    }

    /**
     * Resets all force, tourque and velocities.
     */
    public void resetDynamics() {
        body.resetDynamics();
    }

	public void addForceAtRelativePosition(Vector3f force,
			Vector3f localTranslation) {
		body.addForceAtRelPos(force.x, force.y, force.z, localTranslation.x,
				localTranslation.y, localTranslation.z);
        body.setEnabled( true );
	}

    public void addRelativeForceAtRelativePosition( Vector3f force,
                                                    Vector3f localTranslation ) {
        body.addRelForceAtRelPos( force.x, force.y, force.z, localTranslation.x,
                localTranslation.y, localTranslation.z );
        body.setEnabled( true );
    }

    public void removeFromSimulation( PhysicsWorld physicsWorld ) {
        Space space = physicsWorld.getOdeJavaSpace();
        final List geoms = body.getGeoms();
        for (int i = geoms.size() - 1; i >= 0; i--) {
            Geom geom = (Geom) geoms.get(i);
            space.remove( geom );
        }
    }

    public void addToSimulation( PhysicsWorld physicsWorld ) {
        Space space = physicsWorld.getOdeJavaSpace();
        final List geoms = body.getGeoms();
        for (int i = geoms.size() - 1; i >= 0; i--) {
            Geom geom = (Geom) geoms.get(i);
            space.add( geom );
        }
    }

	// object collsion info, esp. for wheels
	public boolean getContact()
	{
		return hasContact;
	}

	public void setContact(boolean contact)
	{
		hasContact=contact;
	}

}