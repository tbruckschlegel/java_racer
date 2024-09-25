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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS &quot;AS IS&quot;
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
package com.jme.util.awt.lwjgl;

import java.awt.Color;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import com.jme.renderer.ColorRGBA;
import com.jme.util.awt.JMECanvas;
import com.jme.util.awt.JMECanvasImplementor;

/**
 * <code>LWJGLJMECanvas</code>
 * 
 * @author Joshua Slack
 * @version $Id: LWJGLCanvas.java,v 1.1 2005/04/05 23:45:43 renanse Exp $
 */
public class LWJGLCanvas extends AWTGLCanvas implements JMECanvas {

    private static final long serialVersionUID = 1L;

    private JMECanvasImplementor impl;

    public LWJGLCanvas() throws LWJGLException {
        super();
    }

    public void setVSync(boolean sync) {
        setVSyncEnabled(sync);
    }

    public void setImplementor(JMECanvasImplementor impl) {
        this.impl = impl;
    }

    public void paintGL() {
        try {
            makeCurrent();

            if (!impl.isSetup())
                impl.doSetup();

            impl.doUpdate();

            impl.doRender();

            swapBuffers();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void setBackground(Color bgColor) {
        impl.setBackground(makeColorRGBA(bgColor));
    }

    protected ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }
}
