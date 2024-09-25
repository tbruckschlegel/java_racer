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
package com.jmex.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.odejava.Body;
import org.odejava.GeomBox;
import org.odejava.GeomCappedCylinder;
import org.odejava.GeomCone;
import org.odejava.GeomSphere;
import org.odejava.GeomTransform;
import org.odejava.GeomTriMesh;
import org.odejava.JointAMotor;
import org.odejava.JointBall;
import org.odejava.JointFixed;
import org.odejava.JointHinge;
import org.odejava.JointHinge2;
import org.odejava.JointSlider;
import org.odejava.JointUniversal;
import org.odejava.Placeable;
import org.odejava.PlaceableGeom;
import org.odejava.World;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.OrientedBox;
import com.jme.scene.shape.Sphere;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jmex.physics.joints.Joint;
import com.jmex.physics.types.PhysicsBox;
import com.jmex.physics.types.PhysicsCone;
import com.jmex.physics.types.PhysicsCylinder;
import com.jmex.physics.types.PhysicsSphere;
import com.jmex.physics.types.PhysicsType;

/**
 * The PhysicsObjects use this class to obtain their PhysicalEntitys. <b>The user
 * should never touch this class.</b>
 * 
 * @author Ahmed Al-Hindawi
 * @author Per Thulin
 */
public class PhysicsEntityFactory {
   private World world;

   PhysicsEntityFactory( World world ) {
      this.world = world;
   }

   /**
    * store value for field world
    */
   private PhysicsWorld physicsWorld;

   /**
    * @return current value of the field world
    */
   public PhysicsWorld getPhysicsWorld()
   {
      return this.physicsWorld;
   }

   /**
    * @param value new value for field world
    * @return true if world was changed
    */
   boolean setPhysicsWorld( final PhysicsWorld value )
   {
      final PhysicsWorld oldValue = this.physicsWorld;
      boolean changed = false;
      if ( oldValue != value )
      {
         if ( oldValue != null )
         {
            this.physicsWorld = null;
            oldValue.setFactory( null );
         }
         this.physicsWorld = value;
         if ( value != null )
         {
            value.setFactory( this );
         }
         changed = true;
      }
      return changed;
   }

   public ArrayList createEntites( StaticPhysicsObject obj ) {

      //update all the world coordinates of the spatial (and probably its children)
      obj.getSpatial().updateGeometricState( 0, true );

      // populate the list
      ArrayList listOfJmes;
      if (obj.getSpatial() instanceof Node) {
         listOfJmes = (ArrayList) stripNode((Node) obj.getSpatial(), new ArrayList() );
      } else {
         listOfJmes = new ArrayList(1);
         listOfJmes.add( obj.getSpatial());
      }

      ArrayList listOfGeoms = new ArrayList();
      // loop through the list of geometries and add them to the list
      Iterator it = listOfJmes.iterator();
      while (it.hasNext()) {
         Geometry jmeGeo = (Geometry) it.next();

         Placeable odeGeo = createGeom( jmeGeo, obj );
         listOfGeoms.add(odeGeo);
      }
      return listOfGeoms;
   }

    /**
     * <p>
     * Creates a physical representation (either a Body in case of a dynamic
     * object, or a PlaceableGeom in case it's static) from a
     * <code>PhysicsObject</code>. To know which geometry it should be using,
     * it looks at the jME geometry of that object, and from there decides which
     * shape that will fit the best.
     * </p>
     * <p>
     * The physical representation will have the same position and rotation as
     * its graphical corresponding, and in case of a DynamicPhysicsObject, a mass
     * will also be set.
     * </p>
     * <p>
     * Note: the user should <b>NEVER touch this method </b>, since it's only
     * used by the <code>PhysicsObject</code> (to obtain its physical entity),
     * during its creation.
     * </p>
     *
     * @param obj
     *            The object to create the geometry from.
     */
    public Placeable createPhysicalEntity(DynamicPhysicsObject obj) {

        Spatial jmeGeo = obj.getSpatial();
       //update all the world coordinates of the spatial (and probably its children)
       jmeGeo.updateGeometricState( 0, true );

       if (jmeGeo instanceof Node ) {
          // a node only exists if its a DymanicPhysicsObject. So just return
          // a body alot of compounded objects in it representing its children
          return createCompoundedEntity( obj );
       } else {
         PlaceableGeom geom = createGeom( jmeGeo, obj );
         return createBodyForSingleGeom( obj, geom );
       }
    }

