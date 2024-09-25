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
package com.jmex.physics.joints;

import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsWorld;
import org.odejava.Body;

/**
 * A superclass <code>Joint</code>. It is used to initialise all the joints
 * and obtain the ODE representation of joints from. This class should never be
 * used by the user explicitly and they should use one of its subclasses.
 * 
 * @author Ahmed
 */
public abstract class Joint {

   /**
    * Type parameters for all joints that are supported by the System or will
    * be supported.
    */
   public static final int JT_HINGE = 0;
   public static final int JT_HINGE2 = 1;
   public static final int JT_BALL = 2;
   public static final int JT_SLIDER = 3;
   public static final int JT_UNIVERSAL = 4;
   public static final int JT_AMOTOR = 5;
   public static final int JT_FIXED = 6;

   // the dynamic objects to be connected
   protected DynamicPhysicsObject obj1, obj2;

   /**
    * Set the first dynamic physics object attached to this.
    * @param obj1
    */
   public void setPhysicsObject1( DynamicPhysicsObject obj1 ) {
      this.obj1 = obj1;
   }

   /**
    * Set the second dynamic physics object attached to this.
    * @param obj2
    */
   public void setPhysicsObject2( DynamicPhysicsObject obj2 ) {
      this.obj2 = obj2;
   }

   // the ode joint representation
   protected org.odejava.Joint odeJoint;

   // this type of joint.
   private int jointType;

   // the name of this joint
   private String name;

   /**
    * The constructor for the joint. It takes the type of joint, and the two
    * objects that should be connected. This is good for things like car
    * suspensions
    *
    * @param type
    * @param obj1
    * @param obj2
    */
   public Joint(String name, int type, DynamicPhysicsObject obj1,
                DynamicPhysicsObject obj2) {
      this.obj1 = obj1;
      this.obj2 = obj2;
      this.jointType = type;
      this.name = name;
      this.odeJoint = PhysicsWorld.getInstance().getFactory().createPhysicalEntity(
            this);
   }

   /**
    * Perform the attachment and attach the two objects.
    * <p>
    * <b>Note: This operation must be performed before any parameters are set.
    * Otherwise, unexpected behaviour from the objects can arrise.</b>
    */
   public void attach() {
       Body body1 = obj1 != null ? obj1.getPhysicalEntity() : null;
       Body body2 = obj2 != null ? obj2.getPhysicalEntity() : null;
       odeJoint.attach( body1, body2 );
   }

   /**
    * Delete the joint and let the two objects go freely.
    */
   public void delete() {
      odeJoint.delete();
   }

   /**
    * @return what type of joint this is.
    */
   public int getType() {
      return jointType;
   }

   /**
    * @return the name of this joint.
    */
   public String getName() {
      return name;
   }

   /**
    * @return the first dynamic physics object attached to this. Null will be
    * returned if attached to the world.
    */
   public DynamicPhysicsObject getPhysicsObject1() {
      return obj1;
   }

   /**
    * @return the second dynamic physics object attached to this. Null will be
    * returned if attached to the world.
    */
   public DynamicPhysicsObject getPhysicsObject2() {
      return obj2;
   }

    public void detach() {
        odeJoint.attach( null, null );
    }
}