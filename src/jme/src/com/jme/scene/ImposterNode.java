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
package com.jme.scene;

import com.jme.bounding.*;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.shape.*;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.math.FastMath;
import com.jme.renderer.Camera;

/**
 * <code>ImposterNode</code>
 * 
 * @author Joshua Slack
 * @version $Id: ImposterNode.java,v 1.13 2005/01/03 20:04:43 renanse Exp $
 */
public class ImposterNode extends Node {
	private static final long serialVersionUID = 1L;

	private TextureRenderer tRenderer;

	private Texture texture;

	private Node quadScene;

	private static int inode_val = 0;

	private Quad standIn;

	private float redrawRate;

	private float elapsed;

	private float cameraDistance = 10f;

	private float cameraThreshold;

	private float oldAngle;

	private float lastAngle;

	private boolean haveDrawn;

	private boolean byCamera;

	private boolean byTime;

	public ImposterNode(String name, float size, int twidth, int theight) {
		super(name);
		tRenderer = DisplaySystem.getDisplaySystem().createTextureRenderer(
				twidth, theight, false, true, false, false,
				TextureRenderer.RENDER_TEXTURE_2D, 0);

		tRenderer.getCamera().setLocation(new Vector3f(0, 0, 75f));
		tRenderer.updateCamera();
		tRenderer.setBackgroundColor(new ColorRGBA(0, 0, 0, 0f));

		quadScene = new Node("imposter_scene_" + inode_val);

		standIn = new Quad("imposter_quad_" + inode_val);
		standIn.initialize(size, size);
		standIn.setModelBound(new BoundingBox());
		standIn.updateModelBound();
		standIn.setParent(this);

		inode_val++;
		resetTexture();
		redrawRate = elapsed = .05f; // 20x per sec
		cameraThreshold = 0; // off
		haveDrawn = false;
		standIn.updateRenderState();

	}

	/**
	 * <code>draw</code> calls the onDraw method for each child maintained by
	 * this node.
	 * 
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 * @param r
	 *            the renderer to draw to.
	 */
	@Override
    public void draw(Renderer r) {
		if (!haveDrawn || shouldDoUpdate(r.getCamera())) {
			updateCamera(r.getCamera().getLocation());
			if (byTime) {
				updateScene(redrawRate);
				elapsed -= redrawRate;
			} else if (byCamera) {
				updateScene(0);
			}
			renderTexture();
			haveDrawn = true;
		}
		standIn.onDraw(r);
	}

	/**
	 * Force the texture camera to update its position and direction based on
	 * the given eyeLocation
	 * 
	 * @param eyeLocation
	 *            The location the viewer is looking from in the real world.
	 */
	public void updateCamera(Vector3f eyeLocation) {
		float vDist = eyeLocation.distance(standIn.getCenter());
		float ratio = cameraDistance / vDist;
		Vector3f newPos = (eyeLocation.subtract(standIn.getCenter()))
				.multLocal(ratio).addLocal(standIn.getCenter());
		tRenderer.getCamera().setLocation(newPos);
		tRenderer.getCamera().lookAt(standIn.getCenter());
	}

