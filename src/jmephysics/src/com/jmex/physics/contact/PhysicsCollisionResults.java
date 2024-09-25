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
package com.jmex.physics.contact;

import java.util.ArrayList;

import com.jmex.physics.PhysicsObject;
import com.jme.scene.Spatial;
import com.jme.math.Vector3f;

/**
 * After each update of the PhysicsWorld, the world will return a
 * PhysicsCollisionResults which can then be processed as logic of the game
 * requires.
 * <P>
 * Note: This class does not extend com.jme.intersection.CollisionResults and
 * thus an exception will arrise if you cast this class to it.
 * </P>
 * 
 * @author Ahmed
 */
public class PhysicsCollisionResults {

	/** an arraylist containing all the collision data */
	private ArrayList collidingSpatials = new ArrayList();

   private ArrayList collisionObjectsForReuse = new ArrayList();

   /**
	 * Constructor for the PhysicsCollisionResults.
	 */
	public PhysicsCollisionResults() {
	}

	/**
	 * Add a collision between two physics objects to this
	 * PhysicsCollisionResults.
	 * 
	 * @param source
	 * @param target
    * @param actualBounceVelocity the velocity with which the two objects hit (in direction 'into' object 1)
	 */
	public void addCollision(PhysicsObject source, PhysicsObject target,
                            Spatial collidingSpatialSource, Spatial collidingSpatialTarget,
                            Vector3f actualBounceVelocity ) {
      //todo: avoid creation of objects here
		PhysicsCollision data = createOrReuseCollision( source, target, collidingSpatialSource, collidingSpatialTarget,
            actualBounceVelocity );
		collidingSpatials.add(data);
	}

   private PhysicsCollision createOrReuseCollision( PhysicsObject source, PhysicsObject target,
                                                    Spatial collidingSpatialSource, Spatial collidingSpatialTarget,
                                                    Vector3f actualBounceVelocity )
   {
      int reusableObjects = collisionObjectsForReuse.size();
      if ( reusableObjects == 0 )
      {
         return new PhysicsCollision(source, target,
               collidingSpatialSource, collidingSpatialTarget, new Vector3f( actualBounceVelocity ) );
      }
      else
      {
         PhysicsCollision collision = ( PhysicsCollision ) collisionObjectsForReuse.remove( reusableObjects - 1 );
         collision.sourcePhysicsObject = source;
         collision.targetPhysicsObject = target;
         collision.sourceSpatial = collidingSpatialSource;
         collision.targetSpatial = collidingSpatialTarget;
         collision.actualBounceVelocity.set( actualBounceVelocity );
         return collision;
      }
   }


   /**
    * Query if the collisions were computed by the last call of {@link com.jmex.physics.PhysicsWorld#update}. If the
    * collisions were not computed there are no collisions in this resultset (invalid).
    * @return true if the collision results are valid
    */
   public boolean isUpdated()
   {
      return this.updated;
   }

    /**
     * store the value for field updated
     */
    private boolean updated;

    /**
     * setter for field updated
     * @see #isUpdated() 
     * @param value new value
     */
    public void setUpdated( final boolean value )
    {
       final boolean oldValue = this.updated;
       if ( oldValue != value )
       {
          this.updated = value;
       }
    }

	/**
	 * @return the number of collisions that have occured during this step
	 */
	public int getNumberOfCollisions() {
		return collidingSpatials.size();
	}

	/**
	 * Obtain the colliding pair at this position along the results
	 */
	public PhysicsCollision getCollisionData(int i) {
		return (PhysicsCollision) collidingSpatials.get(i);
	}

	/**
	 * Clears the results so that as if no collisions have happened
	 */
	public void clear() {
      for ( int i = collidingSpatials.size() - 1; i >= 0; i-- )
      {
         Object obj = collidingSpatials.get( i );
         collisionObjectsForReuse.add( obj );
      }
      collidingSpatials.clear();
	}

}
