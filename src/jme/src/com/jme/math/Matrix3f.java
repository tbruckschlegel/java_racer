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
package com.jme.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.Level;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>Matrix3f</code> defines a 3x3 matrix. Matrix data is maintained
 * internally and is acessible via the get and set methods. Convenience methods
 * are used for matrix operations as well as generating a matrix from a given
 * set of values.
 * 
 * @author Mark Powell
 * @author Joshua Slack -- Optimization
 * @version $Id: Matrix3f.java,v 1.32 2005/04/06 20:24:25 renanse Exp $
 */
public class Matrix3f {

    public float m00, m01, m02;

    public float m10, m11, m12;

    public float m20, m21, m22;

    private static final Vector3f tempVa = new Vector3f();

    /**
     * Constructor instantiates a new <code>Matrix3f</code> object. The
     * initial values for the matrix is that of the identity matrix.
     *  
     */
    public Matrix3f() {
        loadIdentity();
    }

    /**
     * constructs a matrix with the given values.
     * 
     * @param m00
     *            0x0 in the matrix.
     * @param m01
     *            0x1 in the matrix.
     * @param m02
     *            0x2 in the matrix.
     * @param m10
     *            1x0 in the matrix.
     * @param m11
     *            1x1 in the matrix.
     * @param m12
     *            1x2 in the matrix.
     * @param m20
     *            2x0 in the matrix.
     * @param m21
     *            2x1 in the matrix.
     * @param m22
     *            2x2 in the matrix.
     */
    public Matrix3f(float m00, float m01, float m02, float m10, float m11,
            float m12, float m20, float m21, float m22) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    /**
     * Copy constructor that creates a new <code>Matrix3f</code> object that
     * is the same as the provided matrix.
     * 
     * @param mat
     *            the matrix to copy.
     */
    public Matrix3f(Matrix3f mat) {
        copy(mat);
    }

    /**
     * <code>copy</code> transfers the contents of a given matrix to this
     * matrix. If a null matrix is supplied, this matrix is set to the identity
     * matrix.
     * 
     * @param matrix
     *            the matrix to copy.
     */
    public void copy(Matrix3f matrix) {
        if (null == matrix) {
            loadIdentity();
        } else {
            m00 = matrix.m00;
            m01 = matrix.m01;
            m02 = matrix.m02;
            m10 = matrix.m10;
            m11 = matrix.m11;
            m12 = matrix.m12;
            m20 = matrix.m20;
            m21 = matrix.m21;
            m22 = matrix.m22;
        }
    }

    /**
     * <code>get</code> retrieves a value from the matrix at the given
     * position. If the position is invalid a <code>JmeException</code> is
     * thrown.
     * 
     * @param i
     *            the row index.
     * @param j
     *            the colum index.
     * @return the value at (i, j).
     */
    public float get(int i, int j) {
        switch (i) {
        case 0:
            switch (j) {
            case 0: return m00;
            case 1: return m01;
            case 2: return m02;
            }
        case 1:
            switch (j) {
            case 0: return m10;
            case 1: return m11;
            case 2: return m12;
            }
        case 2:
            switch (j) {
            case 0: return m20;
            case 1: return m21;
            case 2: return m22;
            }
        }

        LoggingSystem.getLogger().log(Level.WARNING, "Invalid matrix index.");
        throw new JmeException("Invalid indices into matrix.");
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a <code>Vector3f</code> object.
     * 
     * @param i
     *            the column to retrieve. Must be between 0 and 2.
     * @return the column specified by the index.
     */
    public Vector3f getColumn(int i) {
        return getColumn(i, null);
    }

    /**
     * <code>getColumn</code> returns one of three columns specified by the
     * parameter. This column is returned as a <code>Vector3f</code> object.
     * 
     * @param i
     *            the column to retrieve. Must be between 0 and 2.
     * @param store
     *            the vector object to store the result in. if null, a new one
     *            is created.
     * @return the column specified by the index.
     */
    public Vector3f getColumn(int i, Vector3f store) {
        if (store == null) store = new Vector3f();
        switch (i) {
        case 0:
            store.x = m00;
            store.y = m10;
            store.z = m20;
            break;
        case 1:
            store.x = m01;
            store.y = m11;
            store.z = m21;
            break;
        case 2:
            store.x = m02;
            store.y = m12;
            store.z = m22;
            break;
        default:
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }
        return store;
    }

    /**
     * <code>toFloatBuffer</code> returns a FloatBuffer object that contains
     * the matrix data.
     * 
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer toFloatBuffer() {
        FloatBuffer fb = ByteBuffer.allocateDirect(9*4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();

        fb.put(m00).put(m01).put(m02);
        fb.put(m10).put(m11).put(m12);
        fb.put(m20).put(m21).put(m22);
        fb.rewind();
        return fb;
    }

    /**
     * <code>fillFloatBuffer</code> fills a FloatBuffer object with
     * the matrix data.
     * @param fb the buffer to fill, must be correct size
     * @return matrix data as a FloatBuffer.
     */
    public FloatBuffer fillFloatBuffer(FloatBuffer fb) {
        fb.clear();
        fb.put(m00).put(m01).put(m02);
        fb.put(m10).put(m11).put(m12);
        fb.put(m20).put(m21).put(m22);
        fb.rewind();
        return fb;
    }

    /**
     * 
     * <code>setColumn</code> sets a particular column of this matrix to that
     * represented by the provided vector.
     * 
     * @param i
     *            the column to set.
     * @param column
     *            the data to set.
     */
    public void setColumn(int i, Vector3f column) {

        if (column == null) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Column is null. Ignoring.");
            return;
        }
        switch (i) {
        case 0:
            m00 = column.x;
            m10 = column.y;
            m20 = column.z;
            break;
        case 1:
            m01 = column.x;
            m11 = column.y;
            m21 = column.z;
            break;
        case 2:
            m02 = column.x;
            m12 = column.y;
            m22 = column.z;
            break;
        default:
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Invalid column index.");
            throw new JmeException("Invalid column index. " + i);
        }
    }