   /**
    * Creates the Body of a DynamicPhysicsObject. To know how to represent it
    * physically it looks at the PhysicsType provided.
    * <p>
    * Note: the user should <b>NEVER touch this method </b>, since it's only
    * used by the <code>PhysicsObject</code> (to obtain its physical entity),
    * during its creation.
    * </p>
    *
    * @param obj
    *            The object to create the physical entity of.
    * @param type
    *            The geometry that will represent it physically.
    */
   public Placeable createPhysicalEntity(DynamicPhysicsObject obj, PhysicsType type) {
      PlaceableGeom odeGeo;
      if (type.getType() == PhysicsType.TYPE_SPHERE) {
         PhysicsSphere sphere = (PhysicsSphere) type;
         odeGeo = new GeomSphere(obj.getName(), sphere.radius);
      } else if (type.getType() == PhysicsType.TYPE_BOX) {
         PhysicsBox box = (PhysicsBox) type;
         odeGeo = new GeomBox(obj.getName(), box.xLength, box.yLength, box.zLength);
      } else if (type.getType() == PhysicsType.TYPE_CYLINDER) {
         PhysicsCylinder cylinder = (PhysicsCylinder) type;
         odeGeo = new GeomCappedCylinder(obj.getName(), cylinder.radius, cylinder.length);
      } else if (type.getType() == PhysicsType.TYPE_CONE) {
         PhysicsCone cone = (PhysicsCone) type;
         odeGeo = new GeomCone(obj.getName(), cone.radius, cone.length);
      } else {
         throw new IllegalArgumentException("Unknown PhysicsType");
      }

      odeGeo.setGraphics( obj.getSpatial() );
      odeGeo.setPhysicsObject( obj );

      Body body = new Body(obj.getSpatial().getName(), world, odeGeo);
      body.adjustMass(obj.getMass());

      return body;
   }

    private PlaceableGeom createGeom( Spatial jmeGeo, PhysicsObject obj ) {
        if (jmeGeo instanceof Sphere ) {
           return createSphere((Sphere) jmeGeo, obj, jmeGeo);
        } else if (jmeGeo instanceof Cylinder ) {
           return createCylinder((Cylinder) jmeGeo, obj );
        } else if (jmeGeo instanceof Box ) {
           return createBox((Box) jmeGeo, obj );
        } else if (jmeGeo instanceof OrientedBox ) {
           return createOrientedBox((OrientedBox) jmeGeo, obj, jmeGeo);
        } else if (jmeGeo instanceof TriMesh ) {
           return createTriMesh(( TriMesh ) jmeGeo, obj);
        } else {
           throw new IllegalArgumentException();
        }
    }

   private Placeable createBodyForSingleGeom( PhysicsObject obj, PlaceableGeom odeGeo )
   {
      Body body = new Body(obj.getSpatial().getName(), world, odeGeo);
      body.adjustMass((( DynamicPhysicsObject ) obj).getMass());
      return body;
   }

   private PlaceableGeom createSphere( Sphere sphere, PhysicsObject obj, Spatial jmeGeo ) {
       float radius = sphere.radius * sphere.getWorldScale().x;
       return createSphere( obj, jmeGeo, radius, transformWorld(sphere.getCenter(), sphere, obj) );
   }

    private Vector3f transformWorld(Vector3f center, Spatial spatial, PhysicsObject obj) {
        return getRelativeRotation( spatial, obj.getSpatial() ).multLocal( center.mult( spatial.getWorldScale() ) );
    }

    private PlaceableGeom createSphere( PhysicsObject obj, Spatial jmeGeo, float radius, Vector3f center ) {
        GeomSphere odeGeo = new GeomSphere(obj.getName(), radius );
        odeGeo.setGraphics( jmeGeo );
        odeGeo.setPhysicsObject( obj );

        if ( center.x != 0 || center.y != 0 || center.z != 0 )
        {
            return translate( odeGeo, center );
        }
        else
        {
            return odeGeo;
        }
    }

    private PlaceableGeom createCylinder( Cylinder jmeGeo, PhysicsObject obj ) {

        GeomCappedCylinder odeGeo = new GeomCappedCylinder(obj.getName(), jmeGeo.getRadius()
                * jmeGeo.getWorldScale().y, jmeGeo.getHeight()
                * jmeGeo.getWorldScale().z);

        odeGeo.setGraphics( jmeGeo );
        odeGeo.setPhysicsObject( obj );

        return odeGeo;
    }

