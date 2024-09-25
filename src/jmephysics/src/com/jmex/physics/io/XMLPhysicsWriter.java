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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.odejava.Geom;
import org.odejava.GeomBox;
import org.odejava.GeomCappedCylinder;
import org.odejava.GeomCone;
import org.odejava.GeomSphere;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsObject;
import com.jmex.physics.PhysicsWorld;
import com.jmex.physics.StaticPhysicsObject;

/**
 * Writes an XML file out of a list of PhysicsObjects and 
 * optionally settings of the PhysicsWorld.
 * 
 * @author Per Thulin
 */
public class XMLPhysicsWriter extends PhysicsWriter {

	/** Points to the file we writes to. */
	private Writer output;
	
	/** The line we're writing in the {@link #writeLine()}.  */
	private StringBuffer currentLine;
	
	/** Current indentation. Controlled by the start/end element methods. */
	private short tabCount;
	
	/* (non-Javadoc)
	 * @see com.jmex.physics.io.PhysicsWriter#writePhysics(java.io.OutputStream, java.util.ArrayList, boolean)
	 */
	public void writePhysics(Writer output, List objects,
			boolean writeWorldSettings) {
		
		this.output = output;
		currentLine = new StringBuffer();
		tabCount = 0;
		// Used to throw into the startElement method when writing
		// various elements.
		ArrayList attributes = new ArrayList();
		
		// Write the header.
		currentLine.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		writeLine();
		
		// Start out root element.
		startElement("physics", null);
		
		// Write the "world" element if user wants to.
		if (writeWorldSettings) {
			String ups = "" + PhysicsWorld.getInstance().getUpdateRate();
			String stepsize = "" + PhysicsWorld.getInstance().getStepSize();
			
			attributes.add(new Attribute("ups", ups));
			attributes.add(new Attribute("stepsize", stepsize));
			startElement("world", attributes);
			attributes.clear();
			
			Vector3f g = PhysicsWorld.getInstance().getGravity();
			attributes.add(new Attribute("x", "" + g.x));
			attributes.add(new Attribute("y", "" + g.y));
			attributes.add(new Attribute("z", "" + g.z));
			startElement("gravity", attributes);
			endElement("gravity");
			endElement("world");
		}
		
		// Write the objects that have been passed as a parameter.
		Iterator i = objects.iterator();
		while (i.hasNext()) {
			PhysicsObject obj = (PhysicsObject) i.next();
			
			attributes.clear();
			
			// Write the objects "type" attribute (static or dynamic).
			if (obj.isStatic())
				attributes.add(new Attribute("type", "static"));
			else
				attributes.add(new Attribute("type", "dynamic"));
			
			// One attribute both dynamic and static physics objects
			// have in common is "graphics", which refers to the name
			// of the spatial the object is bound to.
			String graphics = obj.getSpatial().getName();
			attributes.add(new Attribute("graphics", graphics));
			
			// If the currently processed object is dynamic, it
			// has a "mass" attribute.
			if (!obj.isStatic()) {
				float mass = ((DynamicPhysicsObject)obj).getMass();
				attributes.add(new Attribute("mass", mass));
			}
			
			// Following is code that extracts the geom attributes of the
			// object.
			
			// Here we extract the ODE geometry from the object. The procedure
			// is slightly different between static and dynamic objects.
			// Note: this will not work with compounded objects of any sort.
			Geom geom = null;
			if (obj.isStatic())
				geom = (Geom) ((StaticPhysicsObject)obj).getPhysicalEntities().get(0);
			else
				geom = ((DynamicPhysicsObject)obj).getPhysicalEntity().getGeom();
			
			// Following is code that investigates what kind of ODE geometry
			// the object contains, and then extracts its attributes.
			
			if (geom instanceof GeomBox) {
				attributes.add(new Attribute("geom", "box"));
				GeomBox box = (GeomBox) geom;
				float length[] = box.getLengths();
				attributes.add(new Attribute("extentx", length[0]));
				attributes.add(new Attribute("extenty", length[1]));
				attributes.add(new Attribute("extentz", length[2]));
			}
			else if (geom instanceof GeomSphere) {
				attributes.add(new Attribute("geom", "sphere"));
				GeomSphere sphere = (GeomSphere) geom;
				float radius = sphere.getRadius();
				attributes.add(new Attribute("radius", radius));
			}
			else if (geom instanceof GeomCappedCylinder) {
				attributes.add(new Attribute("geom", "cylinder"));
				GeomCappedCylinder cylinder = (GeomCappedCylinder) geom;
				float radius = cylinder.getRadius();
				float length = cylinder.getLength();
				attributes.add(new Attribute("radius", radius));
				attributes.add(new Attribute("length", length));
			}
			else if (geom instanceof GeomCone) {
				attributes.add(new Attribute("geom", "cone"));
//				GeomCone cone = (GeomCone) geom;
//				float radius = cone.getRadius();
//				float length = cone.getLength();
//				attributes.add(new Attribute("radius", radius));
//				attributes.add(new Attribute("length", length));
			}
			
			startElement("object", attributes);
			endElement("object");
		} // Finished writing the objects.
		
		// End our root element.
		endElement("physics");
		
		try {
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // End constructor.
	
	/**
	 * Writes a start-element with the attributes given as a parameter.
	 * 
	 * @param eName Name of the start element.
	 * @param attributes The elements attributes.
	 */
	private void startElement(String eName, ArrayList attributes) {
		currentLine.append("<" + eName);
		if (attributes != null) {
			Iterator i = attributes.iterator();
			while (i.hasNext()) {
				Attribute a = (Attribute) i.next();
				currentLine.append(" " + a.getQName());
				currentLine.append("=\"" + a.value + "\"");
			}
		}
		currentLine.append(">");
		writeLine();
		tabCount++;
	}
	
	/**
	 * Writes an end-element.
	 * 
	 * @param eName The elements name.
	 */
	private void endElement(String eName) {
		currentLine.append("</" + eName + ">");
		tabCount--;
		writeLine();
	}
	
	/**
	 * Writes the currently processed line to the file
	 * with proper indentation and row breaking.
	 */
	private void writeLine() {
		try {
			for (int i = 0; i < tabCount; i++) {
				output.write('\t');
			}
			currentLine.append('\n');
				output.write(currentLine.toString());
			System.out.println("writing line: " + currentLine.toString());
			currentLine.setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Holds an attribute with its belonging value.
	 * 
	 * @author Per Thulin	 
	 */
	private class Attribute {
		private String qName;
		private String value;
		
		Attribute(String qName, String value) {
			this.qName = qName;
			this.value = value;
		}
		
		Attribute(String qName, float value) {
			this(qName, "" + value);
		}
		
		Attribute(String qName, int value) {
			this(qName, "" + value);
		}
		
		Attribute(String qName, boolean value) {
			this(qName, "" + value);
		}
		
		String getQName() {
			return this.qName;
		}
		
		String getValue() {
			return this.value;
		}
	}
	
}