    /**
     * <code>set</code> places a given value into the matrix at the given
     * position. If the position is invalid a <code>JmeException</code> is
     * thrown.
     * 
     * @param i
     *            the row index.
     * @param j
     *            the colum index.
     * @param value
     *            the value for (i, j).
     */
    public void set(int i, int j, float value) {
        switch (i) {
        case 0:
            switch (j) {
            case 0: m00 = value; return;
            case 1: m01 = value; return;
            case 2: m02 = value; return;
            }
        case 1:
            switch (j) {
            case 0: m10 = value; return;
            case 1: m11 = value; return;
            case 2: m12 = value; return;
            }
        case 2:
            switch (j) {
            case 0: m20 = value; return;
            case 1: m21 = value; return;
            case 2: m22 = value; return;
            }
        }

        LoggingSystem.getLogger().log(Level.WARNING, "Invalid matrix index.");
        throw new JmeException("Invalid indices into matrix.");
    }

    /**
     * 
     * <code>set</code> sets the values of the matrix to those supplied by the
     * 3x3 two dimenion array.
     * 
     * @param matrix
     *            the new values of the matrix.
     * @throws JmeException
     *             if the array is not of size 9.
     */
    public void set(float[][] matrix) {
        if (matrix.length != 3 || matrix[0].length != 3) { throw new JmeException(
        "Array must be of size 9."); }

        m00 = matrix[0][0];
        m01 = matrix[0][1];
        m02 = matrix[0][2];
        m10 = matrix[1][0];
        m11 = matrix[1][1];
        m12 = matrix[1][2];
        m20 = matrix[2][0];
        m21 = matrix[2][1];
        m22 = matrix[2][2];
    }

