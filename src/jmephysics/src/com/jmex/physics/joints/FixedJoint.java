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
package com.jmex.physics.joints;

import com.jmex.physics.DynamicPhysicsObject;
import org.odejava.JointFixed;

/**
 * A joint that maintains a fixed relationship between two objects. Effectively
 * creating a new object. This is useful for having a single body having
 * different mass spread. E.g. A car being front heavy.
 * 
 * @author Ahmed
 */
public class FixedJoint extends Joint {

	/**
	 * Constructor for <code>FixedJoint</code>. Takes a name and the objects
	 * to connect. Also, after attaching the two objects,
	 * <code>setFixed()</code> should be called to remember the position and
	 * orientation between the two objects.
	 * 
	 * @param name
	 * @param obj1
	 * @param obj2
	 */
	public FixedJoint(String name, DynamicPhysicsObject obj1, DynamicPhysicsObject obj2) {
		super(name, Joint.JT_FIXED, obj1, obj2);
	}

	/**
	 * Overloads the <code>super.attach()</code> method to use some ODE
	 * internals.
	 */
	public void attach() {
		super.attach();
		((JointFixed) odeJoint).setFixed();
	}

}
