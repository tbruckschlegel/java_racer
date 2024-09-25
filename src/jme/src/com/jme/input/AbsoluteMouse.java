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

/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a
 *                      WidgetViewport to update an AbstractInputHandler
 *                      without polling the mouse.  GOP
 */

package com.jme.input;

/**
 * <code>AbsoluteMouse</code> defines a mouse object that maintains a position
 * within the window. Each call to update adjusts the current position by the
 * change in position since the previous update. The mouse is forced to be
 * contained within the values provided during construction (typically these
 * correspond to the width and height of the window).
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: AbsoluteMouse.java,v 1.15 2004/10/14 01:23:06 mojomonkey Exp $
 */
public class AbsoluteMouse extends Mouse {

    private static final long serialVersionUID = 1L;

    //position
    private int width, height;

    /**
     * Constructor instantiates a new <code>AbsoluteMouse</code> object. The
     * limits of the mouse movements are provided.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param width
     *            the width of the mouse's limit.
     * @param height
     *            the height of the mouse's limit.
     */
    public AbsoluteMouse(String name, int width, int height) {
        super(name);
        this.width = width;
        this.height = height;
    }

    /**
     * <code>update</code> sets the mouse's current position within the
     * window.
     */
    public void update() {
        update(true);
    }

    /**
     * <code>update</code> updates the mouse's information with the last known
     * mouse movement and button presses. If updateState is true, the mouse is
     * polled for new movement and button press information
     * 
     * @param updateState
     *            Mouse information is updated if true
     * @see com.jme.input.Mouse#update(boolean)
     */
    public void update(boolean updateState) {
        if (updateState) mouse.updateState();

        localTranslation.x += mouse.getXDelta() * _speed;
        localTranslation.y += mouse.getYDelta() * _speed;

        if (localTranslation.x + hotSpotOffset.x < 0) {
            localTranslation.x = -hotSpotOffset.x;
        } else if (localTranslation.x + hotSpotOffset.x > width) {
            localTranslation.x = width - hotSpotOffset.x;
        }

        if (localTranslation.y + hotSpotOffset.y < 0 - imageHeight) {
            localTranslation.y = 0 - imageHeight - hotSpotOffset.y;
        } else if (localTranslation.y + hotSpotOffset.y > height) {
            localTranslation.y = height - hotSpotOffset.y;
        }
        worldTranslation.set(localTranslation);
        hotSpotLocation.set(localTranslation).addLocal(hotSpotOffset);
    }

    /**
     * set the mouse's limit.
     * 
     * @param width
     *            the width of the mouse's limit.
     * @param height
     *            the height of the mouse's limit.
     */
    public void setLimit(int width, int height) {
        this.width = width;
        this.height = height;
    }
}