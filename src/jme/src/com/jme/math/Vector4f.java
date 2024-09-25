package com.jme.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;

/*
 * -- Added *Local methods to cut down on object creation - JS
 */

/**
 * <code>Vector4f</code> defines a Vector for a three float value tuple.
 * <code>Vector4f</code> can represent any three dimensional value, such as a
 * vertex, a normal, etc. Utility methods are also included to aid in
 * mathematical calculations.
 *
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Vector4f.java,v 1.31 2005/05/30 16:19:17 renanse Exp $
 */
public class Vector4f implements Externalizable, Cloneable{

    private static final long serialVersionUID = 1L;

    /**
     * the x value of the vector.
     */
    public float x;

    /**
     * the y value of the vector.
     */
    public float y;

    /**
     * the z value of the vector.
     */
    public float z;

    /**
     * the w value of the vector.
     */
    public float w;

    /**
     * Constructor instantiates a new <code>Vector4f</code> with default
     * values of (0,0,0).
     *
     */
    public Vector4f() {
        x = y = z = w = 0;
    }

    /**
     * Constructor instantiates a new <code>Vector4f</code> with provides
     * values.
     *
     * @param x
     *            the x value of the vector.
     * @param y
     *            the y value of the vector.
     * @param z
     *            the z value of the vector.
     */
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Constructor instantiates a new <code>Vector4f</code> that is a copy
     * of the provided vector
     * @param copy The Vector4f to copy
     */
    public Vector4f(Vector4f copy) {
        this.set(copy);
    }

    /**
     * <code>set</code> sets the x,y,z values of the vector based on passed
     * parameters.
     *
     * @param x
     *            the x value of the vector.
     * @param y
     *            the y value of the vector.
     * @param z
     *            the z value of the vector.
     * @return this vector
     */
    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * <code>set</code> sets the x,y,z values of the vector by copying the
     * supplied vector.
     *
     * @param vect
     *            the vector to copy.
     * @return this vector
     */
    public Vector4f set(Vector4f vect) {
        this.x = vect.x;
        this.y = vect.y;
        this.z = vect.z;
        this.w = vect.w;
        return this;
    }