    private PlaceableGeom createBox( Box jmeGeo, PhysicsObject obj ) {

        // calculate the lengths and obtain the the world scale
        float xLength = jmeGeo.xExtent * 2 * jmeGeo.getWorldScale().x;
        float yLength = jmeGeo.yExtent * 2 * jmeGeo.getWorldScale().y;
        float zLength = jmeGeo.zExtent * 2 * jmeGeo.getWorldScale().z;

        return createBox( xLength, yLength, zLength, transformWorld( jmeGeo.getCenter(), jmeGeo, obj ), obj, jmeGeo );
    }

    private PlaceableGeom createBox( float xLength, float yLength, float zLength, Vector3f center, PhysicsObject obj, Spatial jmeGeo ) {
        GeomBox odeGeo = new GeomBox(jmeGeo.getName(), xLength, yLength, zLength);
        odeGeo.setGraphics( jmeGeo );
        odeGeo.setPhysicsObject( obj );

        if ( center.x != 0 || center.y != 0 || center.z != 0 )
        {
            return translate( odeGeo, center );
        }
        else
        {
            return odeGeo;
        }
    }

    private PlaceableGeom translate( PlaceableGeom odeGeo, Vector3f position ) {
        GeomTransform transform = new GeomTransform( odeGeo.getName() );
        transform.setEncapsulatedGeom( odeGeo );
        transform.setPhysicsObject( odeGeo.getPhysicsObject() );
        transform.setGraphics( odeGeo.getGraphics() );
        odeGeo.setPosition( position );
        return transform;
    }

    /**
     * Create a Physical object by compounding the children of that node into a
     * single body. This is is done in a recursive fashion.
     *
     * @param obj
     * @return the created physics object
     */
    private Placeable createCompoundedEntity(DynamicPhysicsObject obj) {
        // create the returning body
        Body body = new Body(obj.getName(), world);

        // Add all the Geometry attached to the Node to the body.
        ArrayList geoms = (ArrayList) stripNode((Node) obj.getSpatial(), new ArrayList() );
        for (int i = 0; i < geoms.size(); i++) {
            Geometry geometry = (Geometry) geoms.get(i);
            PlaceableGeom odeGeo;
            if (geometry instanceof Box) {
                odeGeo = createBox((Box) geometry, obj );
            } else if (geometry instanceof Sphere) {
                odeGeo = createSphere((Sphere) geometry, obj, geometry );
            } else if (geometry instanceof OrientedBox) {
                odeGeo = createOrientedBox((OrientedBox) geometry, obj, geometry );
            } else if ( geometry instanceof TriMesh )
            {
                //some unknown geometry, e.g. a trimesh
                odeGeo = createTriMesh( ( TriMesh ) geometry, obj );
            } else {
                throw new IllegalArgumentException();
            }
            odeGeo.setQuaternion( getRelativeRotation( geometry, obj.getSpatial() ) );
            body.addGeom( translate( odeGeo, geometry.getWorldTranslation().subtract(obj.getSpatial().getWorldTranslation()) ) );
        }

        body.adjustMass(obj.getMass());
        // Synchronize the bodys location and rotation to the Nodes.
        body.setQuaternion(obj.getSpatial().getWorldRotation());
        body.setPosition(obj.getSpatial().getWorldTranslation());

        return body;
    }

    /**
     * @param spatial spatial of interest
     * @param parent a parent Node of the spatial or the spatial itself
     * @return the rotation of spatial relative to parent
     */
    private Quaternion getRelativeRotation(Spatial spatial, Spatial parent) {
        Quaternion rotation = new Quaternion();
        while ( spatial != parent )
        {
            spatial.getLocalRotation().mult( rotation, rotation );
            spatial = spatial.getParent();
        }
        return rotation;
    }

    /**
     * Steps down a Node to return vital geoms that ODE can recognise which are
     * a box and a sphere really. If it isn't one of em, then approxmiate using
     * the bounding volume.
     *
     * @param node The node to strip for Geometry.
     * @param list list to add to
     * @return An ArrayList containing jME Geometry stored in the given Node.
     */
    private List stripNode( Node node, List list ) {
        for (int i = 0; i < node.getQuantity(); i++) {
            Spatial spat = node.getChild(i);
            if ( "particles".equals( spat.getName() ) )
            {
                LoggingSystem.getLogger().info( "ignoring spatial because it is named 'particles'" );
            }
            else
            {
                if (spat instanceof Geometry ) {
                    list.add(spat);
                } else if ( spat instanceof Node ) {
                    stripNode((Node) spat, list );
                }
            }
        }

        return list;
    }

