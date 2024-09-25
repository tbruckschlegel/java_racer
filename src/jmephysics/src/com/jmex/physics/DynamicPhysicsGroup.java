package com.jmex.physics;

import java.util.*;

import com.jme.math.*;

/**
 * <p>
 * <code>DynamicPhysicsGroup</code> is a wrapper class for DynamicPhysicsObjects
 * to allow forces to be applied to an entire grouping of objects at a time.
 * </p>
 * <p>
 * This is not necessary to add to a scene as the underlying DynamicPhysicsObjects
 * should be added to the scene and this directly works with them.
 * </p>
 * 
 * @author Matthew D. Hicks
 * @see com.jmex.physics.DynamicPhysicsObject
 */
public class DynamicPhysicsGroup {
    // The DynamicPhysicsObjects attached to this group
    private ArrayList children;
    
    public DynamicPhysicsGroup() {
        children = new ArrayList();
    }

    /**
     * Add a DynamicPhysicsObject to this group
     * 
     * @param dpo
     *      The DynamicPhysicsObject to add
     */
    public void addObject(DynamicPhysicsObject dpo) {
        children.add(dpo);
    }
    
    /**
     * Calls syncWithGraphical() on all children of this group
     */
    public void syncWithGraphical() {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.syncWithGraphical();
        }
    }
    
    /**
     * @return true if and ONLY if all of the underlying
     * DynamicPhysicsObjects are synced.
     */
    public boolean isSynced() {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            if ( !dpo.isSynced() ) {
                return false;
            }
        }
        return true;
    }
    
    public void addForce(Vector3f force) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.addForce(force);
        }
    }
    
    public void addRelativeForce(Vector3f force) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.addRelativeForce(force);
        }
    }
    
    public void addTorque(Vector3f torque) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.addTorque(torque);
        }
    }
    
    public void setTorque(Vector3f torque) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.setTorque(torque);
        }
    }
    
    public void addRelativeTorque(Vector3f torque) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.addRelativeTorque(torque);
        }
    }
    
    public void setLinearVelocity(Vector3f velocity) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.setLinearVelocity(velocity);
        }
    }
    
    public void setAngularVelocity(Vector3f velocity) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.setAngularVelocity(velocity);
        }
    }
    
    public void setGravityMode(int mode) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.setGravityMode(mode);
        }
    }
    
    public void setEnabled(boolean enabled) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.setEnabled(enabled);
        }
    }
    
    public void resetForces() {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.resetDynamics();
        }
    }
    
    public void addForceAtRelativePosition(Vector3f force, Vector3f localTranslation) {
        DynamicPhysicsObject dpo;
        for (int i = 0; i < children.size(); i++) {
            dpo = (DynamicPhysicsObject)children.get(i);
            dpo.addForceAtRelativePosition(force, localTranslation);
        }
    }
}
