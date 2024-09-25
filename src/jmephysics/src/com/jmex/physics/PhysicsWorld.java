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
package com.jmex.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;
import com.jmex.physics.contact.PhysicsCallBack;
import com.jmex.physics.contact.PhysicsCollisionResults;
import org.odejava.Body;
import org.odejava.Geom;
import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.Placeable;
import org.odejava.Space;
import org.odejava.World;
import org.odejava.collision.Contact;
import org.odejava.collision.JavaCollision;
import org.odejava.ode.Ode;

/**
 * <p>
 * <code>PhysicsWorld</code> is what you add objects with physical properties
 * to. In order to do that, you need to create a <code>PhysicsObject</code>.
 * </p>
 * <p>
 * To create the <code>PhysicsWorld</code> you need to call
 * <code>PhysicsWorld.create()</code> (once only). Then it's enough calling
 * <code>PhysicsWorld.getInstance()</code> everytime you need a reference to
 * it.
 * </p>
 * <p>
 * In order to step the simulation forward you need to call update() from your
 * game loop. Look at the <code>SimpleTest</code> for a basic example on how
 * to do all this.
 * </p>
 * <p>
 * There are three different ways of updating the world: QUICK, FAST and
 * SIMULATION. SIMULATION is the slowest but most accurate one - QUICK and FAST
 * trades accuracy for speed. QUICK is the default one. You set them by calling:
 * </p>
 * <code>PhysicsWorld.setStepFunction(PhysicsWorld.SF_STEP_SIMULATION)</code>,
 * <br>
 * <code>PhysicsWorld.setStepFunction(PhysicsWorld.SF_STEP_QUICK)</code>,
 * <br>
 * <code>PhysicsWorld.setStepFunction(PhysicsWorld.SF_STEP_FAST)</code>,<br>
 * <p>
 * Look at the <code>StepFunctionTest</code> on how to do this.
 * </p>
 * 
 * @author Ahmed Al-Hindawi
 * @author Nicki de Bruyn
 * @author Per Thulin
 */
public class PhysicsWorld {

    /**
     * This is an ODE-specific method and may be removed (or moved) if other dynamics implementations are used
     * @return the ODEJava World object
     */
    public World getODEJavaWorld() {
        return world;
    }

    /** The slowest but most accurate step function */
	public static final int SF_STEP_SIMULATION = 0;

	/** A faster but lees accurate alternative */
	public static final int SF_STEP_FAST = 2;

	/** The step function that has superseded SF_STEP_FAST */
	public static final int SF_STEP_QUICK = 1;

	// Signals which one of the above that is to be used.
	private int stepFunction;

	// The singleton instance.
	private static PhysicsWorld instance;

	// The space to add bodys to.
	private World world;

	// The space to add geometry to.
	private Space space;

	// collision system
	private JavaCollision collision;

	// helper class to read/write collisional data
	private Contact contact;

	// A PhysicsCollisionResults which is returned after
	// every update call
	private PhysicsCollisionResults results;

	// Keeps track of all the PhysicsObjects in this world.
	private ArrayList physicsObjects;

	// Varables used to govern the frequency of update calls.
	private float updateRate;

	private float elapsedTime;

	private PhysicsCallBack callBack;
	
	/** User-defined actions to perform each update. */
	private ArrayList updateActions;
	
	/** Ensures that graphics and physics stays synced. */
	private PhysicsSynchronizer synchronizer;

	private Vector3f tmp1 = new Vector3f();
	private Vector3f tmp2 = new Vector3f();
	private Vector3f tmpBounceVel = new Vector3f();

   /**
    * store value for field factory
    */
   private PhysicsEntityFactory factory;

   /**
    * @return current value of the field factory
    */
   public PhysicsEntityFactory getFactory()
   {
      return this.factory;
   }

   /**
    * @param value new value for field factory
    * @return true if factory was changed
    */
   boolean setFactory( final PhysicsEntityFactory value )
   {
      final PhysicsEntityFactory oldValue = this.factory;
      boolean changed = false;
      if ( oldValue != value )
      {
         if ( oldValue != null )
         {
            this.factory = null;
            oldValue.setPhysicsWorld( null );
         }
         this.factory = value;
         if ( value != null )
         {
            value.setPhysicsWorld( this );
         }
         changed = true;
      }
      return changed;
   }

