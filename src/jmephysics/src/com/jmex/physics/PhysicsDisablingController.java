/*
 * Copyright (c) 2004-2005, jme-Physics All rights reserved.
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
 * Neither the name of the jme-Physics nor the
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
package com.jmex.physics;

import com.jme.renderer.Camera;
import com.jme.scene.Controller;

/**
 * 
 * A controller specifically for <code>DynamicPhysicsObject</code> that will
 * disable the <code>DynamicPhysicsObject</code> when its graphical
 * representation is culled by frustum culling. This can lead to an increase in
 * FPS but on the price of getting incorrect physics when the object is not at
 * rest.
 * 
 * @author Ahmed
 * 
 */
public class PhysicsDisablingController extends Controller {

    // the default serialised version of this controller
    private static final long serialVersionUID = 1L;

    // the physics object that we are disabling
    private DynamicPhysicsObject phyObject;

    /**
     * Constructor for the <code>PhysicsDisablingController</code>. Takes the
     * <code>DynamicPhysicsObject</code> that it controls as the parameters
     * 
     * @param obj
     */
    public PhysicsDisablingController(DynamicPhysicsObject obj) {
        this.phyObject = obj;
    }

    /**
     * This method is called everytime the root node of the jme geometry is
     * updated. This method will enabled/disable the physics object controller
     * by this controller.
     */
    public void update(float time) {
    	if (super.isActive()) {
	    	// the last time an intersection with the object
		    // and the frustum, what happened?
		    int lastFrustum = phyObject.getSpatial().getLastFrustumIntersection();
		
		    // check if it was outside the frustum
		    // if so, disable the physics
		    if (lastFrustum == Camera.OUTSIDE_FRUSTUM) {
		        phyObject.setEnabled(false);
		    } else {
		        phyObject.setEnabled(true);
		    }
    	}
    }

}
