package com.jmex.physics.types;

/**
 * A <code>PhysicsType</code> contains instructions on how to build certein 
 * geometries.
 * 
 * <p>
 * They are used when building DynamicPhysicsObjets which physical behavior you
 * need more control over.
 * </p>
 * 
 * @author Per Thulin
 */
public abstract class PhysicsType {
	public static final int TYPE_BOX = 0;
	public static final int TYPE_SPHERE = 1;
	public static final int TYPE_CYLINDER = 2;
	public static final int TYPE_CONE = 3;
	
	/**
	 * Returns the type of geometry this type represents. It can be any of the
	 * constants located in com.jmex.physics.type.PhysicsType.
	 * 
	 * @return The type of geometry this type represents.
	 */
	public abstract int getType();
}
