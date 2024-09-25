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
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsObject;
import com.jmex.physics.PhysicsWorld;
import com.jmex.physics.StaticPhysicsObject;
import com.jmex.physics.types.PhysicsBox;
import com.jmex.physics.types.PhysicsCone;
import com.jmex.physics.types.PhysicsCylinder;
import com.jmex.physics.types.PhysicsSphere;
import com.jmex.physics.types.PhysicsType;

/**
 * This class uses SAX to parse an XML file, and returns a list of
 * <code>PhysicsObject</code>s.
 * 
 * @see com.jmex.physics.io.PhysicsReader
 * 
 * @author Per Thulin
 */
public class XMLPhysicsReader extends PhysicsReader {
	
	/* (non-Javadoc)
	 * @see com.jmex.physics.io.PhysicsReader#loadPhysics(java.io.InputStream, com.jme.scene.Node)
	 */
	public ArrayList loadPhysics(InputStream input, Node graphics) {
		this.graphics = graphics;		
		physics = new ArrayList();
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		try {
			SAXParser parser=factory.newSAXParser();
			parser.parse(input, new PhysicsParser());
		} catch (IOException e) {
			throw new RuntimeException("Unable to do IO correctly:" + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Serious parser configuration error:" + e.getMessage());
		} catch (SAXParseException e) {
			throw new RuntimeException(e.toString() +'\n' + "Line: " +e.getLineNumber() + '\n' + "Column: " + e.getColumnNumber());
		}catch (SAXException e) {
			throw new RuntimeException("Unknown sax error: " + e.getMessage());
		}
		
		return physics;
	}
	
	/**
	 * This class interprets the XML elements and creates the physics objects.
	 * The objects are put in the {@link PhysicsReader#physics} list.
	 * 
	 * @author Per Thulin
	 */
	private class PhysicsParser extends DefaultHandler {
		
		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startDocument()
		 */
		public void startDocument() throws SAXException {
			// Create the world if not already created. This is so that not the
			// sky comes crashing down when we try to create a physics object
			// or set a world setting.
			PhysicsWorld.create();
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String namespaceURI, String sName,
				String qName, Attributes attributes) throws SAXException {
			
			if (qName.equals("world"))
				parseWorld(attributes);
			else if (qName.equals("gravity"))
				parseGravity(attributes);
			else if (qName.equals("object"))
				parseObject(attributes);
			
		}
		
		/**
		 * Parses an "object" element.
		 * 
		 * @param attributes
		 */
		private void parseObject(Attributes attributes) {
			String type = attributes.getValue("type");
			String spatialID = attributes.getValue("graphics");
			Spatial spatial = getChildInAnyGeneration(spatialID, graphics);
			if (spatial == null) {
				throw new NullPointerException("Couldn't find spatial " +
						"\"" + spatialID + "\"" + " that had physics attributes " +
						"mapped to it! Check your spelling.");
			}
			PhysicsObject object = null;
			// If the object is static, things get simple; we just need to create it
			// from its specified graphics which we hope have a bounding volume set.
			if (type.equals("static")) {
				object = new StaticPhysicsObject(spatial);
			}
			// If the object is dynamic we need to look for mass and geom attributes.
			else if (type.equals("dynamic")) {
				float mass = 0;
				PhysicsType geom = null;
				for (int i = 0; i < attributes.getLength(); i++) {
					String attribute = attributes.getQName(i);
					if (attribute.equals("mass")){
						mass = Float.parseFloat(attributes.getValue("mass"));
					}
					// If the object has specified its own physical representation:
					else if (attribute.equals("geom")) {
						geom = parseGeom(attributes);
					}
				}
				if (geom != null)
					object = new DynamicPhysicsObject(spatial, geom, mass);
				else 
					object = new DynamicPhysicsObject(spatial, mass);
			}
			
			// Finally we add the object to the list we will return.
			physics.add(object);
		}
		
		/**
		 * Parses an "object" element, and creates a <code>PhysicsType</code>
		 * from the attributes it contains.
		 * 
		 * @param attributes
		 * @return
		 */
		private PhysicsType parseGeom(Attributes attributes) {
			String type = attributes.getValue("geom");
			if (type.equals("sphere")) {
				float radius = Float.parseFloat(attributes.getValue("radius"));
				return new PhysicsSphere(radius);
			}
			else if (type.equals("box")) {
				float width = Float.parseFloat(attributes.getValue("extentx"));
				float height = Float.parseFloat(attributes.getValue("extenty"));
				float depth = Float.parseFloat(attributes.getValue("extentz"));
				return new PhysicsBox(width, height, depth);
			}
			else if (type.equals("cylinder")) {
				float radius = Float.parseFloat(attributes.getValue("radius"));
				float length = Float.parseFloat(attributes.getValue("length"));
				return new PhysicsCylinder(radius, length);
			}
			else if (type.equals("cone")) {
				float radius = Float.parseFloat(attributes.getValue("radius"));
				float length = Float.parseFloat(attributes.getValue("length"));
				return new PhysicsCone(radius, length);
			}
			return null;
		}
		
		private void parseGravity(Attributes attributes) {
			float x = Float.parseFloat(attributes.getValue("x"));
			float y = Float.parseFloat(attributes.getValue("y"));
			float z = Float.parseFloat(attributes.getValue("z"));
			PhysicsWorld.getInstance().setGravity(new Vector3f(x, y, z));
		}
		
		/**
		 * Parses a "world" element.
		 * 
		 * @param attributes
		 */
		private void parseWorld(Attributes attributes) {			
			for (int i = 0; i < attributes.getLength(); i++) {
				String attribute = attributes.getQName(i);
				if (attribute.equals("ups")) {
					int updateRate = Integer.parseInt(attributes.getValue(attribute));
					PhysicsWorld.getInstance().setUpdateRate(updateRate);
				}
				else if (attribute.equals("stepsize")) {
					float stepSize = Float.parseFloat(attributes.getValue(attribute));
					PhysicsWorld.getInstance().setStepSize(stepSize);
				}
			}			
		}
		
		/**
		 * This method searches for a child in any generation of a given node.
		 * It's used when looking for a spatial to create a physics object from.
		 * 
		 * @param childID The ID of the child to look for.
		 * @param node The node to look in.
		 * @return The child, or null if it wasn't found.
		 */
		private Spatial getChildInAnyGeneration(final String childID, final Node node) {
			ArrayList children = node.getChildren();
			for (int i = children.size() - 1; i >= 0; i--) {
				Spatial child = (Spatial) children.get(i);
				if (child.getName().equals(childID))
					return child; // Child was found in current generation.
				if (child instanceof Node)
					child = getChildInAnyGeneration(childID, (Node) child); // Dig deeper down the generation tree.
				if (child != null && child.getName().equals(childID))
					return child; // Child was found in some other generation.
			}
			return null; // No child found.
		}
		
	}
	
}