	/**
	 * Check to see if the texture needs updating based on the params set for
	 * redraw rate and camera threshold.
	 * 
	 * @param cam
	 *            The camera we check angles against.
	 * @return boolean
	 */
	private boolean shouldDoUpdate(Camera cam) {
		byTime = byCamera = false;
		if (redrawRate > 0 && elapsed >= redrawRate) {
			byTime = true;
			return true;
		}
		if (cameraThreshold > 0) {
			float camChange = FastMath.abs(getCameraChange(cam));
			if (camChange >= cameraThreshold) {
				byCamera = true;
				oldAngle = lastAngle;
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the different in radians that the camera angle has changed since last
	 * update.
	 * 
	 * @param cam
	 *            The camera we check angles against.
	 * @return float
	 */
	private float getCameraChange(Camera cam) {
		// change is last camera angle - this angle
		Vector3f eye = cam.getLocation();
		Vector3f spot = standIn.getCenter();
		float opp = eye.x - spot.x;
		float adj = eye.z - spot.z;
		if (adj == 0)
			return 0;
		lastAngle = FastMath.atan(opp / adj);
		opp = eye.y - spot.y;
		lastAngle += FastMath.atan(opp / adj);
		return oldAngle - lastAngle;
	}

	/**
	 * <code>draw</code> calls the onDraw method for each child maintained by
	 * this node.
	 * 
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 * @param r
	 *            the renderer to draw to.
	 */
	@Override
    public void drawBounds(Renderer r) {
		standIn.onDrawBounds(r);
	}

	/**
	 * 
	 * <code>attachChild</code> attaches a child to this node. This node
	 * becomes the child's parent. The current number of children maintained is
	 * returned.
	 * 
	 * @param child
	 *            the child to attach to this node.
	 * @return the number of children maintained by this node.
	 */
	@Override
    public int attachChild(Spatial child) {
		return quadScene.attachChild(child);
	}

	/**
	 * Set the Underlying texture renderer used by this imposter. Automatically
	 * calls resetTexture()
	 * 
	 * @param tRenderer
	 *            TextureRenderer
	 */
	public void setTextureRenderer(TextureRenderer tRenderer) {
		this.tRenderer = tRenderer;
		resetTexture();
	}

	/**
	 * Get the Underlying texture renderer used by this imposter.
	 * 
	 * @return TextureRenderer
	 */
	public TextureRenderer getTextureRenderer() {
		return tRenderer;
	}

	/**
	 * Get the distance we want the render camera to stay away from the render
	 * scene.
	 * 
	 * @return float
	 */
	public float getCameraDistance() {
		return cameraDistance;
	}

	/**
	 * Set the distance we want the render camera to stay away from the render
	 * scene.
	 * 
	 * @param cameraDistance
	 *            float
	 */
	public void setCameraDistance(float cameraDistance) {
		this.cameraDistance = cameraDistance;
	}

	/**
	 * Get how often (in seconds) we want the texture updated. example: .02 =
	 * every 20 ms or 50 times a sec. 0.0 = do not update based on time.
	 * 
	 * @return float
	 */
	public float getRedrawRate() {
		return redrawRate;
	}

	/**
	 * Set the redraw rate (see <code>getRedrawRate()</code>)
	 * 
	 * @param rate
	 *            float
	 */
	public void setRedrawRate(float rate) {
		this.redrawRate = rate;
		this.elapsed = rate;
	}

	/**
	 * Get the Quad used as a standin for the scene being faked.
	 * 
	 * @return Quad
	 */
	public Quad getStandIn() {
		return standIn;
	}

	/**
	 * Set how much the viewers camera position has to change (in terms of angle
	 * to the imposter) before an update is called.
	 * 
	 * @param threshold
	 *            angle in radians
	 */
	public void setCameraThreshold(float threshold) {
		this.cameraThreshold = threshold;
		this.oldAngle = cameraThreshold + threshold;
	}

	/**
	 * Get the camera threshold (see <code>setCameraThreshold()</code>)
	 * 
	 * @param rate
	 *            float
	 */
	public float getCameraThreshold() {
		return cameraThreshold;
	}

	/**
	 * Resets and applies the texture, texture state and alpha state on the
	 * standin Quad.
	 */
	public void resetTexture() {
		if (texture != null)
			texture = tRenderer.setupTexture(texture.getTextureId());
		else
			texture = tRenderer.setupTexture();
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState();
		ts.setEnabled(true);
		ts.setTexture(texture, 0);
		standIn.setRenderState(ts);

		// Add a blending mode... This is so the background of the texture is
		// transparent.
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer()
				.createAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		as1.setEnabled(true);
		standIn.setRenderState(as1);
	}

	/**
	 * Updates the scene the texture represents.
	 * 
	 * @param timePassed
	 *            float
	 */
	public void updateScene(float timePassed) {
		quadScene.updateGeometricState(timePassed, true);
	}

	/**
	 * force the underlying texture renderer to render the scene. Could be
	 * useful for imposters that do not use time or camera angle to update the
	 * scene. (In which case, updateCamera and updateScene would likely be
	 * called prior to calling this.)
	 */
	public void renderTexture() {
		tRenderer.render(quadScene, texture);
	}

	/**
	 * <code>updateWorldBound</code> merges the bounds of all the children
	 * maintained by this node. This will allow for faster culling operations.
	 * 
	 * @see com.jme.scene.Spatial#updateWorldBound()
	 */
	@Override
    public void updateWorldBound() {
		worldBound = (BoundingVolume) standIn.getWorldBound().clone(worldBound);
	}

	/**
	 * 
	 * <code>updateWorldData</code> updates the world transforms from the
	 * parent down to the leaf.
	 * 
	 * @param time
	 *            the frame time.
	 */
	@Override
    public void updateWorldData(float time) {
		super.updateWorldData(time);
		standIn.updateGeometricState(time, false);
		elapsed += time;
	}
}