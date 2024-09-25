/*
 * Created on Dec 12, 2005
 */
package com.jmex.physics.effects;

import java.lang.ref.*;
import java.util.*;

import com.jme.math.*;
import com.jmex.physics.*;
import com.jmex.physics.contact.*;

/**
 * @author Matthew D. Hicks
 */
public class RollingFriction extends PhysicsUpdateAction {
    private static RollingFriction instance;
    
    private Vector3f tmpAngular;
    private ArrayList objectFriction;
    private ArrayList collisionFriction;
    
    private RollingFriction() {
        tmpAngular = new Vector3f();
        objectFriction = new ArrayList();
        collisionFriction = new ArrayList();
    }
    
    public void add(PhysicsObject obj1, PhysicsObject obj2, float coefficient) {
        final Float coefficientObject = new Float(coefficient);
        // FIX ME: what about objects without names / with all the same name?
        //objectFriction.put(obj1.getName() + ":" + obj2.getName(), coefficientObject );
        //objectFriction.put(obj2.getName() + ":" + obj1.getName(), coefficientObject );
        objectFriction.add(new Object[] {new WeakReference(obj1), new WeakReference(obj2), coefficientObject});
    }
    
    public void add(PhysicsObject obj, float coefficient) {
        final Float coefficientObject = new Float(coefficient);
        collisionFriction.add(new Object[] {new WeakReference(obj), coefficientObject});
    }
    
    public void beforeStep(PhysicsWorld world) {
    }

    public void afterStep(PhysicsWorld world) {
        PhysicsCollision c;
        Float coefficient;
        for (int i = 0; i < world.getCollisionResults().getNumberOfCollisions(); i++) {
            c = world.getCollisionResults().getCollisionData(i);
            //coefficient = (Float)objectFriction.get(c.getSourcePhysicsObject().getName() + ":" + c.getTargetPhysicsObject().getName());
            coefficient = getCoefficient(c.getSourcePhysicsObject(), c.getTargetPhysicsObject());
            if (coefficient != null) {
                float coefficientValue = coefficient.floatValue();
                if (c.getSourcePhysicsObject() instanceof DynamicPhysicsObject) {
                    computeNewAngularVelocity((DynamicPhysicsObject)c.getSourcePhysicsObject(), coefficientValue);
                }
                if (c.getTargetPhysicsObject() instanceof DynamicPhysicsObject) {
                    computeNewAngularVelocity((DynamicPhysicsObject)c.getTargetPhysicsObject(), coefficientValue);
                }
            }
            coefficient = getCoefficient(c.getSourcePhysicsObject());
            if ((coefficient != null) && (c.getSourcePhysicsObject() instanceof DynamicPhysicsObject)) {
                computeNewAngularVelocity((DynamicPhysicsObject)c.getSourcePhysicsObject(), coefficient.floatValue());
            }
            coefficient = getCoefficient(c.getTargetPhysicsObject());
            if ((coefficient != null) && (c.getTargetPhysicsObject() instanceof DynamicPhysicsObject)) {
                computeNewAngularVelocity((DynamicPhysicsObject)c.getTargetPhysicsObject(), coefficient.floatValue());
            }
        }
    }

    private Float getCoefficient(PhysicsObject obj1, PhysicsObject obj2) {
        Object[] objects;
        PhysicsObject temp1;
        PhysicsObject temp2;
        for (int i = 0; i < objectFriction.size(); i++) {
            objects = (Object[])objectFriction.get(i);
            temp1 = (PhysicsObject)((WeakReference)objects[0]).get();
            temp2 = (PhysicsObject)((WeakReference)objects[1]).get();
            if ((temp1 == obj1) && (temp2 == obj2)) {
                return (Float)objects[2];
            } else if ((temp2 == obj1) && (temp1 == obj2)) {
                return (Float)objects[2];
            }
        }
        return null;
    }
    
    private Float getCoefficient(PhysicsObject obj) {
        Object[] objects;
        PhysicsObject temp;
        for (int i = 0; i < collisionFriction.size(); i++) {
            objects = (Object[])collisionFriction.get(i);
            temp = (PhysicsObject)((WeakReference)objects[0]).get();
            if (temp == obj) {
                return (Float)objects[1];
            }
        }
        return null;
    }
    
    private void computeNewAngularVelocity(DynamicPhysicsObject object, float coefficientValue) {
        final Vector3f angular = object.getAngularVelocity(tmpAngular);
        float ax = Math.abs(angular.x);
        if (ax < coefficientValue) {
            ax = 0.0f;
        } else {
            ax -= coefficientValue;
        }
        if (angular.x < 0.0f) {
            ax = -ax;
        }

        float ay = Math.abs(angular.y);
        if (ay < coefficientValue) {
            ay = 0.0f;
        } else {
            ay -= coefficientValue;
        }
        if (angular.y < 0.0f) {
            ay = -ay;
        }

        float az = Math.abs(angular.z);
        if (az < coefficientValue) {
            az = 0.0f;
        } else {
            az -= coefficientValue;
        }
        if (angular.z < 0.0f) {
            az = -az;
        }
        object.setAngularVelocity(ax, ay, az);
    }

    public static RollingFriction getInstance() {
        if (instance == null) {
            instance = new RollingFriction();
        }
        return instance;
    }
}
