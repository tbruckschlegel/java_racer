/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
package com.jmex.physics.effects;

import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsUpdateAction;
import com.jmex.physics.PhysicsWorld;

/**
 * <p>
 * <code>Attractor</code> is used to attract objects to a given
 * point in space.
 * </p>
 * <p>
 * Usage:</br>
 * 1) construct an <code>Attractor</code></br>
 * 2) add objects to it through {@link #addObject(DynamicPhysicsObject)}</br>
 * 3) add it to the world through {@link PhysicsWorld#addUpdateAction(PhysicsUpdateAction)}
 * </p>
 * See jmextest.physics.effects.AttractorTest.
 * @author Per Thulin
 */
public class Attractor extends PhysicsUpdateAction {

    /** Flags whether or not the attractor is turned on. */
    private boolean active;

    /** Contains all the objects that should be attracted. */
    private ArrayList objects;

    /** The point that attract the objects. */
    private Vector3f attractingPoint;

    /** The force which objects should be attracted with. */
    private float force;

    /** The radius to attract objects within. */
    private float radius;

    /** Temp variable to flatline memory usage. */
    private static final Vector3f distance = new Vector3f();

    /** Temp variable to flatline memory usage. */
    private static final Vector3f direction = new Vector3f();

    /** Temp variable to flatline memory usage. */
    private static final Vector3f forceToApply = new Vector3f();

    /**
     * Constructor.
     *
     * @param attractingPoint The point to which objects are attracted.
     * @param force The force objects are attracted with.
     * @param radius The radius to attract objects within.
     */
    public Attractor(Vector3f attractingPoint, float force, float radius) {
        this.attractingPoint = attractingPoint;
        this.force = force;
        this.radius = radius;

        objects = new ArrayList();
    }

    /**
     * Gets the point to which objects are attracted.
     *
     * @return
     */
    public Vector3f getAttractingPoint() {
        return attractingPoint;
    }

    /**
     * Sets the point to which objects are attracted.
     *
     * @param attractTo
     */
    public void setAttractingPoint(Vector3f attractTo) {
        this.attractingPoint = attractTo;
    }

    /**
     * Adds an object to attract.
     *
     * @param obj
     */
    public void addObject(DynamicPhysicsObject obj) {
        objects.add(obj);
    }

    /**
     * Removes a maintained object.
     *
     * @param obj
     */
    public void removeObject(DynamicPhysicsObject obj) {
        objects.remove(obj);
    }

    /**
     * Removes all maintained objects.
     */
    public void removeAllObjects() {
        objects.clear();
    }

    /**
     * Sets whether or not this <code>Attractor</code> is on.
     *
     * @param active True activates.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns whether or not this <code>Attractor</code> is on.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
      * @see com.jmex.physics.PhysicsUpdateAction#beforeStep()
      */
    public void beforeStep( PhysicsWorld world ) {
        if (!active) return;

        for (int i = objects.size() - 1; i >= 0; i--) {
            DynamicPhysicsObject obj = (DynamicPhysicsObject) objects.get(i);

            // Calculate the distance between the object and the attracting point.
            attractingPoint.subtract(obj.getSpatial().getWorldTranslation(), distance);

            // Calculate the direction vector between the attracting point
            // and the object.
            direction.set(distance);
            direction.normalizeLocal();

            distance.x = FastMath.abs(distance.x);
            distance.y = FastMath.abs(distance.y);
            distance.z = FastMath.abs(distance.z);

            // Escape: if the object is outside of the attraction radius. Maybe
            // this is a little unnecessary, but will save computations in a
            // scene with many objects spread out.
            if (distance.x > radius || distance.y > radius || distance.z > radius)
                continue;

            // Calculate the force to apply. The force should attenuate
            // relative to the distance between the object and the
            // attracting point.
            forceToApply.x = (distance.x/radius) * force;
            forceToApply.y = (distance.y/radius) * force;
            forceToApply.z = (distance.z/radius) * force;
            forceToApply.multLocal(direction);

            // Apply the force.
            obj.addForce(forceToApply);
        }
    }

    /* (non-Javadoc)
      * @see com.jmex.physics.PhysicsUpdateAction#afterStep()
      */
    public void afterStep( PhysicsWorld world ) {
    }

}