    /**
     * Recreate Matrix using the provided axis.
     * 
     * @param uAxis
     *            Vector3f
     * @param vAxis
     *            Vector3f
     * @param wAxis
     *            Vector3f
     */
    public void fromAxes(Vector3f uAxis, Vector3f vAxis, Vector3f wAxis) {
        m00 = uAxis.x;
        m10 = uAxis.y;
        m20 = uAxis.z;

        m01 = vAxis.x;
        m11 = vAxis.y;
        m21 = vAxis.z;

        m02 = wAxis.x;
        m12 = wAxis.y;
        m22 = wAxis.z;
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values assuming that the data is rowMajor order;
     * 
     * @param matrix
     *            the matrix to set the value to.
     */
    public void set(float[] matrix) {
        set(matrix, true);
    }

    /**
     * <code>set</code> sets the values of this matrix from an array of
     * values;
     * 
     * @param matrix
     *            the matrix to set the value to.
     * @param rowMajor
     *            whether the incoming data is in row or column major order.
     */
    public void set(float[] matrix, boolean rowMajor) {
        if (matrix.length != 9) throw new JmeException(
                "Array must be of size 9.");

        if (rowMajor) {
	        m00 = matrix[0];
	        m01 = matrix[1];
	        m02 = matrix[2];
	        m10 = matrix[3];
	        m11 = matrix[4];
	        m12 = matrix[5];
	        m20 = matrix[6];
	        m21 = matrix[7];
	        m22 = matrix[8];
        } else {
	        m00 = matrix[0];
	        m01 = matrix[3];
	        m02 = matrix[6];
	        m10 = matrix[1];
	        m11 = matrix[4];
	        m12 = matrix[7];
	        m20 = matrix[2];
	        m21 = matrix[5];
	        m22 = matrix[8];
        }
    }

    /**
     * 
     * <code>set</code> defines the values of the matrix based on a supplied
     * <code>Quaternion</code>. It should be noted that all previous values
     * will be overridden.
     * 
     * @param quaternion
     *            the quaternion to create a rotational matrix from.
     */
    public void set(Quaternion quaternion) {
        m00 = (float) (1.0 - 2.0 * quaternion.y * quaternion.y - 2.0
                * quaternion.z * quaternion.z);
        m10 = (float) (2.0 * quaternion.x * quaternion.y + 2.0 * quaternion.w
                * quaternion.z);
        m20 = (float) (2.0 * quaternion.x * quaternion.z - 2.0 * quaternion.w
                * quaternion.y);

        m01 = (float) (2.0 * quaternion.x * quaternion.y - 2.0 * quaternion.w
                * quaternion.z);
        m11 = (float) (1.0 - 2.0 * quaternion.x * quaternion.x - 2.0
                * quaternion.z * quaternion.z);
        m21 = (float) (2.0 * quaternion.y * quaternion.z + 2.0 * quaternion.w
                * quaternion.x);

        m02 = (float) (2.0 * quaternion.x * quaternion.z + 2.0 * quaternion.w
                * quaternion.y);
        m12 = (float) (2.0 * quaternion.y * quaternion.z - 2.0 * quaternion.w
                * quaternion.x);
        m22 = (float) (1.0 - 2.0 * quaternion.x * quaternion.x - 2.0
                * quaternion.y * quaternion.y);

    }

    /**
     * <code>loadIdentity</code> sets this matrix to the identity matrix.
     * Where all values are zero except those along the diagonal which are one.
     *  
     */
    public void loadIdentity() {
        m01 = m02 = m10 = m12 = m20 = m21 = 0;
        m00 = m11 = m22 = 1;
    }

    /**
     * <code>fromAngleAxis</code> sets this matrix4f to the values specified
     * by an angle and an axis of rotation.  This method creates an object, so
     * use fromAngleNormalAxis if your axis is already normalized.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation.
     */
    public void fromAngleAxis(float angle, Vector3f axis) {
        Vector3f normAxis = axis.normalize();
        fromAngleNormalAxis(angle, normAxis);
    }

    /**
     * <code>fromAngleNormalAxis</code> sets this matrix4f to the values
     * specified by an angle and a normalized axis of rotation.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized).
     */
    public void fromAngleNormalAxis(float angle, Vector3f axis) {
        float fCos = FastMath.cos(angle);
        float fSin = FastMath.sin(angle);
        float fOneMinusCos = ((float)1.0)-fCos;
        float fX2 = axis.x*axis.x;
        float fY2 = axis.y*axis.y;
        float fZ2 = axis.z*axis.z;
        float fXYM = axis.x*axis.y*fOneMinusCos;
        float fXZM = axis.x*axis.z*fOneMinusCos;
        float fYZM = axis.y*axis.z*fOneMinusCos;
        float fXSin = axis.x*fSin;
        float fYSin = axis.y*fSin;
        float fZSin = axis.z*fSin;
        
        m00 = fX2*fOneMinusCos+fCos;
        m01 = fXYM-fZSin;
        m02 = fXZM+fYSin;
        m10 = fXYM+fZSin;
        m11 = fY2*fOneMinusCos+fCos;
        m12 = fYZM-fXSin;
        m20 = fXZM-fYSin;
        m21 = fYZM+fXSin;
        m22 = fZ2*fOneMinusCos+fCos;
    }

    /**
     * <code>mult</code> multiplies this matrix by a given matrix. The result
     * matrix is returned as a new object. If the given matrix is null, a null
     * matrix is returned.
     * 
     * @param mat
     *            the matrix to multiply this matrix by.
     * @return the result matrix.
     */
    public Matrix3f mult(Matrix3f mat) {
        return mult(mat, null);
    }

    /**
     * <code>mult</code> multiplies this matrix by a given matrix. The result
     * matrix is returned as a new object. If the given matrix is null, a null
     * matrix is returned.
     * 
     * @param mat
     *            the matrix to multiply this matrix by.
     * @param product
     *            the matrix to store the result in. if null, a new matrix3f is
     *            created.
     * @return a matrix3f object containing the result of this operation
     */
    public Matrix3f mult(Matrix3f mat, Matrix3f product) {
        if (null == mat) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Source matrix is " + "null, null result returned.");
            return null;
        }

        if (product == null) product = new Matrix3f();
        product.m00 = m00 * mat.m00 + m01 * mat.m10 + m02 * mat.m20;
        product.m01 = m00 * mat.m01 + m01 * mat.m11 + m02 * mat.m21;
        product.m02 = m00 * mat.m02 + m01 * mat.m12 + m02 * mat.m22;
        product.m10 = m10 * mat.m00 + m11 * mat.m10 + m12 * mat.m20;
        product.m11 = m10 * mat.m01 + m11 * mat.m11 + m12 * mat.m21;
        product.m12 = m10 * mat.m02 + m11 * mat.m12 + m12 * mat.m22;
        product.m20 = m20 * mat.m00 + m21 * mat.m10 + m22 * mat.m20;
        product.m21 = m20 * mat.m01 + m21 * mat.m11 + m22 * mat.m21;
        product.m22 = m20 * mat.m02 + m21 * mat.m12 + m22 * mat.m22;
        return product;
    }

