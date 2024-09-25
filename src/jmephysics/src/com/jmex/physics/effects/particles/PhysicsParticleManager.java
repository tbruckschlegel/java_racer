/*
 * Copyright (c) 2004-2005, jme-Physics All rights reserved.
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
 * Neither the name of the jme-Physics nor the
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
package com.jmex.physics.effects.particles;

import org.odejava.Geom;

import com.jme.scene.shape.Sphere;
import com.jmex.effects.Particle;
import com.jmex.effects.ParticleManager;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.JmeGeomProperties;
import com.jmex.physics.PhysicsUpdateAction;
import com.jmex.physics.PhysicsWorld;

/**
 * 
 * A <code>PhysicsParticleManager</code> creates DynamicPhysicsObjects out of
 * the particles. However, to make it lighter, the particles dont collide with
 * each other. That is all contact is immedietly cancelled between them.
 * <p>
 * Note: This <code>PhysicsParticleManager</code> is capped to 50 particles.
 * That means that for a graphical particle system of over 50 particles, some
 * particles will miss out on having a physical representation of them.<br>
 * This means that this PhysicsParticleManager is suitable for fire/steam/smoke
 * (tightly packed particles). But its not suitable for rain/snow that tend to
 * be less tightly packed.
 * </p>
 * 
 * @author Ahmed
 * 
 */
public class PhysicsParticleManager extends PhysicsUpdateAction {

	// the particles
	private Particle[] particles;
	private DynamicPhysicsObject[] phyParticles;
	
	private String name;

	/**
	 * Constructor for the <code>PhysicsParticleManager</code>.
	 * <p>
	 * Note: This constructor is heavy weight. That is it takes some time to
	 * finish. So your better off either caching some
	 * <code>PhysicsParticleManager</code>s
	 * </p>
	 * 
	 * @param manager
	 *            the particle manager
	 * @param mass
	 *            the mass of each particle
	 */
	public PhysicsParticleManager(String name, ParticleManager manager, float mass) {
		this.name = name;
		// cap to 50 particles
		if (manager.getParticleArray().length > 50) {
			this.particles = new Particle[50];
			for (int i = 0; i < particles.length; i++) {
				this.particles[i] = manager.getParticleArray()[i];
			}
		} else {
			this.particles = manager.getParticleArray();
		}

		this.phyParticles = new DynamicPhysicsObject[particles.length];

		// find the average size of the particle
		float averageSize = (manager.getStartSize() + manager.getEndSize()) / 2f;

		// create a box, could be a sphere....
		// loop through the dynamic PhysicsObjects
		for (int i = 0; i < phyParticles.length; i++) {
			Sphere sphere = new Sphere("", 8, 8, averageSize);
			sphere.setName(name + " Particle: " + i);
			phyParticles[i] = new DynamicPhysicsObject(sphere, mass);
		}
	}

	/**
	 * Obtain the
	 * <code>DynamicPhysicsObjects<code>s that make up this <code>PhysicsParticleManager</code>.
	 * <p>
	 * Note: this should not be used to modify the objects in any way as the PhysicsWorld needs to be updated too. This is for internal use only.
	 * </p>
	 * @return
	 */
	public DynamicPhysicsObject[] getPhysicsParticles() {
		return phyParticles;
	}
	
	/**
	 * Return the name of this particle manager
	 * @return
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.jmex.physics.PhysicsUpdateAction#beforeStep()
	 */
	public void beforeStep( PhysicsWorld world ) {
		for (int i = 0; i < phyParticles.length; i++) {
			if (particles[i].getStatus() == Particle.ALIVE) {
				phyParticles[i].setEnabled(true);
				phyParticles[i].getSpatial().getLocalTranslation().x = particles[i]
						.getPosition().x;
				phyParticles[i].getSpatial().getLocalTranslation().y = particles[i]
						.getPosition().y;
				phyParticles[i].getSpatial().getLocalTranslation().z = particles[i]
						.getPosition().z;

				phyParticles[i].syncWithGraphical();
			} else {
				phyParticles[i].setEnabled(false);
			}
		}	
	}

	/* (non-Javadoc)
	 * @see com.jmex.physics.PhysicsUpdateAction#afterStep()
	 */
	public void afterStep( PhysicsWorld world ) {
	}
	
	/**
	 * Add this <code>PhysicsParticleManager</code> to the world.
     * @param world
     */
	public boolean addToWorld( PhysicsWorld world ) {
		world.addUpdateAction(this);

		for (int i = 0; i < this.getPhysicsParticles().length; i++) {
			DynamicPhysicsObject obj = this.getPhysicsParticles()[i];
			// loop through its Geoms and set their "isParticle" field to true.
			for (int j = 0; j < obj.getPhysicalEntity().getGeoms().size(); j++) {
				Geom g = (Geom) obj.getPhysicalEntity().getGeoms().get(j);
                Object userObject = g.getUserObject();
                if ( userObject == null )
                {
                    userObject = new JmeGeomProperties();
                    g.setUserObject( userObject );
                }
                if ( userObject instanceof JmeGeomProperties )
                {
                    ((JmeGeomProperties) userObject).isParticle = true;
                }
                else
                {
                    throw new RuntimeException( "Geoms in PhysicsParticleManager must have JmeGeomProperties as user objects!" );
                }
			}
			world.addObject(obj);
		}

		return true;
	}
	
	/**
	 * Removes this <code>PhysicsParticleManager</code> from the scene.
     * @param world
     */
	public boolean removeFromWorld( PhysicsWorld world ) {
		world.removeUpdateAction(this);

		for (int i = 0; i < this.phyParticles.length; i++) {
			world.removeObject(this.phyParticles[i]);
		}

		return true;
	}
	
}
