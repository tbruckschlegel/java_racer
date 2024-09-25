package com.jmex.physics.types;

/**
 * @author Per Thulin
 */
public class PhysicsSphere extends PhysicsType {
	
	/** The radius of the sphere. */
	public float radius;
	
	/**
	 * 
	 * @param radius The radius of the sphere.
	 */
	public PhysicsSphere(float radius) {
		this.radius = radius;
	}
	
	/**
	 * @see PhysicsType#getType()
	 */
	public int getType() {
		return PhysicsType.TYPE_SPHERE;
	}
}
