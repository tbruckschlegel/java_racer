/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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
package com.jme.math;

import java.util.ArrayList;

/**
 * <code>SpringSystem</code> is a set of springs and nodes that
 * act and update as a cohesive unit.
 * @author Joshua Slack
 * @version $Id: SpringSystem.java,v 1.1 2004/11/29 21:24:57 renanse Exp $
 */
public class SpringSystem {
	/** Array of SpringNodes in this system. */
	protected ArrayList nodes = new ArrayList();
	/** Array of Springs in this system. */
	protected ArrayList springs = new ArrayList();
	/** Array of external forces to apply to this system. */
	private ArrayList externalForces = new ArrayList();
	/** Number of times to update the Springs per system update.  Default is 2 */
	private int relaxLoops = 2;


	/**
	 * Public constructor useful for creating custom SpringSystems.
	 */
	public SpringSystem() {
	}

	/**
	 * Grab a specific node from this system.
	 * @param index int
	 * @return SpringNode
	 */
	public SpringNode getNode(int index) {
		return (SpringNode)nodes.get(index);
	}

	/**
	 * Return the number of nodes in this system.
	 * @return int
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * Add a SpringNode to this system.
	 * @param node SpringNode
	 */
	public void addNode(SpringNode node) {
		nodes.add(node);
	}

	/**
	 * Remove a given SpringNode from this system.
	 * @param node SpringNode
	 * @return true if SpringNode is found and removed.
	 */
	public boolean removeNode(SpringNode node) {
		return nodes.remove(node);
	}

	/**
	 * Grab a specific spring from this system.
	 * @param index int
	 * @return Spring
	 */
	public Spring getSpring(int index) {
		return (Spring)springs.get(index);
	}

	/**
	 * Return the number of springs in this system.
	 * @return int
	 */
	public int getSpringCount() {
		return springs.size();
	}

	/**
	 * Create and add a Spring to this system given two nodes.
	 * @param node1 SpringNode
	 * @param node2 SpringNode
	 */
	public void addSpring(SpringNode node1, SpringNode node2) {
		Spring s = new Spring(node1, node2, node1.position.distance(node2.position));
		springs.add(s);
	}

	/**
	 * Add a Spring to this system.
	 * @param spring Spring
	 */
	public void addSpring(Spring spring) {
		springs.add(spring);
	}

	/**
	 * Remove a given Spring from this system.
	 * @param spring Spring
	 * @return true if Spring is found and removed.
	 */
	public boolean removeSpring(Spring spring) {
		return springs.remove(spring);
	}

	/**
	 * Set how many times the springs are updated per SpringSystem update.
	 * More updates results in tighter more accurate springs at the cost of
	 * speed.
	 * @param relaxLoops int should be at least 2 for stability.
	 */
	public void setRelaxLoops(int relaxLoops) {
		this.relaxLoops = relaxLoops;
	}

	/**
	 * Return how many times each spring is updated per SpringSystem update.
	 * @return int
	 */
	public int getRelaxLoops() {
		return relaxLoops;
	}

	/**
	 * Add an external force to this system.
	 * @param force SpringNodeForce
	 */
	public void addForce(SpringNodeForce force) {
		externalForces.add(force);
	}

	/**
	 * Remove a force from this system.
	 * @param force SpringNodeForce
	 * @return true if found and removed.
	 */
	public boolean removeForce(SpringNodeForce force) {
		return externalForces.remove(force);
	}

	/**
	 * Convienence method for creating a rectangular system of springs and nodes.
	 * This system will contain approximately 4 structural, 4 shear and 4 bend
	 * springs per node (except for edge and corner nodes)
	 * @param width number of nodes wide
	 * @param height number of nodes high
	 * @param verts a precreated array of vertices.  These should be setup
	 * from left to right, top to bottom.
	 * @param normals a precreated array to use as normals.  Should be ordered
	 * the same as the verts array.
	 * @param particleMass a mass applied to each individual node.  Total system
	 * mass would equals particleMass x width x height. (i.e. particale mass x
	 * getNodeCount())
	 * @return The newly created SpringSystem
	 */
	public static SpringSystem createRectField(int width, int height,
											   Vector3f[] verts,
											   Vector3f[] normals,
											   float particleMass) {
		SpringSystem system = new SpringSystem();

		for (int i = 0; i < verts.length; i++) {
			SpringNode node = new SpringNode(verts[i], normals[i]);
			node.index = i;
			node.setMass(particleMass);
			system.nodes.add(node);
		}

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				SpringNode node = (SpringNode)system.nodes.get(j * width + i);

				// attach structural springs...  one down and one right
				if (i < width - 1)
					system.addSpring(node, (SpringNode)system.nodes.get(j * width + (i + 1)));
				if (j < height - 1)
					system.addSpring(node, (SpringNode)system.nodes.get((j + 1) * width + i));

				// attach more structural springs...  one up and one left
				if (i > 0)
					system.addSpring(node, (SpringNode)system.nodes.get(j * width + (i - 1)));
				if (j > 0)
					system.addSpring(node, (SpringNode)system.nodes.get((j - 1) * width + i));

					// attach shear springs...  4 diagonals
				if (i < width - 1 && j > 0)
					system.addSpring(node, (SpringNode)system.nodes.get((j - 1) * width + (i + 1)));
				if (i < width - 1 && j < height - 1)
					system.addSpring(node, (SpringNode)system.nodes.get((j + 1) * width + (i + 1)));

				if (i > 0 && j > 0)
					system.addSpring(node, (SpringNode)system.nodes.get((j - 1) * width + (i - 1)));
				if (i > 0 && j < height - 1)
					system.addSpring(node, (SpringNode)system.nodes.get((j + 1) * width + (i - 1)));

				// attach bend springs...  two spaces in each direction.
				if (i < width - 2)
					system.addSpring(node, (SpringNode)system.nodes.get(j * width + (i + 2)));
				if (j < height - 2)
					system.addSpring(node, (SpringNode)system.nodes.get((j + 2) * width + i));
				if (i > 1)
					system.addSpring(node, (SpringNode)system.nodes.get(j * width + (i - 2)));
				if (j > 1)
					system.addSpring(node, (SpringNode)system.nodes.get((j - 2) * width + i));
			}
		}
//		System.err.println("nodes: " + system.nodes.size());
//		System.err.println("springs: " + system.springs.size());
		return system;
	}

	/**
	 * Calculate all external forces to be applied on the system nodes.
	 * This should be done before a call to update.
	 * @param dt change in time since last call to this function in ms.
	 */
	public void calcForces(float dt) {
		for (int x = 0, nSize = nodes.size(); x < nSize; x++) {
			SpringNode node = (SpringNode)nodes.get(x);
			node.acceleration.zero();

			// apply external forces
			for (int y = externalForces.size(); --y >= 0; ) {
				SpringNodeForce force = (SpringNodeForce) externalForces.get(y);
				if (force.isEnabled()) {
					force.apply(dt, node);
				}
			}
		}
	}

	/**
	 * Update the SpringNodes and Springs in this System.  Update the Springs
	 * multiple times as defined by setRelaxLoops() (default is 2)
	 * @param dt float
	 */
	public void update(float dt) {
		for (int x = 0, nSize = nodes.size(); x < nSize; x++) {
			SpringNode node = (SpringNode)nodes.get(x);
			node.update(dt);
		}
		for (int x = 0; x < relaxLoops; x++) {
			for (int i = 0, sSize = springs.size(); i < sSize; i++) {
				Spring spring = (Spring)springs.get(i);
				spring.update();
			}
		}
	}

}
