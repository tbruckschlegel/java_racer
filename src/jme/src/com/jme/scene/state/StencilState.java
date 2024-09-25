/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene.state;


/**
 * The StencilState RenderState allows the user to set the attributes of the
 * stencil buffer of the renderer. The Stenciling is similar to
 * Z-Buffering in that it allows enabling and disabling drawing on a per pixel
 * basis. You can use the stencil plane to mask out portions of the rendering
 * to create special effects, such as outlining or planar shadows.
 * @author Mark Powell
 * @version $id$
 */
public abstract class StencilState extends RenderState {

    /** A stencil function that never passes. */
    public static final int SF_NEVER = 0;
    /** A stencil function that passes if (ref & mask) < (stencil & mask). */
    public static final int SF_LESS = 1;
    /** A stencil function that passes if (ref & max) <= (stencil & mask). */
    public static final int SF_LEQUAL = 2;
    /** A stencil function that passes if (ref & max) > (stencil & mask). */
    public static final int SF_GREATER = 3;
    /** A stencil function that passes if (ref & max) >= (stencil & mask). */
    public static final int SF_GEQUAL = 4;
    /** A stencil function that passes if (ref & max) == (stencil & mask). */
    public static final int SF_EQUAL = 5;
    /** A stencil function that passes if (ref & max) != (stencil & mask). */
    public static final int SF_NOTEQUAL = 6;
    /** A stencil function that always passes. */
    public static final int SF_ALWAYS = 7;

    /** A stencil function result that keeps the current value. */
    public static final int SO_KEEP = 0;
    /** A stencil function result that sets the stencil buffer value to 0. */
    public static final int SO_ZERO = 1;
    /** A stencil function result that sets the stencil buffer value to ref, as specified by stencil function. */
    public static final int SO_REPLACE = 2;
    /** A stencil function result that increments the current stencil buffer value. */
    public static final int SO_INCR = 3;
    /** A stencil function result that decrements the current stencil buffer value. */
    public static final int SO_DECR = 4;
    /** A stencil function result that bitwise inverts the current stencil buffer value. */
    public static final int SO_INVERT = 5;
    
    private int stencilFunc;
    private int stencilRef;
    private int stencilMask;
    private int stencilOpFail;
    private int stencilOpZFail;
    private int stencilOpZPass;

    /**
     * Returns RS_STENCIL
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_STENCIL;
    }

    /**
     * Sets the function that defines if a stencil test passes or not.
     * @param func The new stencil function.
     */
    public void setStencilFunc(int func) {
        this.stencilFunc = func;
    }

    /**
     * Returns the currently set stencil function.
     * @return The current stencil function.
     */
    public int getStencilFunc() {
        return stencilFunc;
    }

    /**
     * Sets the stencil reference to be used during the stencil function.
     * @param ref The new stencil reference.
     */
    public void setStencilRef(int ref) {
        this.stencilRef = ref;
    }

    /**
     * Returns the currently set stencil reference.
     * @return The current stencil reference.
     */
    public int getStencilRef() {
        return stencilRef;
    }

    /**
     * Sets the stencil mask to be used during the stencil function.
     * @param mask The new stencil mask.
     */
    public void setStencilMask(int mask) {
        this.stencilMask = mask;
    }

    /**
     * Returns the currently set stencil mask.
     * @return The current stencil mask.
     */
    public int getStencilMask() {
        return stencilMask;
    }

    /**
     * Specifies the aciton to take when the stencil test fails.  One of SO_KEEP, SO_ZERO, and so on.
     * @param op The new stencil operation.
     */
    public void setStencilOpFail(int op) {
        this.stencilOpFail = op;
    }

    /**
     * Returns the current stencil operation.
     * @return The current stencil operation.
     */
    public int getStencilOpFail() {
        return stencilOpFail;
    }

    /**
     * Specifies stencil action when the stencil test passes, but the depth test fails.
     * One of SO_KEEP, SO_ZERO, and so on.
     * @param op The Z test operation to set.
     */
    public void setStencilOpZFail(int op) {
        this.stencilOpZFail = op;
    }

    /**
     * Returns the current Z op fail function.
     * @return The current Z op fail function.
     */
    public int getStencilOpZFail() {
        return stencilOpZFail;
    }

    /**
     * Specifies stencil action when both the stencil test and the depth test pass, or
     * when the stencil test passes and either there is no depth buffer or depth
     * testing is not enabled.  One of SO_KEEP, SO_ZERO, and so on.
     * @param op The new Z test pass operation to set.
     */
    public void setStencilOpZPass(int op) {
        this.stencilOpZPass = op;
    }

    /**
     * Returns the current Z op pass function.
     * @return The current Z op pass function.
     */
    public int getStencilOpZPass() {
        return stencilOpZPass;
    }
}
