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
package com.jme.widget.button;

import com.jme.util.JmeType;

/**
 * @author Gregg Patton
 * @version $Id: WidgetButtonStateType.java,v 1.4 2004/05/27 02:07:29 guurk Exp $
 */
public final class WidgetButtonStateType extends JmeType {
	public final static WidgetButtonStateType BUTTON_OVER =
		new WidgetButtonStateType("BUTTON_OVER");
	public final static WidgetButtonStateType BUTTON_UP =
		new WidgetButtonStateType("BUTTON_UP");
	public final static WidgetButtonStateType BUTTON_DOWN =
		new WidgetButtonStateType("BUTTON_DOWN");
	public final static WidgetButtonStateType BUTTON_DISABLED =
		new WidgetButtonStateType("BUTTON_DISABLED");

	private WidgetButtonStateType(String name) {
		super(name);
	}

	public JmeType getType(String name) {
		JmeType type = null;

		if (BUTTON_UP.name.equals(name)) {
			type = BUTTON_UP;
		} else if (BUTTON_DOWN.name.equals(name)) {
			type = BUTTON_DOWN;
		} else if (BUTTON_DISABLED.name.equals(name)) {
			type = BUTTON_DISABLED;
		} else if (BUTTON_OVER.name.equals(name)) {
			type = BUTTON_OVER;
		}

		return type;
	}

}

/*
 * $Log: WidgetButtonStateType.java,v $
 * Revision 1.4  2004/05/27 02:07:29  guurk
 * Added a new state: OVER
 *
 * Added new CVS keywords.
 *
 */
