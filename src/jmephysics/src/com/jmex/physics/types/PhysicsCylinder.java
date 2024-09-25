package com.jmex.physics.types;

/**
 * @author Per Thulin
 */
public class PhysicsCylinder extends PhysicsType {
	
	/**
	 * The radius of the cylinder. It's the same as
	 * <code>com.jme.scene.shape.Cylinder.getRadius()</code>
	 */
	public float radius;
	
	/** 
	 * The length of the cylinder. It's the same as 
	 * <code>com.jme.scene.shape.Cylinder.getHeight()</code>.
	 */
	public float length;
	
	/**
	 * 
	 * @param radius
	 *            The radius of the cylinder. It's the same as
	 *            <code>com.jme.scene.shape.Cylinder.getRadius()</code>
	 *
	 * @param length
	 *            The length of the cylinder. It's the same as
	 *            <code>com.jme.scene.shape.Cylinder.getHeight()</code>.
	 */
	public PhysicsCylinder(float radius, float length) {
		this.radius = radius;
		this.length = length;
	}
	
	/**
	 * @see PhysicsType#getType()
	 */
	public int getType() {
		return PhysicsType.TYPE_CYLINDER;
	}
}

