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
package com.jmex.physics.io;

import java.io.InputStream;
import java.util.ArrayList;

import com.jme.scene.Node;

/**
 * <code>PhysicsReader</code> is the abstract superclass of all readers. It provides
 * the interface for retrieving <code>PhysicsObject</code>s stored in a file.
 * <p>
 * The file contains physics attributes that are mapped to jME Spatials. In order for
 * the reader to create <code>PhysicsObject</code>s, a node containing those Spatials
 * will have to be passed as an argument. If the physics file refers to a Spatial
 * that is not contained in that node, a NPE will be thrown telling you so. The file
 * can also contain some basic settings of the <code>PhysicsWorld</code>, such as
 * updaterate, stepsize and gravity.
 * 
 * @author Per Thulin
 */
public abstract class PhysicsReader {

	/** The node containing all Spatials. */
	protected Node graphics;
	
	/** 
	 * The list that we fill with physics objects mapped 
	 * to spatials in the graphics node.
	 */
	protected ArrayList physics;
	
	/**
	 * Returns a list of <code>PhysicsObject</code>s defined in a file.
	 * <p>
	 * The file contains physics attributes that are mapped to jME Spatials. In order for
	 * the reader to create <code>PhysicsObject</code>s, a node containing those Spatials
	 * will have to be passed as an argument. If the physics file refers to a Spatial
	 * that is not contained in that node, a NPE will be thrown telling you so. The file
	 * can also contain some basic settings of the <code>PhysicsWorld</code>, such as
	 * updaterate, stepsize and gravity.
	 * 
	 * @param
	 *     input The stream that points to the physics file.
	 * @param
	 *     graphics A node containing the spatials that the physics file refers to.
	 * @return
	 *     An ArrayList containing <code>PhysicsObject</code>s that have been
	 *     created from the attributes stored in the physics file, together with the
	 *     spatials contained in the node parameter.
	 */
	public abstract ArrayList loadPhysics(InputStream input, Node graphics);
	
}