    /**
     * <code>mult</code> multiplies this matrix by a given
     * <code>Vector3f</code> object. The result vector is returned. If the
     * given vector is null, null will be returned.
     * 
     * @param vec
     *            the vector to multiply this matrix by.
     * @return the result vector.
     */
    public Vector3f mult(Vector3f vec) {
        return mult(vec, null);
    }

    /**
     * Multiplies this 3x3 matrix by the 1x3 Vector vec and stores the result in
     * product.
     * 
     * @param vec
     *            The Vector3f to multiply.
     * @param product
     *            The Vector3f to store the result.
     * @return The given product vector.
     */
    public Vector3f mult(Vector3f vec, Vector3f product) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Source vector is" + " null, null result returned.");
            return null;
        }

        if (null == product) {
            product = new Vector3f();
        }

        float x = vec.x;
        float y = vec.y;
        float z = vec.z;

        product.x = m00 * x + m01 * y + m02 * z;
        product.y = m10 * x + m11 * y + m12 * z;
        product.z = m20 * x + m21 * y + m22 * z;
        return product;
    }

    /**
     * <code>multLocal</code> multiplies this matrix internally by 
     * a given float scale factor.
     * 
     * @param scale
     *            the value to scale by.
     * @return this Matrix3f
     */
    public Matrix3f multLocal(float scale) {
        m00 *= scale;
        m01 *= scale;
        m02 *= scale;
        m10 *= scale;
        m11 *= scale;
        m12 *= scale;
        m20 *= scale;
        m21 *= scale;
        m22 *= scale;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies this matrix by a given
     * <code>Vector3f</code> object. The result vector is stored inside the
     * passed vector, then returned . If the given vector is null, null will be
     * returned.
     * 
     * @param vec
     *            the vector to multiply this matrix by.
     * @return The passed vector after multiplication
     */
    public Vector3f multLocal(Vector3f vec) {
        if (vec == null) return null;
        float x = vec.x;
        float y = vec.y;
        vec.x = m00 * x + m01 * y + m02 * vec.z;
        vec.y = m10 * x + m11 * y + m12 * vec.z;
        vec.z = m20 * x + m21 * y + m22 * vec.z;
        return vec;
    }

    /**
     * <code>mult</code> multiplies this matrix by a given matrix. The result
     * matrix is saved in the current matrix. If the given matrix is null,
     * nothing happens. The current matrix is returned. This is equivalent to
     * this*=mat
     * 
     * @param mat
     *            the matrix to multiply this matrix by.
     * @return This matrix, after the multiplication
     */
    public Matrix3f multLocal(Matrix3f mat) {
        if (mat == null) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Source matrix is " + "null, null result returned.");
            return null;
        }
        float f00 = m00 * mat.m00 + m01 * mat.m10 + m02 * mat.m20;
        float f01 = m00 * mat.m01 + m01 * mat.m11 + m02 * mat.m21;
        this.m02 = m00 * mat.m02 + m01 * mat.m12 + m02 * mat.m22;
        float f10 = m10 * mat.m00 + m11 * mat.m10 + m12 * mat.m20;
        float f11 = m10 * mat.m01 + m11 * mat.m11 + m12 * mat.m21;
        this.m12 = m10 * mat.m02 + m11 * mat.m12 + m12 * mat.m22;

        float f20 = m20 * mat.m00 + m21 * mat.m10 + m22 * mat.m20;
        float f21 = m20 * mat.m01 + m21 * mat.m11 + m22 * mat.m21;
        this.m22 = m20 * mat.m02 + m21 * mat.m12 + m22 * mat.m22;

        m00 = f00;
        m01 = f01;
        m10 = f10;
        m11 = f11;
        m20 = f20;
        m21 = f21;

        return this;
    }

    /**
     * Transposes this matrix in place. Returns this matrix for chaining
     * 
     * @return This matrix after transpose
     */
    public Matrix3f transposeLocal() {
        float temp;
        temp = m01;
        m01 = m10;
        m10 = temp;
        temp = m02;
        m02 = m20;
        m20 = temp;
        temp = m21;
        m21 = m12;
        m12 = temp;
        return this;
    }

    /**
     * Inverts this matrix as a new Matrix3f.
     * 
     * @return The new inverse matrix
     */
    public Matrix3f invert() {
        return invert(null);
    }

    /**
     * Inverts this matrix and stores it in the given store.
     * 
     * @return The store
     */
    public Matrix3f invert(Matrix3f store) {
        if (store == null) store = new Matrix3f();

        float det = determinant();
        if ( FastMath.abs(det) <= FastMath.FLT_EPSILON )
            return store.zero();

        store.m00 = m11*m22 - m12*m21;
        store.m01 = m02*m21 - m01*m22;
        store.m02 = m01*m12 - m02*m11;
        store.m10 = m12*m20 - m10*m22;
        store.m11 = m00*m22 - m02*m20;
        store.m12 = m02*m10 - m00*m12;
        store.m20 = m10*m21 - m11*m20;
        store.m21 = m01*m20 - m00*m21;
        store.m22 = m00*m11 - m01*m10;

        store.multLocal(1f/det);
        return store;
    }

    /**
     * Inverts this matrix locally.
     * 
     * @return this
     */
    public Matrix3f invertLocal() {
        float det = determinant();
        if ( FastMath.abs(det) <= FastMath.FLT_EPSILON )
            return zero();

        float f00 = m11*m22 - m12*m21;
        float f01 = m02*m21 - m01*m22;
        float f02 = m01*m12 - m02*m11;
        float f10 = m12*m20 - m10*m22;
        float f11 = m00*m22 - m02*m20;
        float f12 = m02*m10 - m00*m12;
        float f20 = m10*m21 - m11*m20;
        float f21 = m01*m20 - m00*m21;
        float f22 = m00*m11 - m01*m10;
        
        m00 = f00;
        m01 = f01;
        m02 = f02;
        m10 = f10;
        m11 = f11;
        m12 = f12;
        m20 = f20;
        m21 = f21;
        m22 = f22;

        multLocal(1f/det);
        return this;
    }
    
    /**
     * Returns a new matrix representing the adjoint of this matrix.
     * 
     * @return The adjoint matrix
     */
    public Matrix3f adjoint() {
        return adjoint(null);
    }
    
    /**
     * Places the adjoint of this matrix in store (creates store if null.)
     * 
     * @param store
     *            The matrix to store the result in.  If null, a new matrix is created.
     * @return store
     */
    public Matrix3f adjoint(Matrix3f store) {
        if (store == null) store = new Matrix3f();

        store.m00 = m11*m22 - m12*m21;
        store.m01 = m02*m21 - m01*m22;
        store.m02 = m01*m12 - m02*m11;
        store.m10 = m12*m20 - m10*m22;
        store.m11 = m00*m22 - m02*m20;
        store.m12 = m02*m10 - m00*m12;
        store.m20 = m10*m21 - m11*m20;
        store.m21 = m01*m20 - m00*m21;
        store.m22 = m00*m11 - m01*m10;

        return store;
    }

    /**
     * <code>determinant</code> generates the determinate of this matrix.
     * 
     * @return the determinate
     */
    public float determinant() {
        float fCo00 = m11*m22 - m12*m21;
        float fCo10 = m12*m20 - m10*m22;
        float fCo20 = m10*m21 - m11*m20;
        float fDet = m00*fCo00 + m01*fCo10 + m02*fCo20;
        return fDet;
    }

    /**
     * Sets all of the values in this matrix to zero.
     * 
     * @return this matrix
     */
    public Matrix3f zero() {
        m00 = m01 = m02 = m10 = m11 = m12 = m20 = m21 = m22 = 0.0f;
        return this;
    }

    /**
     * <code>add</code> adds the values of a parameter matrix to this matrix.
     * 
     * @param mat
     *            the matrix to add to this.
     */
    public void add(Matrix3f mat) {
        m00 += mat.m00;
        m01 += mat.m01;
        m02 += mat.m02;
        m10 += mat.m10;
        m11 += mat.m11;
        m12 += mat.m12;
        m20 += mat.m20;
        m21 += mat.m21;
        m22 += mat.m22;
    }

    /**
     * <code>transpose</code> locally transposes this Matrix.
     * 
     * @return this object for chaining.
     */
    public Matrix3f transpose() {
        float temp = 0;
        temp = m01;
        m01 = m10;
        m10 = temp;

        temp = m02;
        m02 = m20;
        m20 = temp;

        temp = m12;
        m12 = m21;
        m21 = temp;
        
        return this;
    }
    
    /**
     * 
     * <code>fromAxisAngle</code> creates a rotational matrix given an axis
     * and an angle. The angle is expected to be in radians.
     * 
     * @param axis
     *            the axis to rotate about.
     * @param radian
     *            the angle to rotate.
     */
    public void fromAxisAngle(Vector3f axis, float radian) {
        Vector3f normAxis = tempVa.set(axis).normalizeLocal();
        float cos = FastMath.cos(radian);
        float sin = FastMath.sin(radian);
        float oneMinusCos = 1.0f - cos;
        float x2 = normAxis.x * axis.x;
        float y2 = normAxis.y * axis.y;
        float z2 = normAxis.z * axis.z;
        float xym = normAxis.x * axis.y * oneMinusCos;
        float xzm = normAxis.x * axis.z * oneMinusCos;
        float yzm = normAxis.y * axis.z * oneMinusCos;
        float xSin = normAxis.x * sin;
        float ySin = normAxis.y * sin;
        float zSin = normAxis.z * sin;

        m00 = x2 * oneMinusCos + cos;
        m01 = xym - zSin;
        m02 = xzm + ySin;
        m10 = xym + zSin;
        m11 = y2 * oneMinusCos + cos;
        m12 = yzm - xSin;
        m20 = xzm - ySin;
        m21 = yzm + xSin;
        m22 = z2 * oneMinusCos + cos;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * It is in a format of a 3x3 matrix. For example, an identity matrix would
     * be represented by the following string. com.jme.math.Matrix3f <br>[<br>
     * 1.0  0.0  0.0 <br>
     * 0.0  1.0  0.0 <br>
     * 0.0  0.0  1.0 <br>]<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        StringBuffer result = new StringBuffer("com.jme.math.Matrix3f\n[\n");
        result.append(" ");
        result.append(m00);
        result.append("  ");
        result.append(m01);
        result.append("  ");
        result.append(m02);
        result.append(" \n");
        result.append(" ");
        result.append(m10);
        result.append("  ");
        result.append(m11);
        result.append("  ");
        result.append(m12);
        result.append(" \n");
        result.append(" ");
        result.append(m20);
        result.append("  ");
        result.append(m21);
        result.append("  ");
        result.append(m22);
        result.append(" \n]");
        return result.toString();
    }
}