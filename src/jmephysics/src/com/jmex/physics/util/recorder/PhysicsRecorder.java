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
package com.jmex.physics.util.recorder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsObject;

/**
 * Recorder used to record the forces and torques applied to a body at any given
 * time.
 * <p>
 * Notice that the position and rotation of the object will not be stored. Only
 * the forces acting on the object. It only stores the Force and the Torque
 * 
 * @author Ahmed
 */
public class PhysicsRecorder {

	/** An ArrayList containing all the points in time of the physics object */
	private ArrayList pointsInTime;

	/** The physics object under survaliance. */
	private DynamicPhysicsObject obj;

	/** how long to wait on each recording */
	private float timeInterval;

	/** the time that has passed since last recording */
	private float currentTime = 0.0f;

	/** how long the recording has been going on */
	private float runningTime = 0.0f;

	// some objects to remove object creation
	private PointInTime tmpPointInTime;

	/**
	 * Constructor for the physics.
	 * 
	 * @param obj,
	 *            The DynamicPhysicsObject under question.
	 * @param timeInterval,
	 *            How many seconds pass per recording.
	 */
	public PhysicsRecorder(DynamicPhysicsObject obj, float timeInterval) {
		pointsInTime = new ArrayList();
		this.obj = obj;
		this.timeInterval = timeInterval;
	}

	/**
	 * Set the time in which recordings can take place
	 * 
	 * @param time
	 */
	public void setTimeInterval(float time) {
		timeInterval = time;
	}
	
	/**
	 * Obtain the time interval to record between.
	 * @return
	 */
	public float getTimeInterval() {
		return timeInterval;
	}

	/**
	 * Record the force on the object.
	 * 
	 * @param dt,
	 *            the time interval that has passed since last frame.
	 */
	public void record(float dt) {
		runningTime += dt;
		currentTime += dt;
		if (currentTime >= timeInterval) {
			tmpPointInTime = new PointInTime();
			tmpPointInTime.force.set(obj.getForce());
			tmpPointInTime.torque.set(obj.getTorque());
			tmpPointInTime.time = runningTime;
			pointsInTime.add(tmpPointInTime);
			currentTime = 0.0f;
		}
	}

	/**
	 * Save the points in time into the file.
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void save(OutputStream output) throws IOException {

		// the line seperator:
		String lineSep = System.getProperty("line.separator");

		// the string to write;
		String write = "<record entries=\"" + pointsInTime.size() + "\">" + lineSep;

		// begin the xml
		output.write(write.getBytes());

		// loop through the keys and get their values and output
		Iterator it = pointsInTime.iterator();
		while (it.hasNext()) {
			tmpPointInTime = (PointInTime) it.next();
			// create a new entry
			write = "\t<entry time=\"" + tmpPointInTime.time + "\">" + lineSep;
			output.write(write.getBytes());

			// output the force
			write = "\t\t<force value=\"" + tmpPointInTime.force.x + " " + tmpPointInTime.force.y
					+ " " + tmpPointInTime.force.z + "\"/>" + lineSep;
			output.write(write.getBytes());

			// output the torque
			write = "\t\t<torque value=\"" + tmpPointInTime.torque.x + " "
					+ tmpPointInTime.torque.y + " " + tmpPointInTime.torque.z + "\"/>" + lineSep;
			output.write(write.getBytes());

			// close the entry
			write = "\t</entry>" + lineSep;
			output.write(write.getBytes());
		}

		// close the xml
		write = "</record>" + lineSep;
		output.write(write.getBytes());

		output.flush();
		output.close();
	}

	/**
	 * Save to a file and clear the history.
	 * 
	 * @param f
	 * @throws IOException
	 */
	public void saveAndClear(OutputStream f) throws IOException {
		save(f);
		pointsInTime.clear();
	}

	/**
	 * A holding class for all the different variables in the
	 * DynamicPhysicsObject.
	 * 
	 * @author Ahmed
	 */
	class PointInTime {
		public float time;
		public Vector3f force;
		public Vector3f torque;

		public PointInTime() {
			time = 0;
			force = new Vector3f();
			torque = new Vector3f();
		}
	}

}