    /**
     *
     * <code>add</code> adds a provided vector to this vector creating a
     * resultant vector which is returned. If the provided vector is null, null
     * is returned.
     *
     * @param vec
     *            the vector to add to this.
     * @return the resultant vector.
     */
    public Vector4f add(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, null returned.");
            return null;
        }
        return new Vector4f(x + vec.x, y + vec.y, z + vec.z, w + vec.w);
    }

    /**
     *
     * <code>add</code> adds the values of a provided vector storing the
     * values in the supplied vector.
     *
     * @param vec
     *            the vector to add to this
     * @param result
     *            the vector to store the result in
     * @return result returns the supplied result vector.
     */
    public Vector4f add(Vector4f vec, Vector4f result) {
        result.x = x + vec.x;
        result.y = y + vec.y;
        result.z = z + vec.z;
        result.w = w + vec.w;
        return result;
    }

    /**
     * <code>addLocal</code> adds a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls. If the
     * provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to add to this vector.
     * @return this
     */
    public Vector4f addLocal(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, null returned.");
            return null;
        }
        x += vec.x;
        y += vec.y;
        z += vec.z;
        w += vec.w;
        return this;
    }

    /**
     *
     * <code>add</code> adds the provided values to this vector, creating a
     * new vector that is then returned.
     *
     * @param addX
     *            the x value to add.
     * @param addY
     *            the y value to add.
     * @param addZ
     *            the z value to add.
     * @return the result vector.
     */
    public Vector4f add(float addX, float addY, float addZ, float addW) {
        return new Vector4f(x + addX, y + addY, z + addZ, w + addW);
    }

    /**
     * <code>addLocal</code> adds the provided values to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls.
     *
     * @param addX
     *            value to add to x
     * @param addY
     *            value to add to y
     * @param addZ
     *            value to add to z
     * @return this
     */
    public Vector4f addLocal(float addX, float addY, float addZ, float addW) {
        x += addX;
        y += addY;
        z += addZ;
        w += addW;
        return this;
    }

    /**
     *
     * <code>scaleAdd</code> multiplies this vector by a scalar then adds the
     * given Vector4f.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @param add
     *            the value to add
     */
    public void scaleAdd(float scalar, Vector4f add) {
        x = x * scalar + add.x;
        y = y * scalar + add.y;
        z = z * scalar + add.z;
        w = w * scalar + add.w;
    }

    /**
     *
     * <code>scaleAdd</code> multiplies the given vector by a scalar then adds
     * the given vector.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @param mult
     *            the value to multiply the scalar by
     * @param add
     *            the value to add
     */
    public void scaleAdd(float scalar, Vector4f mult, Vector4f add) {
        this.x = mult.x * scalar + add.x;
        this.y = mult.y * scalar + add.y;
        this.z = mult.z * scalar + add.z;
        this.w = mult.w * scalar + add.w;
    }

    /**
     *
     * <code>dot</code> calculates the dot product of this vector with a
     * provided vector. If the provided vector is null, 0 is returned.
     *
     * @param vec
     *            the vector to dot with this vector.
     * @return the resultant dot product of this vector and a given vector.
     */
    public float dot(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, 0 returned.");
            return 0;
        }
        return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
    }

    /**
     * <code>cross</code> calculates the cross product of this vector with a
     * parameter vector v.
     *
     * @param v
     *            the vector to take the cross product of with this.
     * @return the cross product vector.
     */
    private float A, B, C, D, E, F;       // Intermediate Values

    public Vector4f cross(Vector4f v) {
            // Calculate intermediate values.

            A = (x * v.y) - (y * v.x);
            B = (x * v.z) - (z * v.x);
            C = (x * v.w) - (w * v.x);
            D = (y * v.w) - (z * v.y);
            E = (y * v.w) - (w * v.y);
            F = (z * v.w) - (w * v.z);

            // Calculate the result-vector components.

            return new Vector4f((y * F) - (z * E) + (w * D),
                                - (x * F) + (z * C) - (w * B),
                                (x * E) - (y * C) + (w * A),
                                - (x * D) + (y * B) - (z * A));

    }

    /**
     * <code>cross</code> calculates the cross product of this vector with a
     * parameter vector v.  The result is stored in <code>result</code>
     *
     * @param v
     *            the vector to take the cross product of with this.
     * @param result
     *            the vector to store the cross product result.
     * @return result, after recieving the cross product vector.
     */
    public Vector4f cross(Vector4f v,Vector4f result) {
        // Calculate intermediate values.

        A = (x * v.y) - (y * v.x);
        B = (x * v.z) - (z * v.x);
        C = (x * v.w) - (w * v.x);
        D = (y * v.w) - (z * v.y);
        E = (y * v.w) - (w * v.y);
        F = (z * v.w) - (w * v.z);

        // Calculate the result-vector components.

        result.x =  (y * F) - (z * E) + (w * D);
        result.y = -(x * F) + (z * C) - (w * B);
        result.z =  (x * E) - (y * C) + (w * A);
        result.w = -(x * D) + (y * B) - (z * A);

        return result;
    }

    /**
     * <code>length</code> calculates the magnitude of this vector.
     *
     * @return the length or magnitude of the vector.
     */
    public float length() {
        return FastMath.sqrt(lengthSquared());
    }

    /**
     * <code>lengthSquared</code> calculates the squared value of the
     * magnitude of the vector.
     *
     * @return the magnitude squared of the vector.
     */
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    /**
     * <code>distanceSquared</code> calculates the distance squared between
     * this vector and vector v.
     *
     * @param v the second vector to determine the distance squared.
     * @return the distance squared between the two vectors.
     */
    public float distanceSquared(Vector4f v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;        
        double dw = w - v.w;
        return (float) (dx * dx + dy * dy + dz * dz + dw * dw);
    }

    /**
     * <code>distance</code> calculates the distance between this vector and
     * vector v.
     *
     * @param v the second vector to determine the distance.
     * @return the distance between the two vectors.
     */
    public float distance(Vector4f v) {
        return FastMath.sqrt(distanceSquared(v));
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is returned.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @return the new vector.
     */
    public Vector4f mult(float scalar) {
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is supplied as the second parameter and returned.
     *
     * @param scalar the scalar to multiply this vector by.
     * @param product the product to store the result in.
     * @return product
     */
    public Vector4f mult(float scalar, Vector4f product) {
        if (null == product) {
            product = new Vector4f();
        }

        product.x = x * scalar;
        product.y = y * scalar;
        product.z = z * scalar;
        product.w = w * scalar;
        return product;
    }

    /**
     * <code>multLocal</code> multiplies this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @return this
     */
    public Vector4f multLocal(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to mult to this vector.
     * @return this
     */
    public Vector4f multLocal(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, null returned.");
            return null;
        }
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        w *= vec.w;
        return this;
    }


    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to mult to this vector.
     * @return this
     */
    public Vector4f mult(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, null returned.");
            return null;
        }
        return new Vector4f(x * vec.x, y * vec.y, z * vec.z, w * vec.w);
    }


    /**
     * <code>divide</code> divides the values of this vector by a scalar and
     * returns the result. The values of this vector remain untouched.
     *
     * @param scalar
     *            the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector4f divide(float scalar) {
        scalar = 1f/scalar;
        return new Vector4f(x * scalar, y * scalar, z * scalar, z * scalar);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls. Dividing
     * by zero will result in an exception.
     *
     * @param scalar
     *            the value to divides this vector by.
     * @return this
     */
    public Vector4f divideLocal(float scalar) {
        scalar = 1f/scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
        return this;
    }


    /**
     * <code>divide</code> divides the values of this vector by a scalar and
     * returns the result. The values of this vector remain untouched.
     *
     * @param scalar
     *            the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector4f divide(Vector4f scalar) {
        return new Vector4f(x / scalar.x, y / scalar.y, z / scalar.z, w / scalar.w);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls. Dividing
     * by zero will result in an exception.
     *
     * @param scalar
     *            the value to divides this vector by.
     * @return this
     */
    public Vector4f divideLocal(Vector4f scalar) {
        x /= scalar.x;
        y /= scalar.y;
        z /= scalar.z;
        w /= scalar.w;
        return this;
    }

    /**
     *
     * <code>negate</code> returns the negative of this vector. All values are
     * negated and set to a new vector.
     *
     * @return the negated vector.
     */
    public Vector4f negate() {
        return new Vector4f(-x, -y, -z, -w);
    }

    /**
     *
     * <code>negateLocal</code> negates the internal values of this vector.
     *
     * @return this.
     */
    public Vector4f negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }

    /**
     *
     * <code>subtract</code> subtracts the values of a given vector from those
     * of this vector creating a new vector object. If the provided vector is
     * null, null is returned.
     *
     * @param vec
     *            the vector to subtract from this vector.
     * @return the result vector.
     */
    public Vector4f subtract(Vector4f vec) {
        return new Vector4f(x - vec.x, y - vec.y, z - vec.z, w - vec.w);
    }

    /**
     * <code>subtractLocal</code> subtracts a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to subtract
     * @return this
     */
    public Vector4f subtractLocal(Vector4f vec) {
        if (null == vec) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Provided vector is " + "null, null returned.");
            return null;
        }
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        w -= vec.w;
        return this;
    }

    /**
     *
     * <code>subtract</code>
     *
     * @param vec
     *            the vector to subtract from this
     * @param result
     *            the vector to store the result in
     * @return result
     */
    public Vector4f subtract(Vector4f vec, Vector4f result) {
        result.x = x - vec.x;
        result.y = y - vec.y;
        result.z = z - vec.z;
        result.w = w - vec.w;
        return result;
    }

    /**
     *
     * <code>subtract</code> subtracts the provided values from this vector,
     * creating a new vector that is then returned.
     *
     * @param subtractX
     *            the x value to subtract.
     * @param subtractY
     *            the y value to subtract.
     * @param subtractZ
     *            the z value to subtract.
     * @return the result vector.
     */
    public Vector4f subtract(float subtractX, float subtractY, float subtractZ, float subtractW) {
        return new Vector4f(x - subtractX, y - subtractY, z - subtractZ, w - subtractW);
    }

    /**
     * <code>subtractLocal</code> subtracts the provided values from this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls.
     *
     * @param subtractX
     *            the x value to subtract.
     * @param subtractY
     *            the y value to subtract.
     * @param subtractZ
     *            the z value to subtract.
     * @return this
     */
    public Vector4f subtractLocal(float subtractX, float subtractY, float subtractZ, float subtractW) {
        x -= subtractX;
        y -= subtractY;
        z -= subtractZ;
        w -= subtractW;
        return this;
    }

    /**
     * <code>normalize</code> returns the unit vector of this vector.
     *
     * @return unit vector of this vector.
     */
    public Vector4f normalize() {
        float length = length();
        if (length != 0) {
            return divide(length);
        } else {
            return divide(1);
        }
    }

    /**
     * <code>normalizeLocal</code> makes this vector into a unit vector of
     * itself.
     *
     * @return this.
     */
    public Vector4f normalizeLocal() {
        float length = length();
        if (length != 0) {
            return divideLocal(length);
        } else {
            return divideLocal(1);
        }
    }

    /**
     * <code>zero</code> resets this vector's data to zero internally.
     */
    public void zero() {
        x = y = z = w = 0;
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from this to the finalVec
     * this=(1-changeAmnt)*this + changeAmnt * finalVec
     * @param finalVec The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from this towards finalVec
     */
    public void interpolate(Vector4f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*this.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*this.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*this.z + changeAmnt*finalVec.z;
        this.w=(1-changeAmnt)*this.w + changeAmnt*finalVec.w;
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from beginVec to finalVec
     * this=(1-changeAmnt)*beginVec + changeAmnt * finalVec
     * @param beginVec the beging vector (changeAmnt=0)
     * @param finalVec The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginVec towards finalVec
     */
    public void interpolate(Vector4f beginVec,Vector4f finalVec, float changeAmnt) {
        this.x=(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*beginVec.z + changeAmnt*finalVec.z;
        this.w=(1-changeAmnt)*beginVec.w + changeAmnt*finalVec.w;
    }

    /**
     * Check a vector... if it is null or its floats are NaN or infinite,
     * return false.  Else return true.
     * @param vector the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValidVector(Vector4f vector) {
      if (vector == null) return false;
      if (Float.isNaN(vector.x) ||
              Float.isNaN(vector.y) ||
              Float.isNaN(vector.w) ||
          Float.isNaN(vector.z)) return false;
      if (Float.isInfinite(vector.x) ||
              Float.isInfinite(vector.y) ||
              Float.isInfinite(vector.w) ||
          Float.isInfinite(vector.z)) return false;
      return true;
    }


    /**
     * <code>clone</code> creates a new Vector4f object containing the same
     * data as this one.
     *
     * @return the new Vector4f
     */
    public Object clone() {
        try{
            Vector4f result = (Vector4f) super.clone();
            
            return result;
            }
            
            catch(CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
            return null;
    }

    /**
     * Saves this Vector4f into the given float[] object.
     * @param floats The float[] to take this Vector4f
     * @return The floats[] after saving.
     */
    public float[] toArray(float[] floats) {
        floats[0]=x;floats[1]=y;floats[2]=z;floats[3]=w;
        return floats;
    }

    /**
     * are these two vectors the same? they are is they both have the same x,y,
     * and z values.
     *
     * @param o
     *            the object to compare for equality
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Vector4f) || o == null) { return false; }

        if (this == o) { return true; }

        Vector4f comp = (Vector4f) o;
        if (x != comp.x) return false;
        if (y != comp.y) return false;
        if (z != comp.z) return false;
        if (w != comp.w) return false;
        return true;
    }

    /**
     * <code>hashCode</code> returns a unique code for this vector object based
     * on it's values. If two vectors are logically equivalent, they will return
     * the same hash code value.
     * @return the hash code value of this vector.
     */
    public int hashCode() {
        int hash = 7;
        hash += 31 * hash + Float.floatToIntBits(x);
        hash += 31 * hash + Float.floatToIntBits(y);
        hash += 31 * hash + Float.floatToIntBits(z);
        hash += 31 * hash + Float.floatToIntBits(w);
        return hash;
    }

    /**
     * <code>toString</code> returns the string representation of this vector.
     * The format is:
     *
     * org.jme.math.Vector4f [X=XX.XXXX, Y=YY.YYYY, Z=ZZ.ZZZZ]
     *
     * @return the string representation of this vector.
     */
    public String toString() {
        return "com.jme.math.Vector4f [X=" + x + ", Y=" + y + ", Z=" + z +", W=" + w + "]";
    }


    /**
     * Used with serialization.  Not to be called manually.
     * @param in
     * @throws IOException
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException {
        x=in.readFloat();
        y=in.readFloat();
        z=in.readFloat();
        w=in.readFloat();
    }

    /**
     * Used with serialization.  Not to be called manually.
     * @param out
     * @throws IOException
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(w);
    }
}