   /**
	 * Initialises the world, space and gravity. Sets some default values: <br>
	 * gravity = Earths <br>
	 * step interactions = 15 <br>
	 * step size = 0.02 <br>
	 * update rate = 0 (update as fast as possible) <br>
	 * step function = SF_STEP_QUICK
	 */
	public PhysicsWorld() {
		// initialise ODE
		Odejava.getInstance();

		// Create the space which we add geometry to.
		space = new HashSpace();

        // Create the ODE world which we'll add bodys to.
		world = new World();
		world.setAutoDisableBodies(true);

		// Set the default gravity.
		world.setGravity(0, -9.81f, 0);

		// According to the odejava doc, setting this will remove
		// some sort of weirdness that would otherwise occur...
		world.setContactSurfaceThickness(0.001f);

		// Set the default step interactions.
		world.setStepInteractions(15);

		// Set a default step size.
		world.setStepSize(0.02f);

        // Set up the factory that will create physics entitys.
		setFactory( new PhysicsEntityFactory( world ) );

		// Set the default step function.
		stepFunction = SF_STEP_QUICK;

		// use collisions
		collision = new JavaCollision(world);

		// create the DirectBuffer
		contact = new Contact(collision.getContactIntBuffer(), collision.getContactFloatBuffer());

		// create the results
		results = new PhysicsCollisionResults();

		// create the collision pair list
		physicsObjects = new ArrayList();

		updateActions = new ArrayList( 2 );
		
		// Setup the synchronizer.
		synchronizer = new PhysicsSynchronizer();
		addUpdateAction(synchronizer);
		
		// Default to no UPS restriction.
		updateRate = -1;
		elapsedTime = 0;
	}

	/**
	 * Creates a new <code>PhysicsWorld</code>. If one already exists, it
	 * returns the existing one. Some standard values are set: </br>
	 * gravity = Earths</br>
	 * step interactions = 15</br>
	 * step size = 0.02</br>
	 * update rate = 0 (update as fast as possible)</br>
	 * step function = SF_STEP_QUICK</br>
	 * 
	 * @return The singleton.
	 */
	public static PhysicsWorld create() {
		if (instance == null) {
			instance = new PhysicsWorld();
			LoggingSystem.getLogger().log(Level.INFO, "Created Physics World");
		}
		return instance;
	}

	/**
	 * Forces a return of the singleton instance.
	 * 
	 * @return The singleton.
	 */
	public static PhysicsWorld getInstance() {
      return instance;
	}

	/**
	 * Adds a <code>PhysicsObject</code> to the simulation.
	 * 
	 * @param obj
	 *            The object to add.
	 * @return If the object was added or not. It won't be if is already
	 *         attached.
	 */
	public boolean addObject(PhysicsObject obj) {

		if (physicsObjects.contains(obj)) {
			return false;
		}

		// log some stuff:
		LoggingSystem.getLogger().log(Level.INFO,
				"Physics Object (" + obj.getName() + ") has been added to the PhysicsWorld");

		// add it to the arraylist
		physicsObjects.add(obj);

		// see if its static or not and do the necessary
		if (obj.isStatic()) {
			StaticPhysicsObject sObj = (StaticPhysicsObject) obj;
			ArrayList list = sObj.getPhysicalEntities();
			for (int i = 0; i < list.size(); i++) {
				Geom g = (Geom) sObj.getPhysicalEntities().get(i);
				space.addGeom(g);
			}
		} else {
			DynamicPhysicsObject dObj = (DynamicPhysicsObject) obj;
			space.addBodyGeoms(dObj.getPhysicalEntity());
			synchronizer.addObject(dObj);
			// set its index
			final List geoms = (dObj.getPhysicalEntity()).getGeoms();
			for (int i = geoms.size() - 1; i >= 0; i--) {
				Geom geom = (Geom) geoms.get(i);
				geom.setPhysicsObject( obj );
			}

		}

		obj.syncWithGraphical();
		
		return true;
	}

	/**
	 * Removes a <code>PhysicsObject</code> from the simulation.
	 * 
	 * @param obj
	 *            The object to remove.
	 * @return true if object was removed, false if not found
	 */
	public boolean removeObject(PhysicsObject obj) {

		if (!physicsObjects.remove(obj)) {
			return false;
		}

		if (obj.isStatic()) {
			StaticPhysicsObject sObj = (StaticPhysicsObject) obj;
			List list = sObj.getPhysicalEntities();
			for (int j = list.size() - 1; j >= 0; j--) {
				space.remove((Geom) list.get(j));
			}
		} else {
			DynamicPhysicsObject dObj = (DynamicPhysicsObject) obj;
			synchronizer.removeObject(dObj);
			Placeable physicalEntity = dObj.getPhysicalEntity();
			Body body = (Body) physicalEntity;
			world.deleteBody(body);
		}

		// print out a statement
		LoggingSystem
				.getLogger()
				.log(
						Level.INFO,
						"PhysicsObject ("
								+ obj.getName()
								+ ") has been removed from PhysicsWorld and will no longer take place in the simulation");

		return true;
	}

