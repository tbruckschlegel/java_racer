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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import org.odejava.Geom;
import org.odejava.PlaceableGeom;
import org.odejava.Space;

/**
 * <p>
 * A <code>StaticPhysicsObject</code> does not contain any dynamical
 * properties, e.g. mass or velocity. It can be considered as a blockage to
 * other physics objects. It does not respond to any forces. Hence, it's static
 * in the world. Good for such a thing as terrain.
 * </p>
 * 
 * @author Ahmed Al-Hindawi
 * @author Per Thulin
 * @see com.jmex.physics.PhysicsWorld
 * @see com.jmex.physics.DynamicPhysicsObject
 */
public class StaticPhysicsObject extends PhysicsObject {

	private ArrayList geoms;

	/**
	 * Constructor for the <code>StaticPhysicsObject</code>. It will create
	 * the object and store it. To add it to the current simulation, you call
	 * <code>PhysicsWorld.addObject(PhysicsObject)</code> after creation
	 * 
	 * @param graphics
	 *            The graphical representation.
	 */
	public StaticPhysicsObject(Spatial graphics) {
		this( PhysicsWorld.getInstance(), graphics );
	}

    /**
     * Constructor for the <code>StaticPhysicsObject</code>. It will create
     * the object and store it. To add it to the current simulation, you call
     * <code>PhysicsWorld.addObject(PhysicsObject)</code> after creation
     *
     * @param physicsWorld world to create the object in
     * @param graphics
     *            The graphical representation.
     */
    public StaticPhysicsObject( PhysicsWorld physicsWorld, Spatial graphics ) {
        super(graphics);
        geoms = physicsWorld.getFactory().createEntites(this);
    }

	/**
	 * @return the list of Physical Entities represented by this object.
	 *
	 */
	public ArrayList getPhysicalEntities() {
		return geoms;
	}

	/**
	 * This method should be called after having manually changed the
	 * rotation/translation of the jME Geometry.
     * Note that changing scale after creation of the physics object is not synchronoized!
	 */
	public void syncWithGraphical() {
		// loop through the staticPhysicsObjects
		for (int i = geoms.size() - 1; i >= 0; i--) {
			PlaceableGeom phyObject = (PlaceableGeom)geoms.get(i);
			Spatial spat = phyObject.getGraphics();
			spat.updateWorldVectors();
			phyObject.setPosition(spat.getWorldTranslation());
			phyObject.setQuaternion(spat.getWorldRotation());
		}
	}

	/**
	 * @return whether or not this object is static. In this case, it will
	 * always return static
	 */
	public boolean isStatic() {
		return true;
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
		boolean isSycned = true;
		for (int i = geoms.size() - 1; i >= 0; i--) {
			PlaceableGeom phyObject = (PlaceableGeom) geoms.get(i);
			Quaternion qP = phyObject.getQuaternion();
			Vector3f vP = phyObject.getPosition();
			
			Spatial spat = phyObject.getGraphics();
			
			Quaternion qG = spat.getWorldRotation();
			Vector3f vG = spat.getWorldTranslation();

			isSycned = (qP.x == qG.x && qP.y == qG.y && qP.z == qG.z && qP.w == qG.w)
					&& (vP.x == vG.x && vP.y == vG.y && vP.z == vG.z);

			if ( !isSycned ) {
				break;
			}
		}

		return isSycned;
	}

    public void removeFromSimulation( PhysicsWorld physicsWorld ) {

        Space space = physicsWorld.getOdeJavaSpace();
        for (int i = geoms.size() - 1; i >= 0; i--) {
            Geom geom = (Geom) geoms.get(i);
            space.remove( geom );
        }
    }

    public void addToSimulation( PhysicsWorld physicsWorld ) {
        Space space = physicsWorld.getOdeJavaSpace();
        for (int i = geoms.size() - 1; i >= 0; i--) {
            Geom geom = (Geom) geoms.get(i);
            space.add( geom );
        }
    }

}