    private PlaceableGeom createOrientedBox( OrientedBox box, PhysicsObject obj, Spatial associatedGraphics ) {
        Vector3f extents = box.getExtent().mult(box.getWorldScale());
        //todo: regard axes
        checkAxis( box.getxAxis(), box.getyAxis(), box.getzAxis() );
        return createBox( extents.x * 2, extents.y * 2, extents.z * 2,
                transformWorld( box.getCenter(), box, obj ), obj, associatedGraphics );
    }

    private PlaceableGeom createTriMesh( TriMesh jmeGeo, PhysicsObject obj ) {
        if (obj.isStatic()) {
            GeomTriMesh odeGeo = new GeomTriMesh(jmeGeo.getName(), jmeGeo );

            odeGeo.setGraphics( jmeGeo );
            odeGeo.setPhysicsObject( obj );

            return odeGeo;
        } else {
            // so its a trimesh, but its not static. To solve this, just use the
            // boundingVolume
            BoundingVolume bv = jmeGeo.getModelBound();
            Vector3f worldScale = jmeGeo.getWorldScale();
            if ( bv instanceof BoundingSphere ) {
                BoundingSphere sphere = ( (BoundingSphere) bv );
                if ( worldScale.x != worldScale.y || worldScale.x != worldScale.z )
                {
                    LoggingSystem.getLogger().warning( "Non-uniform scale for spheres not supported!" );
                }
                return createSphere( obj, jmeGeo, sphere.getRadius()*worldScale.x,
                        transformWorld( sphere.getCenter(), jmeGeo, obj ) );
            }
            else if ( bv instanceof BoundingBox ) {
                BoundingBox box = (BoundingBox) bv;
                return createBox(
                        box.xExtent*2*worldScale.x,
                        box.yExtent*2*worldScale.y,
                        box.zExtent*2*worldScale.z, transformWorld( box.getCenter(), jmeGeo, obj ), obj, jmeGeo );
            }
            else if ( bv instanceof OrientedBoundingBox ) {
                OrientedBoundingBox box = (OrientedBoundingBox) bv;
                //todo: regard axes
                checkAxis( box.getxAxis(), box.getyAxis(), box.getzAxis() );
                Vector3f extent = box.getExtent();
                return createBox(
                        extent.x*2*worldScale.x,
                        extent.y*2*worldScale.y,
                        extent.z*2*worldScale.z, transformWorld( box.getCenter(), jmeGeo, obj ), obj, jmeGeo );
            }
            else {
                // trimesh doesn't have a bounding box, throw an exception
                throw new JmeException( "Trimesh (" + jmeGeo.getName()
                        + ") does not have a bounding volume." );
            }
        }
    }

    private void checkAxis( Vector3f xAxis, Vector3f yAxis, Vector3f zAxis ) {
        if ( xAxis.x != 1 || yAxis.y != 1 || zAxis.z != 1 )
        {
            throw new RuntimeException( "Non-uniform oriented boxes are not supported!" );
        }
    }

    /**
     * Creates the ODE representation of a joint. The joint type is determied by
     * a call to Joint.getType(). If no joint type mathces, a JmeException is
     * thrown with the approperiate message.
     * <p>
     * Note: the user should <b>NEVER touch this method </b>, since it's only
     * used by the <code>Joint</code> (to obtain its type), during its
     * creation.
     *
     * @param joint
     * @return the physics joint
     */
    public org.odejava.Joint createPhysicalEntity(Joint joint) {
        // the return joint

        // see which type of joint it is
        int type = joint.getType();
        String name = joint.getName();

        // see which type of joint it is and create a joint for it.
        // else throw a jme exception
        switch (type) {
        case Joint.JT_HINGE:
            return new JointHinge(name, world);
        case Joint.JT_BALL:
            return new JointBall(name, world);
        case Joint.JT_HINGE2:
            return new JointHinge2(name, world);
        case Joint.JT_SLIDER:
            return new JointSlider(name, world);
        case Joint.JT_UNIVERSAL:
            return new JointUniversal(name, world);
        case Joint.JT_AMOTOR:
            return new JointAMotor(name, world);
        case Joint.JT_FIXED:
            return new JointFixed(name, world);
        default:
            throw new JmeException(
                    "Joint type is not defined. Please use an already defined joint type from com.jmex.physics.joints");
        }
    }

}