	/**
	 * Returns whether or not this world contains a given
	 * <code>PhysicsObject</code>.
	 * 
	 * @param obj
     * @return true if object is contained in world, false if not found
	 */
	public boolean containsObject(PhysicsObject obj) {
		return physicsObjects.contains(obj);
	}

	/**
	 * Returns the list of objects this world maintains. Note that the returned
	 * list is not a copy, so handle carefully :)
	 * 
	 * @return The list of maintained objects.
	 */
	public ArrayList getObjects() {
		return physicsObjects;
	}
	
	/**
	 * Returns how many <code>PhysicsObject</code>s this world contains.
	 * 
	 * @return number of PhysicsObjects
	 */
	public int getNumberOfObjects() {
		return physicsObjects.size();
	}

	/**
	 * Set how much to step by each time.
	 * 
	 * @param stepSize
	 *            The step size to set. Default is 0.02.
	 */
	public void setStepSize(float stepSize) {
		world.setStepSize(stepSize);
		LoggingSystem.getLogger().log(Level.INFO,
				"Setting StepSize of PhysicsWorld to : " + stepSize);
	}

	/**
	 * Obtains how far to step by each update call.
	 * 
	 * @return The current step size. Default is 0.02.
	 */
	public float getStepSize() {
		return world.getStepSize();
	}

	/**
	 * Sets how many updates per second that is wanted. Default is running as
	 * fast as possible (i.e. -1).
	 * 
	 * @param ups
	 *            The updaterate. Note: cannot be 0.
	 */
	public void setUpdateRate(int ups) {
		// updateRate = timer.getResolution() / ups;
		updateRate = 1.0f / ups;
		LoggingSystem.getLogger().log(Level.INFO,
				"Changed the number of updates in a second of PhysicsWorld to: " + ups);
	}

	/**
	 * Returns how many updates per second we run at. Default is running as fast
	 * as possible (-1).
	 * 
	 * @return The updaterate. Default is -1.
	 */
	public int getUpdateRate() {
		// return (int) (updateRate * timer.getResolution());
		return (int) (1.0f / updateRate);
	}

	/**
	 * Runs the update method of this physics World for x number of times with
	 * an update. This is used to "warm up" and let the physical objects settle
	 * down and not let the user see the objects falling down due to gravity.
	 * Obviously if the scene depends on gravity, this should not be called.
	 * 
	 * @param iterations
	 *            The number of iterations to warm up.
	 */
	public void warmUp(int iterations) {
		for (int i = 0; i < iterations; i++) {
			// collide objects in a given space
			collision.collide(space);

			// read and modify contact information
			iterateContacts();

			// apply the contact information for the collisions
			collision.applyContacts();

			// See what step function to use and then step through the world.
            if ( stepFunction == SF_STEP_QUICK ) {
                world.quickStep();
            }
            else if ( stepFunction == SF_STEP_FAST ) {
                world.stepFast();
            }
            else if ( stepFunction == SF_STEP_SIMULATION ) {
                world.step();
            }
		}

		LoggingSystem.getLogger().log(Level.INFO,
				"PhysicsWorld has warmed up the objects " + iterations + "time(s)");
	}

