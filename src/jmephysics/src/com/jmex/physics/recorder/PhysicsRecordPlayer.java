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
package com.jmex.physics.recorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * Plays a recording made by the <code>PhysicsRecorder</code> into an object.
 * 
 * @author Ahmed
 */
public class PhysicsRecordPlayer {

	/** the times that the forces would act */
	private float[] time;

	/** the force on the object at the recorded time */
	private Vector3f[] force;

	/** the torque on the object at the recorded time */
	private Vector3f[] torque;

	/** the angular velocity of the object at that time */
	private Vector3f[] angularVelocity;

	/** the linear velocity of the object at that time */
	private Vector3f[] linearVelocity;

	/** the object to act on */
	private DynamicPhysicsObject obj;

	// interal uses
	private int counter = 0;
	private float relativeTime = 0.0f;

	/**
	 * Create the recordplayer with the give file and the object to act on.
	 * 
	 * @param f,
	 *            the input stream
	 * @param obj,
	 *            the object to act on.
	 */
	public PhysicsRecordPlayer(InputStream f, DynamicPhysicsObject obj) {
		this.obj = obj;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(f, new XMLParser());
		} catch (ParserConfigurationException pce) {
			LoggingSystem.getLogger().log(Level.INFO,
					"Error occured during parsing: " + pce.getMessage());
		} catch (SAXException saxE) {
			LoggingSystem.getLogger().log(Level.INFO,
					"Error occured during parsing: " + saxE.getMessage());
		} catch (IOException ioe) {
			LoggingSystem.getLogger().log(Level.INFO,
					"Error occured during parsing: " + ioe.getMessage());
		}
		// reset the counter
		counter = 0;
	}

	/**
	 * Play the file onto the object
	 * 
	 * @param time
	 */
	public void play(float time) {
		relativeTime += time;
		for (int i = counter; i < this.time.length; i++) {
			if (relativeTime >= this.time[i]) {
				obj.addForce(force[i]);
				obj.addTorque(torque[i]);
				obj.setAngularVelocity(angularVelocity[i]);
				obj.setLinearVelocity(linearVelocity[i]);
				counter++;
				break;
			}
		}
	}

	/**
	 * Internal class used to parse the xml
	 * 
	 * @author Ahmed
	 */
	class XMLParser extends DefaultHandler {

		/**
		 * parse the elements
		 */
		public void startElement(String URI, String localName, String qName, Attributes attrib) {
			if (qName.equalsIgnoreCase("record")) {
				String numOfRecords = attrib.getValue("entries");
				int t = Integer.parseInt(numOfRecords);
				time = new float[t];
				force = new Vector3f[t];
				torque = new Vector3f[t];
				angularVelocity = new Vector3f[t];
				linearVelocity = new Vector3f[t];
			} else if (qName.equalsIgnoreCase("entry")) {
				float tmpTime = Float.parseFloat(attrib.getValue("time"));
				time[counter] = tmpTime;
			} else if (qName.equalsIgnoreCase("force")) {
				String strForce = attrib.getValue("value");

				force[counter] = splitString(strForce);
			} else if (qName.equalsIgnoreCase("torque")) {
				String strTorque = attrib.getValue("value");

				torque[counter] = splitString(strTorque);
			} else if (qName.equalsIgnoreCase("angularVelocity")) {
				String angVel = attrib.getValue("value");

				angularVelocity[counter] = splitString(angVel);
			} else if (qName.equalsIgnoreCase("linearVelocity")) {
				String linVel = attrib.getValue("value");

				linearVelocity[counter] = splitString(linVel);
			}
		}

		/**
		 * Process the end elements
		 */
		public void endElement(String URI, String localName, String qName) {
			if (qName.equalsIgnoreCase("entry")) {
				counter++;
			}
		}

		/**
		 * Split a string like "0 0 0" and returns a Vector3f object
		 * 
		 * @param str
		 * @return
		 */
		private Vector3f splitString(String str) {
			String[] compStrForces = str.split(" ");
			float[] compForces = new float[3];
			compForces[0] = Float.parseFloat(compStrForces[0]);
			compForces[1] = Float.parseFloat(compStrForces[1]);
			compForces[2] = Float.parseFloat(compStrForces[2]);

			return new Vector3f(compForces[0], compForces[1], compForces[2]);
		}
	}
}
