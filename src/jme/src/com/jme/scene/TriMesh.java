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
package com.jme.scene;

import java.io.Serializable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.ArrayList;

import com.jme.intersection.CollisionResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.bounding.OBBTree;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating triangles on every
 * three points.
 * 
 * @author Mark Powell
 * @version $Id: TriMesh.java,v 1.40 2005/05/24 22:47:30 Mojomonkey Exp $
 */
public class TriMesh extends com.jme.scene.Geometry implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int[] indices;

    private transient IntBuffer indexBuffer;

    protected int triangleQuantity = -1;

    /** This tree is only built on calls too updateCollisionTree. */
    private OBBTree collisionTree;

    /** This is and should only be used by collision detection. */
    private Matrix3f worldMatrot;

    /**
     * Empty Constructor to be used internally only.
     */
    public TriMesh() {
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public TriMesh(String name) {
        super(name);

    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object. Provided
     * are the attributes that make up the mesh all attributes may be null,
     * except for vertices and indices.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param vertices
     *            the vertices of the geometry.
     * @param normal
     *            the normals of the geometry.
     * @param color
     *            the colors of the geometry.
     * @param texture
     *            the texture coordinates of the mesh.
     * @param indices
     *            the indices of the vertex array.
     */
    public TriMesh(String name, Vector3f[] vertices, Vector3f[] normal,
            ColorRGBA[] color, Vector2f[] texture, int[] indices) {

        super(name, vertices, normal, color, texture);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        this.indices = indices;
        triangleQuantity = indices.length / 3;
        updateIndexBuffer();
        LoggingSystem.getLogger().log(Level.INFO, "TriMesh created.");
    }

    /**
     * Recreates the geometric information of this TriMesh from scratch. The
     * index and vertex array must not be null, but the others may be. Every 3
     * indices define an index in the <code>vertices</code> array that
     * refrences a vertex of a triangle.
     * 
     * @param vertices
     *            The vertex information for this TriMesh.
     * @param normal
     *            The normal information for this TriMesh.
     * @param color
     *            The color information for this TriMesh.
     * @param texture
     *            The texture information for this TriMesh.
     * @param indices
     *            The index information for this TriMesh.
     * @see #reconstruct(com.jme.math.Vector3f[], com.jme.math.Vector3f[],
     *      com.jme.renderer.ColorRGBA[], com.jme.math.Vector2f[])
     */
    public void reconstruct(Vector3f[] vertices, Vector3f[] normal,
            ColorRGBA[] color, Vector2f[] texture, int[] indices) {

        super.reconstruct(vertices, normal, color, texture);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        this.indices = indices;
        triangleQuantity = indices.length / 3;

        updateIndexBuffer();
        LoggingSystem.getLogger().log(Level.INFO, "TriMesh reconstructed.");
    }

    /**
     * 
     * <code>getIndices</code> retrieves the indices into the vertex array.
     * 
     * @return the indices into the vertex array.
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * 
     * <code>getIndexAsBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexAsBuffer() {
        return indexBuffer;
    }

    /**
     * Stores in the <code>storage</code> array the indices of triangle
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<3</code>, then nothing happens
     * 
     * @param i
     *            The index of the triangle to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getTriangle(int i, int[] storage) {
        if (i < triangleQuantity && storage.length >= 3) {

            int iBase = 3 * i;
            storage[0] = indices[iBase++];
            storage[1] = indices[iBase++];
            storage[2] = indices[iBase];
        }
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle
     * <code>i</code>. If <code>i</code> is an invalid triangle index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getTriangle(int i, Vector3f[] vertices) {
        // System.out.println(i + ", " + triangleQuantity);
        if (i < triangleQuantity && i >= 0) {
            int iBase = 3 * i;
            vertices[0] = vertex[indices[iBase++]];
            vertices[1] = vertex[indices[iBase++]];
            vertices[2] = vertex[indices[iBase]];
        }
    }

    /**
     * Returns the number of triangles this TriMesh contains.
     * 
     * @return The current number of triangles.
     */
    public int getTriangleQuantity() {
        return triangleQuantity;
    }

    /**
     * 
     * <code>setIndices</code> sets the index array for this
     * <code>TriMesh</code>.
     * 
     * @param indices
     *            the index array.
     */
    public void setIndices(int[] indices) {
        this.indices = indices;
        triangleQuantity = indices.length / 3;
        updateIndexBuffer();
    }

    /**
     * <code>draw</code> calls super to set the render state then passes
     * itself to the renderer.
     * 
     * LOGIC: 1. If we're not RenderQueue calling draw goto 2, if we are, goto 3
     * 2. If we are supposed to use queue, add to queue and RETURN, else 3 3.
     * call super draw 4. tell renderer to draw me.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }
        super.draw(r);
        r.draw(this);
    }

    /**
     * <code>drawBounds</code> calls super to set the render state then passes
     * itself to the renderer.
     * 
     * @param r
     *            the renderer to display
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(this);
    }


    public void updateIndexBuffer() {
        if (indices == null) {
            return;
        }
        if (indexBuffer == null || indexBuffer.capacity() < indices.length) {
            indexBuffer = ByteBuffer.allocateDirect(
                    4 * (triangleQuantity >= 0 ? triangleQuantity * 3
                            : indices.length)).order(ByteOrder.nativeOrder())
                    .asIntBuffer();
        }

        indexBuffer.clear();
        indexBuffer.put(indices, 0,
                triangleQuantity >= 0 ? triangleQuantity * 3 : indices.length);
        indexBuffer.flip();

    }

    /**
     * Clears the buffers of this TriMesh. The buffers include its indexBuffer,
     * and all Geometry buffers.
     */
    public void clearBuffers() {
        super.clearBuffers();
        indexBuffer = null;
    }

    /**
     * Sets this geometry's index buffer as a refrence to the passed
     * <code>IntBuffer</code>. Incorrectly built IntBuffers can have
     * undefined results. Use with care.
     * 
     * @param toSet
     *            The <code>IntBuffer</code> to set this geometry's index
     *            buffer to
     */
    public void setIndexBuffer(IntBuffer toSet) {
        indexBuffer = toSet;
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        updateIndexBuffer();
    }

    /**
     * This function creates a collision tree from the TriMesh's current
     * information. If the information changes, the tree needs to be updated.
     */
    public void updateCollisionTree() {
        if (collisionTree == null)
            collisionTree = new OBBTree();
        collisionTree.construct(this);
    }

    /**
     * determines if a collision between this trimesh and a given spatial occurs
     * if it has true is returned, otherwise false is returned.
     * 
     */
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        if (this == scene) {
            return false;
        }
        if (getWorldBound().intersects(scene.getWorldBound())) {
            if ((scene instanceof Node)) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    if (hasCollision(parent.getChild(i), checkTriangles)) {
                        return true;
                    }
                }

                return false;
            } else {
                if (!checkTriangles) {
                    return true;
                } else {
                    return hasTriangleCollision((TriMesh) scene);
                }
            }
        } else {
            return false;
        }
    }

    /**
     * determines if this TriMesh has made contact with the give scene. The
     * scene is recursively transversed until a trimesh is found, at which time
     * the two trimesh OBBTrees are then compared to find the triangles that
     * hit.
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        if (this == scene) {
            return;
        }

        if (getWorldBound().intersects(scene.getWorldBound())) {
            if ((scene instanceof Node)) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    findCollisions(parent.getChild(i), results);
                }
            } else {
                results.addCollision(this, (Geometry) scene);
            }
        }
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck) {
        if (collisionTree == null || toCheck.collisionTree == null)
            return false;
        else {
            if (worldMatrot == null)
                worldMatrot = worldRotation.toRotationMatrix();
            else
                worldMatrot = worldRotation.toRotationMatrix(worldMatrot);
            collisionTree.bounds.transform(worldMatrot, worldTranslation,
                    worldScale, collisionTree.worldBounds);
            toCheck.worldMatrot = toCheck.worldRotation.toRotationMatrix();
            return collisionTree.intersect(toCheck.collisionTree);
        }
    }

    /**
     * This function finds all intersections between this trimesh and the
     * checking one. The intersections are stored as Integer objects of Triangle
     * indexes in each of the parameters.
     * 
     * @param toCheck
     *            The TriMesh to check.
     * @param thisIndex
     *            The array of triangle indexes intersecting in this mesh.
     * @param otherIndex
     *            The array of triangle indexes intersecting in the given mesh.
     */
    public void findTriangleCollision(TriMesh toCheck, ArrayList thisIndex,
            ArrayList otherIndex) {
        if (collisionTree == null || toCheck.collisionTree == null)
            return;
        else {
            if (worldMatrot == null)
                worldMatrot = worldRotation.toRotationMatrix();
            else
                worldMatrot = worldRotation.toRotationMatrix(worldMatrot);
            collisionTree.bounds.transform(worldMatrot, worldTranslation,
                    worldScale, collisionTree.worldBounds);
            toCheck.worldMatrot = toCheck.worldRotation.toRotationMatrix();
            collisionTree.intersect(toCheck.collisionTree, thisIndex,
                    otherIndex);
        }
    }

    /**
     * 
     * <code>findTrianglePick</code> determines the triangles of this trimesh
     * that are being touched by the ray. The indices of the triangles are
     * stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test.
     * @param results
     *            the indices to the triangles.
     */
    public void findTrianglePick(Ray toTest, ArrayList results) {
        if (worldBound == null) {
            return;
        }
        if (worldBound.intersects(toTest)) {
            if (worldMatrot == null) {
                worldMatrot = worldRotation.toRotationMatrix();
            } else {
                worldRotation.toRotationMatrix(worldMatrot);

                if (collisionTree == null) {
                    updateCollisionTree();
                }
                collisionTree.bounds.transform(worldMatrot, worldTranslation,
                        worldScale, collisionTree.worldBounds);
                collisionTree.intersect(toTest, results);
            }
        }
    }

    /**
     * This function is <b>ONLY </b> to be used by the intersection testing
     * code. It should not be called by users. It returns a matrix3f
     * representation of the mesh's world rotation.
     * 
     * @return This mesh's world rotation.
     */
    public Matrix3f findWorldRotMat() {
        return worldMatrot;
    }

    /**
     * sets the attributes of this TriMesh into a given spatial. What is to be
     * stored is contained in the properties parameter.
     * 
     * @param store
     *            the Spatial to clone to.
     * @param properties
     *            the CloneCreator object that defines what is to be cloned.
     */
    public Spatial putClone(Spatial store, CloneCreator properties) {
        TriMesh toStore;
        if (store == null) {
            toStore = new TriMesh(this.getName() + "copy");
        } else {
            toStore = (TriMesh) store;
        }
        super.putClone(toStore, properties);

        if (properties.isSet("indices")) {
            toStore.indices = this.indices;
            toStore.indexBuffer = this.indexBuffer;
            toStore.triangleQuantity = this.triangleQuantity;
        } else {
            int[] temp = new int[this.indices.length];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = this.indices[i];
            }
            toStore.setIndices(temp);
        }

        if (properties.isSet("obbtree")) {
            toStore.collisionTree = this.collisionTree;
        }
        
        toStore.useDynamicVBOIndex = this.useDynamicVBOIndex;
        toStore.useStaticVBOIndex = this.useStaticVBOIndex;

        return toStore;
    }

    /**
     * Return this mesh object as triangles. Every 3 vertices returned compose
     * single triangle. Vertices are returned by reference for efficiency, so it
     * is required that they won't be modified by caller.
     * 
     * @return view of current mesh as group of triangle vertices
     */
    public Vector3f[] getMeshAsTriangles() {
        Vector3f[] triangles = new Vector3f[indices.length];
        for (int i = 0; i < triangles.length; i++) {
            triangles[i] = vertex[indices[i]];
        }
        return triangles;
    }

    private boolean useStaticVBOIndex = false;
    private boolean useDynamicVBOIndex = false;
    private int vboIndexID = -1;

    
    /**
     * Returns true if dynamic VBO (Index Buffer) is enabled for index information.
     * This is used during rendering.
     *
     * @return If dynamic VBO is enabled for indices.
     */
    public boolean isDynamicVBOIndexEnabled() {
        return useDynamicVBOIndex;
    }

    /**
     * Enables or disables Vertex Buffer Objects for index information.
     *
     * @param enabled
     *            If true, static VBO enabled for indices.
     */
    public void setDynamicVBOIndexEnabled(boolean enabled) {
        useDynamicVBOIndex = enabled;
    }

    /**
     * Returns true if static VBO (Index Buffer) is enabled for index information.
     * This is used during rendering.
     *
     * @return If static VBO is enabled for indices.
     */
    public boolean isStaticVBOIndexEnabled() {
        return useStaticVBOIndex;
    }
    
    /**
     * Enables or disables Index Buffer Objects for index information.
     *
     * @param enabled
     *            If true, static VBO enabled for indices.
     */
    public void setStaticVBOIndexEnabled(boolean enabled) {
        useStaticVBOIndex = enabled;
    }

    
    public int getVBOIndexID() {
        return vboIndexID;
    }

    public void setVBOIndexID(int id) {
        vboIndexID = id;
    }

}