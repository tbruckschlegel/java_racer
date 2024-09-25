package com.jmex.physics.types;

/**
 * @author Per Thulin
 */
public class PhysicsCone extends PhysicsType {
	/** The radius of this cone. */
	public float radius;
	
	/** The length of this cone. */
	public float length;
	
	public PhysicsCone(float radius, float length) {
		this.radius = radius;
		this.length= length;
	}
	
	/**
	 * @see PhysicsType#getType()
	 */
	public int getType() {
		return PhysicsType.TYPE_CONE;
	}	
}
