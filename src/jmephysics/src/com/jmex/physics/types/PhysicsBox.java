package com.jmex.physics.types;

/**
 * @author Per Thulin
 */
public class PhysicsBox extends PhysicsType {
	
	/** The dimensions of this box. */
	public float xLength, yLength, zLength;
	
	/**
	 * 
	 * @param xLength
	 * @param zLength
	 * @param yLength
	 */
	public PhysicsBox(float xLength, float zLength, float yLength) {
		this.xLength = xLength;
		this.yLength = yLength;
		this.zLength = zLength;
	}
	
	/**
	 * @see PhysicsType#getType()
	 */
	public int getType() {
		return PhysicsType.TYPE_BOX;
	}
}
