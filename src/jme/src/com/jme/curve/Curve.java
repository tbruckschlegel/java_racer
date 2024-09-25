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
package com.jme.curve;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.system.JmeException;

/**
 * <code>Curve</code> defines a collection of points that make up a curve.
 * How this curve is constructed is undefined, and the job of a subclass.
 * The control points are defined as the super class <code>Geometry</code>
 * vertex array. They can be set and accessed as such.
 * <code>Curve</code> is abstract only maintaining the point collection. It
 * defines <code>getPoint</code> and <code>getOrientation</code>. Extending
 * classes are responsible for implementing these methods in the appropriate
 * way.
 * @author Mark Powell
 * @version $Id: Curve.java,v 1.12 2004/08/21 06:18:30 cep21 Exp $
 */
public abstract class Curve extends Geometry {

    protected int steps;

    /**
     * Constructor creates a default <code>Curve</code> object with a
     * zero size array for the points.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     */
    public Curve(String name) {
    	super(name);
        this.vertex = new Vector3f[0];
        steps = 25;
    }

    /**
     * Constructor creates a <code>Curve</code> object. The control
     * point list is set during creation. If the control point list is
     * null or has fewer than 2 points, an exception is thrown.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param controlPoints the points that define the curve.
     */
    public Curve(String name, Vector3f[] controlPoints) {
    	super(name);
        if (null == controlPoints) {
            throw new JmeException("Control Points may not be null.");
        }

        if (controlPoints.length < 2) {
            throw new JmeException("There must be at least two control points.");
        }

        this.vertex = controlPoints;
        steps = 25;
    }

    /**
     *
     * <code>setSteps</code> sets the number of steps that make up the curve.
     * @param steps the number of steps that make up the curve.
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     *
     * <code>getSteps</code> retrieves the number of steps that make up the
     * curve.
     * @return the number of steps that makes up the curve.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * <code>draw</code> calls super to set the render state then calls the
     * renderer to display the curve.
     * @param r the renderer used to display the curve.
     */
    public void draw(Renderer r) {
      if (!r.isProcessingQueue()) {
        if (r.checkAndAdd(this))
          return;
      }
      super.draw(r);
      r.draw(this);
    }

    /**
     * <code>drawBounds</code> calls super to set the render state then passes itself
     * to the renderer.
     * @param r the renderer to display
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(this);
    }

    /**
     *
     * <code>getPoint</code> calculates a point on the curve based on
     * the time, where time is [0, 1]. How the point is calculated is
     * defined by the subclass.
     * @param time the time frame on the curve, [0, 1].
     * @return the point on the curve at a specified time.
     */
    public abstract Vector3f getPoint(float time);

    /**
     * Equivalent to getPoint(float) but instead of creating a new Vector3f object
     * on the heap, the result is stored in store and store is returned.
     * @param time the time frame on the curve: [0, 1].
     * @param store the vector3f object to store the point in.
     * @return store, after receiving the result.
     * @see #getPoint(float) 
     */
    public abstract Vector3f getPoint(float time, Vector3f store);

    /**
     *
     * <code>getOrientation</code> calculates a rotation matrix that
     * defines the orientation along a curve. How the matrix is
     * calculated is defined by the subclass.
     * @param time the time frame on the curve, [0, 1].
     * @param precision the accuracy of the orientation (lower is more
     *      precise). Recommended (0.1).
     * @return the rotational matrix that defines the orientation of
     *      along a curve.
     */
    public abstract Matrix3f getOrientation(float time, float precision);

    /**
     *
     * <code>getOrientation</code> calculates a rotation matrix that
     * defines the orientation along a curve. The up vector is provided
     * keeping the orientation from "rolling" along the curve. This is
     * useful for camera tracks. How the matrix is calculated is defined
     * by the subclass.
     * @param time the time frame on the curve, [0, 1].
     * @param precision the accuracy of the orientation (lower is more
     *      precise). Recommended (0.1).
     * @param up the up vector to lock.
     * @return the rotational matrix that defines the orientation of
     *      along a curve.
     */
    public abstract Matrix3f getOrientation(float time, float precision, Vector3f up);


}