	/**
	 * Steps the world forward. Perform all calculations and detect collisions.
	 * Should be called every frame.
	 * <p>
	 * Note, that if you have set an update rate, this method will check with
	 * the timer if it should do any logic. If not, it escapes. If the time
	 * elapsed is greater than the update interval it computes more than one
	 * step.
	 * </p>
	 * 
	 * @param tpf
	 *            time that has elapsed since the last call of update
	 * @return PhysicsCollisionResults containing all PhysicsCollisions that
	 *         occured while updating
	 */
	public PhysicsCollisionResults update(float tpf) {
		if (tpf > 1 || Float.isNaN(tpf)) {
			if (updateRate > 0) {
				LoggingSystem.getLogger().warning("Maximum update interval is 1 second - capped.");
			}
			tpf = 1;
		}
		if (tpf < 0) {
			tpf = 0;
		}
		elapsedTime += tpf;

		// clear the results
		results.clear();
        boolean updated = false;
        
        // Perform all the update actions.
        for (int i = updateActions.size() - 1; i >= 0; i--) {
			((PhysicsUpdateAction)updateActions.get(i)).beforeUpdate( this );
		}
        
		while (shouldUpdate()) {
            updated = true;

            // Perform all the update actions.
            for (int i = updateActions.size() - 1; i >= 0; i--) {
    			((PhysicsUpdateAction)updateActions.get(i)).beforeStep( this );
    		}
           
			// collide objects in a given space
			collision.collide(space);

			// read and modify contact information
			iterateContacts();

			// apply the contact information for the collisions
			collision.applyContacts();

			// See what step function to use and then step through the world.
            if ( stepFunction == SF_STEP_QUICK ) {
                world.quickStep();
            }
            else if ( stepFunction == SF_STEP_FAST ) {
                world.stepFast();
            }
            else if ( stepFunction == SF_STEP_SIMULATION ) {
                world.step();
            }
			
            // Perform all the update actions. 
			for (int i = updateActions.size() - 1; i >= 0; i--) {
				((PhysicsUpdateAction)updateActions.get(i)).afterStep( this );
			}

			// Reset the "timer".
			if (updateRate > 0) {
				elapsedTime -= updateRate;
			} else {
				elapsedTime = 0;
				break;
			}
		}

        results.setUpdated( updated );
		
        // Perform all the update actions.
        for (int i = updateActions.size() - 1; i >= 0; i--) {
			((PhysicsUpdateAction)updateActions.get(i)).afterUpdate( this );
		}

		return results;
	}

    /**
     * @return same collection of collisions as returned by update
     */
    public PhysicsCollisionResults getCollisionResults() {
        return results;
    }

    /**
	 * Returns true if it is time to update according to the preferred update
	 * rate. It takes the previous time update and substracts it from the
	 * current time. This obtains the difference in update calls. If this
	 * difference is larger than the preferred time or equal to it, then return
	 * true.
	 * 
	 * @return Whether it's time to update or not.
	 */
	private boolean shouldUpdate() {
		// long thisTime = timer.getTime();

		// return (thisTime - lastTime) >= updateRate;
		return (elapsedTime >= updateRate);
	}

	/**
	 * Loop through the collisions that have occured and set the contact
	 * information. Eg. Bounce factor.
	 */
	private void iterateContacts() {
		PhysicsObject previous1 = null;
		PhysicsObject previous2 = null;
		for (int i = 0; i < collision.getContactCount(); i++) {
			// set the index
			contact.setIndex(i);

            final Geom geom1 = contact.getGeom1();
            PhysicsObject obj1 = geom1.getPhysicsObject();
            final Geom geom2 = contact.getGeom2();
            PhysicsObject obj2 = geom2.getPhysicsObject();

			// cancel the contact if both of the geoms are particles
			if ( isParticle( geom1 ) && isParticle( geom2 ) ) {
				contact.ignoreContact();
			} else {
				contact.setMu(-1);
				if (callBack != null) {
					callBack.onContact(obj1, obj2, contact);
					if (contact.getMu() == -1 && contact.getGeomID1() != 0 ) {
						callBack.defaultContact(contact);
					}
				} else {
					contact.setMode(Ode.dContactBounce | Ode.dContactApprox1);
					contact.setBounce(0.4f);
					contact.setBounceVel(1f);
					contact.setMu(100f);
				}

				// if contact was ignored don't put it into results
				if (contact.getGeomID1() != 0 || contact.getGeomID2() != 0) {
					// disctinct by equals() not by name
					if ( ( !obj1.equals( previous1 ) && !obj2.equals( previous2 ) )
                            || (!(obj1.equals(previous1)) && obj2.equals(previous2))
                            || (obj1.equals(previous1) && !(obj2.equals(previous2)) ) ) {
						// compute collision velocity (must be done before
						// returning contact)
						Vector3f actualBounceVel = tmpBounceVel.set(0, 0, 0);
						if (!obj1.isStatic()) {
							actualBounceVel.subtractLocal(((DynamicPhysicsObject) obj1)
									.getLinearVelocity(tmp1));
						}
						if (!obj2.isStatic()) {
							actualBounceVel.addLocal(((DynamicPhysicsObject) obj2)
									.getLinearVelocity(tmp1));
						}
						actualBounceVel.multLocal(contact.getNormal(tmp2));
						results.addCollision(obj1, obj2, geom1.getGraphics()
                                , geom2.getGraphics(), actualBounceVel);
					}
				}
			}
		}
	}

    private boolean isParticle(final Geom geom) {
        final Object userObject = geom.getUserObject();
        if ( userObject instanceof JmeGeomProperties )
        {
            return ((JmeGeomProperties) userObject).isParticle;
        }
        else
        {
            return false;
        }
    }

    /**
	 * Clean up the system. Should be called before ending your application.
	 */
	public void cleanup() {
		space.delete();
		collision.delete();
		world.delete();
		Ode.dCloseODE();
		instance = null;
	}

	/**
	 * Earths gravity = (0, -9.82, 0) (Sweden) Earths gravity = (0, -9.81, 0)
	 * (UK and the rest of the world! :P)
	 * 
	 * @param gravity
	 *            The gravity vector.
	 */
	public void setGravity(Vector3f gravity) {
		world.setGravity(gravity.x, gravity.y, gravity.z);

		LoggingSystem.getLogger().log(
				Level.INFO,
				"Setting Gravity of PhysicsWorld to: (" + gravity.x + ", " + gravity.y + ", "
						+ gravity.z + ")");
	}

	/**
	 * Gets the gravity of this world.
	 * 
	 * @return The current gravity.
	 */
	public com.jme.math.Vector3f getGravity() {
		return getGravity(new Vector3f());
	}

	/**
	 * A memory-usage optimized way to get to the gravity. It fills the passed
	 * Vector3f with the gravity values.
	 * 
	 * @param store
	 *            The Vector3f to fill with values.
	 * @return The Vector3f sent as parameter.
	 */
	public Vector3f getGravity(Vector3f store) {
		return world.getGravity(store);
	}

	/**
	 * Number of interactions, higher gives better accuracy but lower speed in
	 * case of many simultaneous collisions. Default is 15.
	 * 
	 * @param stepInteractions
	 *            The stepInteractions to set.
	 */
	public void setStepInteractions(int stepInteractions) {
		world.setStepInteractions(stepInteractions);
		LoggingSystem.getLogger().log(Level.INFO,
				"Setting the number of interactions of PhysicsWorld to: " + stepInteractions);
	}

	/**
	 * Gets the stepInteractions of this world.
	 * 
	 * @return The stepInteractions of this world.
	 */
	public int getStepInteractions() {
		return world.getStepInteractions();
	}

	/**
	 * Sets the method which we use to update the world. It can be one of the
	 * following: - SF_STEP_SIMULATION (The slowest but most accurate step
	 * function) - SF_STEP_FAST (A faster but lees accurate alternative) -
	 * SF_STEP_QUICK (The step function that has superseded SF_STEP_FAST)
	 * Default is SF_STEP_QUICK.
	 * 
	 * @param stepFunction
	 *            The stepFunction to set.
	 */
	public void setStepFunction(int stepFunction) {
		this.stepFunction = stepFunction;

		if (stepFunction == SF_STEP_FAST) {
			LoggingSystem.getLogger().log(Level.INFO, "Setting the Physics Solver to use StepFast");
		} else if (stepFunction == SF_STEP_QUICK) {
			LoggingSystem.getLogger()
					.log(Level.INFO, "Setting the Physics Solver to use StepQuick");
		} else {
			LoggingSystem.getLogger().log(Level.INFO,
					"Setting the Physics Solver to use Simulation");
		}

	}

	/**
	 * Gets the method which we use to update the world.
	 * 
	 * @return The current step function being used.
	 */
	public int getStepFunction() {
		return stepFunction;
	}

	/**
	 * Removes all PhysicsObjects in the world. Could be useful for
	 * level-switching and such.
	 */
	public void removeAllObjects() {
		List list = (List) physicsObjects.clone();
		for (int i = 0; i < list.size(); i++) {
			removeObject((PhysicsObject) list.get(i));
		}
	}

	/**
	 * @param callBack
	 *            The callBack to set.
	 */
	public void setPhysicsCallBack(PhysicsCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * @return Returns the callBack.
	 */
	public PhysicsCallBack getPhysicsCallBack() {
		return callBack;
	}
	
	/**
	 * Adds a <code>PhysicsUpdateAction</code> to perform each physics update.
	 * 
	 * @param updateAction
	 */
	public void addUpdateAction(PhysicsUpdateAction updateAction) {
        if ( !updateActions.contains( updateAction ) )
        {
		    updateActions.add(updateAction);
        }
	}
	
	/**
	 * Removes a <code>PhysicsUpdateAction</code> from the list.
	 * 
	 * @param updateAction
	 */
	public void removeUpdateAction(PhysicsUpdateAction updateAction) {
		updateActions.remove(updateAction);
	}
	
	/**
	 * Removes all <code>PhysicsUpdateAction</code>s.
	 */
	public void removeAllUpdateActions() {
		updateActions.clear();
	}

    public Space getOdeJavaSpace() {
        return space;
    }
}